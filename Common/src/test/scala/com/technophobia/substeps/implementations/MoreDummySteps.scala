package com.technophobia.substeps.implementations

import com.technophobia.substeps.model.SubSteps
import SubSteps.{StepImplementations, Step}

@StepImplementations
class MoreDummySteps {

  @Step("AnotherDummyStep (.*)")
  def anotherDummyStep(value: String) = {


  }

}
