package com.technophobia.substeps.runner.validators

import javax.validation.{ConstraintValidatorContext, ConstraintValidator}
import org.hibernate.validator.internal.constraintvalidators.PatternValidator
import javax.validation.constraints.Pattern

import collection.JavaConversions._

/**
 * @author rbarefield
 */
class ListPatternValidator extends ConstraintValidator[Pattern, java.util.List[String]] {

  val patternValidator = new PatternValidator

  def initialize(constraintAnnotation: Pattern): Unit = patternValidator.initialize(constraintAnnotation)

  def isValid(value: java.util.List[String], context: ConstraintValidatorContext): Boolean = {

    value match
    {
      case null => true
      case _ => value.foldLeft[Boolean](true){case (b, a) => b & (a != null && patternValidator.isValid(a, context))}
    }
  }
}
