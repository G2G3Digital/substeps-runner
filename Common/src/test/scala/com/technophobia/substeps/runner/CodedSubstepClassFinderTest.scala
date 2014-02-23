package com.technophobia.substeps.runner

import org.junit.{Assert, Test}
import com.technophobia.substeps.implementations.DummySteps

class CodedSubstepClassFinderTest {

  val EXPECTED_NUMBER_OF_CLASSES = 2

  @Test
  def testStepImplementationsFound() {

    val substepClasses = CodedSubstepClassFinder.find(Set(classOf[DummySteps].getPackage.getName))
    Assert.assertEquals(EXPECTED_NUMBER_OF_CLASSES, substepClasses.size)
  }

}
