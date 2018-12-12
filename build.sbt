version in ThisBuild := "0.1.7-SNAPSHOT"

organization in ThisBuild := "com.github.haoch"

scalaVersion in ThisBuild := crossScalaVersions.value.last

crossScalaVersions in ThisBuild := Seq("2.11.12", "2.12.7")

compileOrder in ThisBuild := CompileOrder.JavaThenScala

resolvers in ThisBuild ++= Seq(
  "WSO2" at "http://maven.wso2.org/nexus/content/repositories/releases/",
  "WSO2-Pub" at "http://maven.wso2.org/nexus/content/groups/wso2-public/"
)

lazy val root = project.in(file(".")).settings(
  
  name := "flink-siddhi",  
  testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a")),
  libraryDependencies ++= {

    val siddhiV = "4.2.40"
    
    Seq(
      "org.wso2.siddhi" % "siddhi-core" % siddhiV,
      "org.wso2.siddhi" % "siddhi-query-api" % siddhiV,
      "org.apache.flink" %% "flink-streaming-java" % Common.flinkV % "provided",
      "org.apache.flink" %% "flink-scala" % Common.flinkV,
      "org.apache.flink" %% "flink-runtime" % Common.flinkV % Test classifier "tests",
      "org.apache.flink" %% "flink-test-utils" % Common.flinkV % Test,
      "junit" % "junit" % "4.4" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test exclude("junit", "junit-dep")

    ) map(_.exclude("org.apache.directory.jdbm", "apacheds-jdbm1"))
  }

)

lazy val experimental = project.in(file("experimental")).dependsOn(root)
lazy val `experimental-kafka20` = project.in(file("experimental-kafka20")).dependsOn(experimental)
lazy val `experimental-kafka010` = project.in(file("experimental-kafka010")).dependsOn(experimental)
