package com.technophobia.substeps.implementations

import com.technophobia.substeps.model.SubSteps.{StepImplementations, Step}

@StepImplementations
class MoreDummySteps {

  @Step("AnotherDummyStep (.*)")
  def anotherDummyStep(value: String) = {


  }

}
