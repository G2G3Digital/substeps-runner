package com.technophobia.substeps.runner

import collection.JavaConversions._
import org.reflections.Reflections
import com.technophobia.substeps.model.SubSteps
import SubSteps.StepImplementations

object CodedSubstepClassFinder {

  def find(basePackages : Set[String]): Set[Class[AnyRef]] = {

    for{basePackage <- basePackages
        reflections = new Reflections(basePackage)
        stepImplClass <- reflections.getTypesAnnotatedWith(classOf[StepImplementations])
    }
    yield stepImplClass.asInstanceOf[Class[AnyRef]]
  }
}