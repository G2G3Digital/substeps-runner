/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.technophobia.substeps.runner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author ricky
 */
public class ExecutionConfig {


    @Parameter(required = true)
    private String id;

    /**
     * A descriptive name for the configuration, this is used in the test
     * execution report
     */
    @Parameter(required = true)
    private String description;

    /**
     * If the feature or scenario has these tags, then it will be included,
     * otherwise it won’t. multiple tags are space seperated. Tags can be
     * excluded by prefixing with
     */
    @Parameter
    private String tags;

    /**
     * If a scenario (and therefore a feature) that has this tag fails to pass,
     * then the build will not fail. This is useful for scenarios where tests
     * are written and are included in a CI build in advance of completed
     * functionality, this allows the build and therefore maven releases to
     * succeed. Over the course of a project this list should be reduced as
     * confidence in the delivery grows. Format is the same for <tags>
     */
    @Parameter
    private String nonFatalTags;

    /**
     * Path to the feature file, or directory containing the feature files
     */
    @Parameter(required = true)
    public String featureFile;

    /**
     * Path to directory of substep files, or a single substep file
     */
    @Parameter(required = true)
    public String subStepsFileName;

    /**
     * Defaults to true, if false, Substeps will use the
     * nonStrictKeywordPrecedence to look for alternate expressions if an exact
     * match can’t be found. Useful for porting Cucumber features.
     */
    @Parameter(required = true, defaultValue = "true")
    private boolean strict = true;

    /**
     * If true any parse errors will fail the build immediately, rather than
     * attempting to execute as much as possible and fail those tests that can’t
     * be parsed
     */
    @Parameter(required = true, defaultValue = "true")
    private boolean fastFailParseErrors = true;

    @Parameter
    private Properties systemProperties;

    /**
     * Required if strict is false. An parameter list of keywords to use if an
     * exact match can’t be found. eg. <param>Given</param> <param>When</param>
     * ... Then if a step was defined in a feature or substep as “When I login”,
     * but implemented as “Given I login”, the feature would parse correctly.
     */
    @Parameter
    private String[] nonStrictKeywordPrecedence;

    /**
     * List of packages containing classes with step implementations eg
     * <param>com.technophobia.substeps<param>
     */
    @Parameter(required = true)
    public String[] stepImplementationPackages;

    /**
     * Ordered list of classes containing setup and tear down methods eg
     * <param>com.technophobia.substeps.MySetup<param>. By default the
     * initialisation classes associated with the step implementations will be
     * used.
     */
    @Parameter
    private String[] initialisationClass;

}