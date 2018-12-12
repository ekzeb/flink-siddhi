package com.github.haoch.experimental

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.formats.json.JsonRowDeserializationSchema
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.streaming.siddhi.control.ControlEventSchema

object CEPPipeline20 {

  import CEPPipeline._

  def main(args: Array[String]): Unit = {

    // parse input arguments
    val params = parseParams(args)

    // create a Kafka streaming source consumer for Kafka 0.10.x
    val dataStream = new FlinkKafkaConsumer(params.getRequired("input-topic"),
      new JsonRowDeserializationSchema(new RowTypeInfo(dataSchemaTypes, dataSchemaFields)), params.getProperties)

    val controlStream = new FlinkKafkaConsumer(params.getRequired("control-topic"),
      new ControlEventSchema(), params.getProperties)

    // create a Kafka producer for Kafka 0.10.x
    val kafkaProducer = new FlinkKafkaProducer(params.getRequired("output-topic"),
      new SimpleStringSchema, params.getProperties)


    // MetricStreamKeyedByHost -> CQL Execution Node (Add/Modify/Delete Query) -> Alert Stream

    pipeline(
      params,
      dataStream,
      controlStream,
      kafkaProducer,
      "Kafka 0.10 Example"
    )
  }
}