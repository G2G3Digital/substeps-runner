package com.technophobia.substeps.runner

import java.io.File

import com.technophobia.substeps.parsing.ParseFailureException
import com.technophobia.substeps.services.SubstepsSession
import collection.JavaConversions._
import com.technophobia.substeps.domain.events.DomainEventSubscriber

/**
 * @author rbarefield
 */
class SubstepsRunner(val substepFiles: Set[File], val featureFiles: List[File], val codedStepBasePackages: Set[String], val subscribers: Set[DomainEventSubscriber]) {

  val session = new SubstepsSession(subscribers)

  def prepareForExecution() {

    val substepClasses = CodedSubstepClassFinder.find(codedStepBasePackages)

    substepClasses.foreach(a => session.addCodedSubsteps(a))

    substepFiles.foreach(session.addSubsteps)

    for(featureFile <- featureFiles) {

      try {
        session.addFeature(featureFile)
      } catch {

        case exception : ParseFailureException => throw new SubstepsRunnerException("Parse of feature file failed: " + exception.getMessage)
      }
    }

  }

  def run()  = session.run(List())


}
object SubstepsRunner {

  def apply(substepsFiles: java.util.Set[File], featureFiles: java.util.List[File], codedStepBasePackages: Set[String], subscribers: java.util.Set[DomainEventSubscriber]) : SubstepsRunner =
    new SubstepsRunner(substepsFiles.toSet, featureFiles.toList, codedStepBasePackages, subscribers.toSet)
}