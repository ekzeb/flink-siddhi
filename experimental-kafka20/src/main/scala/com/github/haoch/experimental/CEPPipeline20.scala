package com.github.haoch.experimental

import java.util

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.formats.json.JsonRowDeserializationSchema
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.streaming.siddhi.SiddhiCEP
import org.apache.flink.streaming.siddhi.control.ControlEventSchema

object CEPPipeline20 {

  def main(args: Array[String]): Unit = {

    // parse input arguments
    val params = ParameterTool.fromArgs(args)

    if (params.getNumberOfParameters < 4) {
      println("Missing parameters!\n"
        + "Usage: Kafka --input-topic <topic> --control-topic <topic> --output-topic <topic> "
        + "--bootstrap.servers <kafka brokers> "
        + "--zookeeper.connect <zk quorum> --group.id <some id> ")
      return
    }

    val dataSchemaFields:Array[String] = Array("name", "value", "timestamp", "host")
    val dataSchemaTypes:Array[TypeInformation[_]] = Array(
      BasicTypeInfo.STRING_TYPE_INFO,
      BasicTypeInfo.DOUBLE_TYPE_INFO,
      BasicTypeInfo.LONG_TYPE_INFO,
      BasicTypeInfo.STRING_TYPE_INFO
    )

    // create a Kafka streaming source consumer for Kafka 0.10.x
    val dataStream = new FlinkKafkaConsumer(params.getRequired("input-topic"),
      new JsonRowDeserializationSchema(new RowTypeInfo(dataSchemaTypes, dataSchemaFields)), params.getProperties)

    val controlStream = new FlinkKafkaConsumer(params.getRequired("control-topic"),
      new ControlEventSchema(), params.getProperties)

    // create a Kafka producer for Kafka 0.10.x
    val kafkaProducer = new FlinkKafkaProducer(params.getRequired("output-topic"),
      new SimpleStringSchema, params.getProperties)


    // MetricStreamKeyedByHost -> CQL Execution Node (Add/Modify/Delete Query) -> Alert Stream

    CEPPipeline.pipeline(
      params,
      dataStream,
      controlStream,
      kafkaProducer,
      "Kafka 0.10 Example"
    )
  }
}