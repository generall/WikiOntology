
name := "ontology"
version := "0.1"
scalaVersion := "2.11.8"

val titanV = "1.0.0"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.0.2-incubating.2",
  "com.thinkaurelius.titan" % "titan-core" % titanV,
  "com.thinkaurelius.titan" % "titan-berkeleyje" % titanV,
  // "com.thinkaurelius.titan" % "titan-es" % titanV,
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.w3" %% "banana-jena" % "0.8.1",
  "org.jgrapht" % "jgrapht-core" % "0.9.2",
  "org.jgrapht" % "jgrapht-ext" % "0.9.2"
)


resolvers += Resolver.mavenLocal
