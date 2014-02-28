package com.technophobia.substeps.runner

import com.technophobia.substeps.domain.events._
import org.apache.maven.plugin.logging.Log
import com.technophobia.substeps.domain.{Feature, CodedSubstepInvocation, WrittenSubstepInvocation, BasicScenario}
import java.text.SimpleDateFormat
import com.technophobia.substeps.domain.events.ExecutionStarted
import com.technophobia.substeps.domain.CodedSubstepInvocation
import com.technophobia.substeps.domain.events.ExecutionCompleted
import com.technophobia.substeps.domain.WrittenSubstepInvocation
import com.technophobia.substeps.domain.Feature

/**
 * @author rbarefield
 */
class MavenSubstepsLogger(logger: Log, val expectedNumberOfFeatured: Int) extends DomainEventSubscriber {

  def handle(event: SubstepsDomainEvent) {

    val timeFormat = new SimpleDateFormat("HH:mm:ss");

    var numberOfFeaturesCompleted = 0

    event match {


      case ParsingStarted(fileName, _) => logger.debug(s"Parsing of ${fileName} started")
      case ParsingSuccessful(fileName, _) => logger.info(s"Parsing of ${fileName} successful")
      case ParsingFailed(fileName, reason, _) => logger.error(s"Parsing of ${fileName} failed: ${reason}")


      case ExecutionStarted(Feature(name, _, _), time) => {

        logger.info(s"Execution of '${name}' started at ${timeFormat.format(time)} [Feature ${numberOfFeaturesCompleted + 1} of ${expectedNumberOfFeatured} started]")

      }

      case ExecutionStarted(BasicScenario(title, _, _), time) => logger.info(s"Execution of '${title}' started at ${timeFormat.format(time)}")
      case ExecutionStarted(WrittenSubstepInvocation(_, invocationLine, _), time) => logger.debug(s"Execution of '${invocationLine}' started at ${timeFormat.format(time)}")
      case ExecutionStarted(CodedSubstepInvocation(invocationLine, _), time) => logger.debug(s"Execution of '${invocationLine}' started at ${timeFormat.format(time)}")

      case ExecutionCompleted(Feature(name, _, _), runResult, time) => {

        numberOfFeaturesCompleted += 1
        logger.info(s"Execution of '${name}' completed at ${timeFormat.format(time)} with status: ${runResult} [Feature ${numberOfFeaturesCompleted} of ${expectedNumberOfFeatured} complete]")

      }

      case ExecutionCompleted(BasicScenario(title, _, _), runResult, time) => logger.info(s"Execution of '${title}' completed at ${timeFormat.format(time)} with status: ${runResult.text}")
      case ExecutionCompleted(WrittenSubstepInvocation(_, invocationLine, _), runResult, time) => logger.debug(s"Execution of '${invocationLine}' completed at ${timeFormat.format(time)} with status: ${runResult.text}")
      case ExecutionCompleted(CodedSubstepInvocation(invocationLine, _), runResult, time) => logger.debug(s"Execution of '${invocationLine}' completed at ${timeFormat.format(time)} with status: ${runResult.text}")

      case x => logger.info(s"Unmapped event occurred: ${x}")
    }

  }

}
