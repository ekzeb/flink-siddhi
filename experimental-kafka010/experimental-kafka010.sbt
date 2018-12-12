name := "experimental-kafka010"

skip in publish := true

libraryDependencies ++= {
  Seq(
    "org.apache.flink" %% "flink-connector-kafka-0.10" % Common.flinkV
  )
}