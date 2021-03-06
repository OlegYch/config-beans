name := "config-beans"

organization := "com.olegych"

scalaVersion := "2.9.1-1"

version := "0.0.1-SNAPSHOT"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.10"
  , "org.slf4j" % "slf4j-api" % "1.6.4"
  , "com.google.guava" % "guava" % "11.0.1"
  , "com.jsuereth" % "scala-arm_2.9.1" % "1.2"
  , "javax.transaction" % "jta" % "1.1" % "compile" //needed by scala-arm
  //  , "org.scalaz" %% "scalaz-core" % "6.0.4"
  , "ch.qos.logback" % "logback-classic" % "1.0.0" % "test"
  , "org.specs2" %% "specs2" % "1.9" % "test"
)
