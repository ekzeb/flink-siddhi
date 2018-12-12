package com.github.haoch.experimental

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.formats.json.JsonRowDeserializationSchema
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer010}
import org.apache.flink.streaming.siddhi.control.ControlEventSchema

object CEPPipeline010 {

  import CEPPipeline._

  def main(args: Array[String]): Unit = {

    val params = parseParams(args)

    // create a Kafka streaming source consumer for Kafka 0.10.x
    val dataStream = new FlinkKafkaConsumer010(params.getRequired("input-topic"),
      new JsonRowDeserializationSchema(new RowTypeInfo(dataSchemaTypes, dataSchemaFields)), params.getProperties)

    val controlStream = new FlinkKafkaConsumer010(params.getRequired("control-topic"),
      new ControlEventSchema(), params.getProperties)

    // create a Kafka producer for Kafka 0.10.x
    val kafkaProducer = new FlinkKafkaProducer010(params.getRequired("output-topic"),
      new SimpleStringSchema, params.getProperties)

    pipeline(
      params,
      dataStream,
      controlStream,
      kafkaProducer,
      "Kafka 0.10 Example"
    )
  }
}