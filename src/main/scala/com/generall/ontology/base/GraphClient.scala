package com.generall.ontology.base

import gremlin.scala._
import org.apache.tinkerpop.gremlin.process.traversal.P

/**
  * Created by generall on 17.07.16.
  */
class GraphClient extends TitanConnection {

  val graph = connect().asScala
  val Category = Key[String]("category")
  val Level = Key[Int]("level")

  def getSuperNodes(x:Vertex):List[Vertex] = {
    val vertex = x.asScala
    val level = vertex.value[Int]("level")
    graph.V(x).out.has(Level, P.lte(level)).toList
  }

  def getByCategory(cat: String):Vertex = {
    graph.V.has(Category, cat).head
  }

}
