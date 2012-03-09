name := "config-beans"

organization := "com.olegych"

version := "0.0.1-SNAPSHOT"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.10"
  ,"org.specs2" %% "specs2" % "1.9-SNAPSHOT" % "test"
)