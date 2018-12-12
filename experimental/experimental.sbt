name := "experimental"

libraryDependencies ++= {

  val akkaHttpV = "10.1.5"

  Seq(
    "org.scala-lang.modules" %% "scala-xml" % "1.1.1",
    "com.typesafe.akka" %% "akka-stream" % "2.5.19",
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-xml" % akkaHttpV,
    "org.apache.flink" %% "flink-streaming-scala" % Common.flinkV,
    "org.apache.flink" %% "flink-streaming-java" % Common.flinkV,
    "org.apache.flink" %% "flink-connector-kafka-base" % Common.flinkV,
    "org.apache.flink" % "flink-json" % Common.flinkV,
  )
}