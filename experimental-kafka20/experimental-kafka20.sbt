name := "experimental-kafka20"

skip in publish := true

libraryDependencies ++= {
  Seq(
    "org.apache.flink" %% "flink-connector-kafka" % Common.flinkV
  )
}