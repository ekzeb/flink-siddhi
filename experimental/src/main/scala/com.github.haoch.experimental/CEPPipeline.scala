package com.github.haoch.experimental

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.{DataStream ⇒ ScalaStream}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumerBase, FlinkKafkaProducerBase}
import org.apache.flink.streaming.siddhi.SiddhiCEP
import org.apache.flink.streaming.siddhi.control.{ControlEvent, ControlEventSchema}
import java.util

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.formats.json.JsonRowDeserializationSchema
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction

object CEPPipeline {

  def pipeline[OUT](
    params: ParameterTool,
    inputSource: SourceFunction[OUT],
    ctrlSource: SourceFunction[ControlEvent],
    sinkFunction: SinkFunction[String],
    execName: String): Unit = {

    // parse input arguments
    // val params = ParameterTool.fromArgs(args)

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.disableSysoutLogging
    env.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000))
    // create a checkpoint every 5 seconds
    env.enableCheckpointing(5000)
    // make parameters available in the web interface
    env.getConfig.setGlobalJobParameters(params)

    /*val dataSchemaFields:Array[String] = Array("name", "value", "timestamp", "host")
    val dataSchemaTypes:Array[TypeInformation[_]] = Array(
      BasicTypeInfo.STRING_TYPE_INFO,
      BasicTypeInfo.DOUBLE_TYPE_INFO,
      BasicTypeInfo.LONG_TYPE_INFO,
      BasicTypeInfo.STRING_TYPE_INFO
    )*/

    // create a Kafka streaming source consumer for Kafka 0.10.x
    /*
    new FlinkKafkaConsumer010(params.getRequired("input-topic"),
      new JsonRowDeserializationSchema(new RowTypeInfo(dataSchemaTypes, dataSchemaFields)), params.getProperties)
     */
    val dataStream = env.addSource(inputSource)

    /*(
    new FlinkKafkaConsumer010(params.getRequired("control-topic"),
      new ControlEventSchema(), params.getProperties)
     */
    val controlStream = env.addSource(ctrlSource)

    // create a Kafka producer for Kafka 0.10.x
    /*val kafkaProducer = new FlinkKafkaProducer010(params.getRequired("output-topic"),
      new SimpleStringSchema, params.getProperties)*/

    dataStream.print()

    // MetricStreamKeyedByHost -> CQL Execution Node (Add/Modify/Delete Query) -> Alert Stream

    val alertStream = SiddhiCEP
      .define("MetricStreamKeyedByHost", dataStream.keyBy("host"), "name", "value", "timestamp", "host")
      .union("MetricStreamKeyedByName", dataStream.keyBy("name"), "name", "value", "timestamp", "host")
      .cql(controlStream)
      .returnAsMap("AlertStream")

    alertStream.map(new MapFunction[java.util.Map[String, Object], String] {
      override def map(value: util.Map[String, Object]): String = {
        value.toString
      }
    }).addSink(sinkFunction)

    env.execute(execName)
  }
}