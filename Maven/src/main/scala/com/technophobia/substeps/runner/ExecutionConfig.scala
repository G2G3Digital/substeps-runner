package com.technophobia.substeps.runner

import java.util.Properties
import org.apache.maven.plugins.annotations.Parameter
import org.reflections.Reflections
import java.lang.reflect.Field

import collection.JavaConversions._
import javax.validation.{Valid, ConstraintViolationException, ValidatorFactory, Validation}
import javax.validation.constraints.{Size, Pattern, NotNull}
import org.apache.maven.plugin.MojoExecutionException
;

/**
 * @author rbarefield
 */
class ExecutionConfig {

  /**
   * A descriptive name for the configuration, this is used in the test
   * execution report
   */
  @NotNull @Size(min = 1, max = 200) private var description: String = null

  /**
   * If the feature or scenario has these tags, then it will be included,
   * otherwise it won’t. multiple tags are space seperated. Tags can be
   * excluded by prefixing with
   */
  private var tags: String = null

  /**
   * If a scenario (and therefore a feature) that has this tag fails to pass,
   * then the build will not fail. This is useful for scenarios where tests
   * are written and are included in a CI build in advance of completed
   * functionality, this allows the build and therefore maven releases to
   * succeed. Over the course of a project this list should be reduced as
   * confidence in the delivery grows. Format is the same for <tags>
   */
  private var nonFatalTags: String = null

  /**
   * Path to the feature file, or directory containing the feature files
   */
  @NotNull var featureFile: String = null

  /**
   * Path to directory of substep files, or a single substep file
   */
  @NotNull var subStepsFileName: String = null

  /**
   * Defaults to true, if false, Substeps will use the
   * nonStrictKeywordPrecedence to look for alternate expressions if an exact
   * match can’t be found. Useful for porting Cucumber features.
   */
  @NotNull private var strict: Boolean = true

  /**
   * If true any parse errors will fail the build immediately, rather than
   * attempting to execute as much as possible and fail those tests that can’t
   * be parsed
   */
  @NotNull private var fastFailParseErrors: Boolean = true

  private var systemProperties: Properties = null

  /**
   * Required if strict is false. An parameter list of keywords to use if an
   * exact match can’t be found. eg. <param>Given</param> <param>When</param>
   * ... Then if a step was defined in a feature or substep as “When I login”,
   * but implemented as “Given I login”, the feature would parse correctly.
   */
  private var nonStrictKeywordPrecedence: java.util.List[String] = null

  /**
   * List of packages containing classes with step implementations eg
   * <param>com.technophobia.substeps<param>
   */
  @NotNull @Size(min = 1) @Pattern(regexp = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*")
  var stepImplementationPackages: java.util.List[String] = null

  /**
   * Ordered list of classes containing setup and tear down methods eg
   * <param>com.technophobia.substeps.MySetup<param>. By default the
   * initialisation classes associated with the step implementations will be
   * used.
   */
  private var initialisationClass: java.util.List[String] = null

}
