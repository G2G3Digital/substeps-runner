package com.technophobia.substeps.runner

import com.technophobia.substeps.{FeatureFileParser, SubstepsFileParser}
import java.io.Reader
import com.technophobia.substeps.repositories.SubstepRepository
import com.technophobia.substeps.model.Feature
import com.technophobia.substeps.model.execution.RunResult

import collection.JavaConversions._

/**
 * @author rbarefield
 */
class SubstepsRunner(val substepFiles: Set[Reader], val featureFiles: List[Reader], val codedStepBasePackages: Set[String]) {

  var featureFileParses : Seq[Feature] = Seq()

  val substepRepository = new SubstepRepository

  def prepareForExecution() {

    for(substepFile <- substepFiles; substep <- new SubstepsFileParser().parseOrFail(substepFile)) {

      substepRepository.add(substep)
    }
    for(codedSubstep <- CodedSubstepLoader.loadStepImplementations(codedStepBasePackages.toSet)) {

      substepRepository.add(codedSubstep)
    }
    val featureFilePossibleParses = featureFiles.map(new FeatureFileParser(substepRepository).parse(_))
    val failure = featureFilePossibleParses.find(_.isInstanceOf[FeatureFileParser#Failure])
    if (failure.isDefined) throw new SubstepsRunnerException("Parse of feature file failed: " + failure.get.toString)
    featureFileParses = featureFilePossibleParses.map(_.get)
  }

  def run()  = {

     featureFileParses.foldLeft[RunResult](RunResult.NoneRun)((b, a) => b.combine(a.run()))
  }


}