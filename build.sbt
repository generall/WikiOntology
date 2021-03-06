
name := "ontology"
scalaVersion := "2.11.8"
version := "1.0-SNAPSHOT"

organization := "ml.generall"
val titanV = "1.0.0"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.2-incubating.2",
  "com.thinkaurelius.titan" % "titan-core" % titanV,
  "com.thinkaurelius.titan" % "titan-berkeleyje" % titanV,
  // "com.thinkaurelius.titan" % "titan-es" % titanV,
  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "org.w3" %% "banana-jena" % "0.8.1",
  "org.jgrapht" % "jgrapht-core" % "1.0.1",
  "org.jgrapht" % "jgrapht-ext" % "1.0.1",
  "org.xerial" % "sqlite-jdbc" % "3.8.11.2"
)


resolvers += Resolver.mavenLocal
