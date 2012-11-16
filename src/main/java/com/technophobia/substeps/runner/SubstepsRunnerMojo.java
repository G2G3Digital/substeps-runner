/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps Maven Runner.
 *
 *    Substeps Maven Runner is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps Maven Runner is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.technophobia.substeps.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.report.ExecutionReportBuilder;
import com.technophobia.substeps.report.ReportData;
import com.technopobia.substeps.jmx.SubstepsJMXClient;

/**
 * Mojo to run a number SubStep features, each contained within any number of
 * executionConfigs, encapsulating the required config and setup and tear down
 * details
 * 
 * @goal run-features
 * @requiresDependencyResolution test
 * @phase integration-test
 * 
 * @configurator include-project-dependencies
 */
public class SubstepsRunnerMojo extends AbstractMojo {

    /**
     * Location of the file.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * @parameter default-value="${project.build.directory}"
     */
    private File outputDir;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter
     */
    private Properties systemProperties;

    /**
     * @parameter
     */
    private List<ExecutionConfig> executionConfigs;

    /**
     * @parameter
     */
    private final ExecutionReportBuilder executionReportBuilder = null;

    /**
     * @parameter default-value="9999"
     * @required
     */
    private int jmxPort;

    /**
     * @parameter
     */
    private final String vmArgs = null;

    private Process forkedJVMProcess = null;


    public void execute() throws MojoExecutionException, MojoFailureException {

        startMBeanJVM();

        final BuildFailureManager buildFailureManager = new BuildFailureManager();

        // TODO - now fire up the JMX Connection, run the tests in the spawned
        // VM

        executeInternal(buildFailureManager, this.executionConfigs);

        try {
            System.out.println("waiting for forked process to return");

            if (this.forkedJVMProcess != null) {
                this.forkedJVMProcess.waitFor();
            }
        } catch (final InterruptedException e) {
            // TODO
            e.printStackTrace();
        }
        System.out.println("done");
    }


    /**
     * @throws MojoExecutionException
     * 
     */
    private void startMBeanJVM() throws MojoExecutionException {
        // launch the jvm process that will contain the Substeps MBean Server
        // build up the class path based on this projects classpath

        final CountDownLatch processStarted = new CountDownLatch(1);
        final AtomicBoolean processStartedOk = new AtomicBoolean(false);

        try {
            // strings
            final List testClassPathElements = this.project
                    .getTestClasspathElements();
            final StringBuilder cpBuf = new StringBuilder();

            boolean first = true;
            for (final Object element : testClassPathElements) {

                if (!first) {
                    cpBuf.append(File.pathSeparator);
                }
                cpBuf.append((String) element);
                first = false;
            }

            final List<String> command = new ArrayList<String>();
            command.add("java");
            command.add("-Dfile.encoding=UTF-8");
            command.add("-Dcom.sun.management.jmxremote.port=" + this.jmxPort);
            command.add("-Dcom.sun.management.jmxremote.authenticate=false");
            command.add("-Dcom.sun.management.jmxremote.ssl=false");
            command.add("-Djava.rmi.server.hostname=localhost");

            if (this.vmArgs != null && !this.vmArgs.isEmpty()) {
                final String[] args = this.vmArgs.split(" ");
                for (final String arg : args) {
                    command.add(arg);
                }
            }

            command.add("-classpath");
            command.add(cpBuf.toString());
            command.add("com.technopobia.substeps.jmx.SubstepsJMXServer");

            final ProcessBuilder processBuilder = new ProcessBuilder(command);

            processBuilder.redirectErrorStream(true);

            try {
                this.forkedJVMProcess = processBuilder.start();

                final InputStream stderr = this.forkedJVMProcess
                        .getInputStream();
                final InputStreamReader isr = new InputStreamReader(stderr);
                final BufferedReader br = new BufferedReader(isr);

                final Thread t = new Thread(new Runnable() {

                    public void run() {
                        String line = null;

                        try {
                            while ((line = br.readLine()) != null) {
                                if (line.compareToIgnoreCase("awaiting the shutdown notification...") == 0) {
                                    System.out
                                            .println("mbean server process started");
                                    processStartedOk.set(true);
                                    processStarted.countDown();

                                }
                                System.out.println(" *\t\t" + line);
                            }
                        } catch (final IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } finally {
                            if (processStarted.getCount() > 0) {
                                System.out
                                        .println("spawned process didn't start fully, no further output, an error is assumed and the process will terminate");
                                processStarted.countDown();
                            }
                        }
                    }
                });
                t.start();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (final DependencyResolutionRequiredException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            System.out.println("waiting for process to start...");
            processStarted.await();

            if (!processStartedOk.get()) {
                throw new MojoExecutionException("Unable to launch VM process");
            }

            System.out.println("process started");
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void executeInternal(final BuildFailureManager buildFailureManager,
            final List<ExecutionConfig> executionConfigList)
            throws MojoFailureException {

        final ReportData data = new ReportData();

        Assert.assertNotNull("executionConfigs cannot be null",
                executionConfigList);
        Assert.assertFalse("executionConfigs can't be empty",
                executionConfigList.isEmpty());

        final SubstepsJMXClient client = new SubstepsJMXClient();

        client.init(this.jmxPort);

        for (final ExecutionConfig executionConfig : executionConfigList) {

            final List<SubstepExecutionFailure> failures = new ArrayList<SubstepExecutionFailure>();

            final ExecutionNode rootNode = runExecutionConfig(executionConfig,
                    failures, client);

            if (executionConfig.getDescription() != null) {

                rootNode.setLine(executionConfig.getDescription());
            }

            data.addRootExecutionNode(rootNode);

            buildFailureManager.sortFailures(failures);
        }

        client.shutdown();

        if (this.executionReportBuilder != null) {
            this.executionReportBuilder.buildReport(data);
        }

        if (buildFailureManager.testSuiteFailed()) {

            throw new MojoFailureException("Substep Execution failed:\n"
                    + buildFailureManager.getBuildFailureInfo());

        } else if (!buildFailureManager.testSuiteCompletelyPassed()) {
            // print out the failure string (but won't include any failures)
            getLog().info(buildFailureManager.getBuildFailureInfo());
        }
        // else - we're all good

    }


    private ExecutionNode runExecutionConfig(final ExecutionConfig theConfig,
            final List<SubstepExecutionFailure> failures,
            final SubstepsJMXClient client) {

        final ExecutionNode rootNode = client.prepareExceutionConfig(theConfig);
        // client.runExecutionConfig(theConfig);

        // final ExecutionNodeRunner runner = new ExecutionNodeRunner();

        // final ExecutionNode rootNode =
        // runner.prepareExecutionConfig(theConfig);

        // final List<SubstepExecutionFailure> localFailures = runner.run();

        final List<SubstepExecutionFailure> localFailures = client.run();

        failures.addAll(localFailures);

        return rootNode;
    }

}
