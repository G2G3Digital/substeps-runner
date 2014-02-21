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
class SubstepsRunner(val substepFiles: java.util.Set[Reader], val featureFiles: java.util.List[Reader], val codedStepBasePackages: java.util.Set[String]) {

  var featureFileParses : Seq[Feature] = Seq()

  val substepRepository = new SubstepRepository

  def prepareForExecution() {

    for(substepFile <- substepFiles; substep <- new SubstepsFileParser().parseOrFail(substepFile)) {

      substepRepository.add(substep)
    }
    for(codedSubstep <- CodedSubstepLoader.loadStepImplementations(codedStepBasePackages.toSet)) {

      substepRepository.add(codedSubstep)
    }
    featureFileParses = featureFiles.map(new FeatureFileParser(substepRepository).parseOrFail(_))
  }

  def run()  = {

     featureFileParses.foldLeft[RunResult](RunResult.NoneRun)((b, a) => b.combine(a.run()))
  }


}