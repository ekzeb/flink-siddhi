name := "experimental-kafka010"
crossScalaVersions := Seq("2.11.12", "2.12.7")
scalaVersion := crossScalaVersions.value.last
skip in publish := true

libraryDependencies ++= {
  val flinkV = "1.7.0"

  Seq(
    "org.apache.flink" %% "flink-connector-kafka-0.10" % flinkV
  )
}