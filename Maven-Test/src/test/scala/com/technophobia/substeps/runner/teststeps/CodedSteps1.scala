package com.technophobia.substeps.runner.teststeps

import com.technophobia.substeps.model.SubSteps
import SubSteps.{Step, StepImplementations}
import org.junit.Assert

/**
 * @author rbarefield
 */
@StepImplementations(requiredInitialisationClasses = Array(classOf[CodedSteps1Initialization]))
class CodedSteps1 {

  @Step("CodedSubstep")
  def codedStep() {

    Assert.assertTrue("Initialization class was not called", CodedSteps1Initialization.changed)
  }


}
