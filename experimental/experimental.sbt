name := "experimental"
crossScalaVersions := Seq("2.11.12", "2.12.7")
scalaVersion := crossScalaVersions.value.last
skip in publish := true

libraryDependencies ++= {

  val flinkV = "1.7.0"
  val akkaHttpV = "10.1.5"

  Seq(
    "org.scala-lang.modules" %% "scala-xml" % "1.1.1",
    "com.typesafe.akka" %% "akka-stream" % "2.5.19",
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-xml" % akkaHttpV,
    "org.apache.flink" %% "flink-streaming-scala" % flinkV,
    "org.apache.flink" %% "flink-streaming-java" % flinkV,
    "org.apache.flink" %% "flink-connector-kafka-base" % flinkV,
    "org.apache.flink" % "flink-json" % flinkV,
  )
}