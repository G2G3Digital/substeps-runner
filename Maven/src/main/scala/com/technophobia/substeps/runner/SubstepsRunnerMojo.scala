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
package com.technophobia.substeps.runner

import java.io.File
import com.google.common.io.PatternFilenameFilter
import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.factory.ArtifactFactory
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.resolver.ArtifactResolver
import org.apache.maven.plugin.{MojoFailureException, AbstractMojo, MojoExecutionException}
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import org.apache.maven.project.MavenProjectBuilder

import scala.collection.JavaConversions._
import org.apache.maven.artifact.repository.ArtifactRepository
import javax.validation.Valid
import javax.validation.constraints.{Size, NotNull}
import com.technophobia.substeps.runner.validators.ValidatorFactory
import com.technophobia.substeps.domain.execution.RunResult

/**
 * Mojo to run a number SubStep features, each contained within any number of
 * executionConfigs, encapsulating the required config and setup and tear down
 * details
 */
@SuppressWarnings(Array("unchecked"))
@Mojo(name = "run-features", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.INTEGRATION_TEST, configurator = "include-project-dependencies") class SubstepsRunnerMojo extends AbstractMojo {


  //User parameters
  /**
   * See <a href="./executionConfig.html">ExecutionConfig</a>
   */
  @Parameter
  @NotNull
  @Size(min = 1)
  @Valid private var executionConfigs: java.util.List[ExecutionConfig] = null
  @Parameter
  @Valid private var stepImplementationArtifacts: java.util.List[String] = _

  //Injected parameters
  @Parameter(required = true, readonly = true, defaultValue = "${project}") private var project: MavenProject = _
  @Component private var artifactResolver: ArtifactResolver = _
  @Component private var artifactFactory: ArtifactFactory = _
  @Component private var mavenProjectBuilder: MavenProjectBuilder = _
  @Parameter(defaultValue = "${localRepository}") private var localRepository: ArtifactRepository = _
  @Parameter(readonly = true, defaultValue = "${project.remoteArtifactRepositories}") private var remoteRepositories: java.util.List[_] = _
  @Parameter(readonly = true, defaultValue = "${plugin.artifacts}") private var pluginDependencies: java.util.List[Artifact] = _
  @Component private var artifactMetadataSource: ArtifactMetadataSource = _


  def execute {

    def handle(runResult: RunResult) {

      runResult match {

        case RunResult.Failed(x) => throw new MojoFailureException("Failing substeps\n" + x.mkString("\n"))
        case RunResult.NoneRun => getLog.warn("No substeps were run")
        case RunResult.Passed => getLog.info("Substeps succeeded")
      }
    }

    validateConfiguration()

    for (executionConfig <- executionConfigs) {

      val substepFiles = loadFileOrFilesWithPattern(executionConfig.subStepsFileName, """^.*\.substeps$""").toSet
      val featureFiles = loadFileOrFilesWithPattern(executionConfig.featureFile, """^.*\.feature$""")
      val codedStepPackages = executionConfig.stepImplementationPackages
      val runner: SubstepsRunner = new SubstepsRunner(substepFiles, featureFiles.toList, codedStepPackages.toSet)
      runner.prepareForExecution()
      handle(runner.run())
    }
  }

  private def validateConfiguration() {


    val errors = ValidatorFactory.createValidator.validate(this)
    if (!errors.isEmpty) throw new MojoExecutionException(errors.map(constraint => s"${constraint.getPropertyPath} ${constraint.getMessage}").mkString("\n"))

  }

  private def loadFileOrFilesWithPattern(subStepsDirectoryOrFileName: String, pattern: String): Iterable[File] = {

    val fileOrDirectory: File = new File(subStepsDirectoryOrFileName)

    fileOrDirectory match {

      case f if f.isDirectory => f.listFiles(new PatternFilenameFilter(pattern))
      case f if f.isFile => Set(f)
      case _ => throw new MojoExecutionException(s"${fileOrDirectory} is not a file or directory")
    }
  }

}

