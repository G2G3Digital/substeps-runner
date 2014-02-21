package com.technophobia.substeps.runner

import org.junit.{Assert, Test}
import com.technophobia.substeps.implementations.DummySteps

class CodedSubstepLoaderTest {

  val EXPECTED_NUMBER_OF_STEPS = 3

  @Test
  def testStepImplementationsFound() {

    val steps = CodedSubstepLoader.loadStepImplementations(Set(classOf[DummySteps].getPackage.getName))
    Assert.assertEquals(EXPECTED_NUMBER_OF_STEPS, steps.size)
  }

}
