package com.technophobia.substeps.runner.validators

import javax.validation.{Validation, Configuration}
import java.io.ByteArrayInputStream

/**
 * @author rbarefield
 */
object ValidatorFactory {

  private val constraintMapping = <constraint-mappings
                                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                    xsi:schemaLocation="http://jboss.org/xml/ns/javax/validation/mapping validation-mapping-1.0.xsd"
                                    xmlns="http://jboss.org/xml/ns/javax/validation/mapping">

                                    <constraint-definition annotation="javax.validation.constraints.Pattern">
                                      <validated-by include-existing-validators="true">
                                        <value>com.technophobia.substeps.runner.validators.ListPatternValidator</value>
                                      </validated-by>
                                    </constraint-definition>
                                  </constraint-mappings>


  def createValidator = {

    val configuration : Configuration[_] = Validation
      .byDefaultProvider()
      .configure()
      .addMapping(new ByteArrayInputStream(constraintMapping.toString.getBytes) ).asInstanceOf[Configuration[_]]

    configuration.buildValidatorFactory().getValidator()
  }

}
