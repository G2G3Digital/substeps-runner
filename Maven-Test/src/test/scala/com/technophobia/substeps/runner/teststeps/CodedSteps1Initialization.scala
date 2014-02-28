package com.technophobia.substeps.runner.teststeps

import com.technophobia.substeps.runner.setupteardown.Annotations

/**
 * @author rbarefield
 */
class CodedSteps1Initialization {

  @Annotations.BeforeEveryScenario
  def beforeEveryScenario() {

    CodedSteps1Initialization.changed = true
  }

}
object CodedSteps1Initialization {

  var changed = false
}