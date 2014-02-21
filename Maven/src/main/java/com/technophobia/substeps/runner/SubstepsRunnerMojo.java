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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.io.PatternFilenameFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;


/**
 * Mojo to run a number SubStep features, each contained within any number of
 * executionConfigs, encapsulating the required config and setup and tear down
 * details
 * 
 * @configurator include-project-dependencies
 */
@SuppressWarnings("unchecked")
@Mojo(name = "run-features", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class SubstepsRunnerMojo extends AbstractMojo {

    /**
     * See <a href="./executionConfig.html">ExecutionConfig</a>
     */
    @Parameter(required = true)
    private List<ExecutionConfig> executionConfigs;

    /**
     * List of classes containing step implementations e.g.
     * <param>com.technophobia.substeps.StepImplmentations<param>
     */
    @Parameter
    private List<String> stepImplementationArtifacts;

    @Parameter(required = true, readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Component
    private ArtifactResolver artifactResolver;

    @Component
    private ArtifactFactory artifactFactory;

    @Component
    private MavenProjectBuilder mavenProjectBuilder;

    @Parameter(defaultValue = "${localRepository}")
    private org.apache.maven.artifact.repository.ArtifactRepository localRepository;

    @Parameter(readonly = true, defaultValue = "${project.remoteArtifactRepositories}")
    private List remoteRepositories;

    @Parameter(readonly = true, defaultValue = "${plugin.artifacts}")
    private List<Artifact> pluginDependencies;

    @Component
    private ArtifactMetadataSource artifactMetadataSource;

    public void execute() throws MojoExecutionException, MojoFailureException {

        validateConfiguration();

        try {

            for(ExecutionConfig executionConfig : executionConfigs) {

                Set<Reader> substepFiles = new HashSet(loadFileOrFilesWithPattern(executionConfig.subStepsFileName, "*.substeps"));
                List<Reader> featureFiles = loadFileOrFilesWithPattern(executionConfig.featureFile, "*.feature");
                Set<String> codedStepPackages = new HashSet(Arrays.asList(executionConfig.stepImplementationPackages));
                SubstepsRunner runner = new SubstepsRunner(substepFiles, featureFiles, codedStepPackages);
                runner.prepareForExecution();
                runner.run();
            }
        } catch(Exception e) {

            throw new MojoExecutionException("Unexpected failure",e);
        }

    }

    private void validateConfiguration() throws MojoExecutionException
    {
        if(executionConfigs.isEmpty()) {
            throw new MojoExecutionException("There must be at least one executionConfig defined");
        }
    }

    private List<Reader> loadFileOrFilesWithPattern(String subStepsDirectoryOrFileName, String pattern) throws FileNotFoundException
    {
        File fileOrDirectory = new File(subStepsDirectoryOrFileName);
        List<File> files = new ArrayList<File>();
        if(fileOrDirectory.isDirectory()) {

            files.addAll(Arrays.asList(fileOrDirectory.listFiles(new PatternFilenameFilter(pattern))));

        } else if (fileOrDirectory.isFile()) {

            files.add(fileOrDirectory);
        }

        return asReaders(files);
    }

    private List<Reader> asReaders(List<File> files) throws FileNotFoundException
    {
        List<Reader> readers = new ArrayList<Reader>();
        for(File file: files) {

            readers.add(new FileReader(file));
        }

        return readers;
    }


}
