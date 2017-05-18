version := "0.0.1"

scalaVersion := "2.12.1"

organization := "com.spiritedtechie"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-stream" % "2.4.16",
    "org.twitter4j" % "twitter4j-core" % "4.0.6",
    "org.twitter4j" % "twitter4j-async" % "4.0.6",
    "org.twitter4j" % "twitter4j-stream" % "4.0.6",
    "org.twitter4j" % "twitter4j-media-support" % "4.0.6"
  )
}