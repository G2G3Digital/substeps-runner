package com.technophobia.substeps.runner

import collection.JavaConversions._
import org.reflections.{ReflectionUtils, Reflections}
import java.lang.reflect.Method
import com.technophobia.substeps.model.SubSteps.{Step, StepImplementations}
import com.technophobia.substeps.model.CodedSubstep

object CodedSubstepLoader {

  def loadStepImplementations(basePackages : Set[String]) = {

    for{basePackage <- basePackages
        reflections = new Reflections(basePackage)
        stepImplClass <- reflections.getTypesAnnotatedWith(classOf[StepImplementations])
        method: Method <- ReflectionUtils.getAllMethods(stepImplClass, ReflectionUtils.withAnnotation(classOf[Step]));
        stepAnnotation = method.getAnnotation(classOf[Step])
    }
    yield CodedSubstep(stepAnnotation.value().r, method, stepImplClass)
  }
}