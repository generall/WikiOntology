package com.generall.ontology.base

import gremlin.scala._

/**
  * Created by generall on 17.07.16.
  */
class GraphClient extends TitanConnection {

  val graph = connect().asScala
  val Category = Key[String]("category")

  def getSuperNodes(x:Vertex):List[Vertex] = {
    graph.V(x).out.toList
  }

  def getByCategory(cat: String):Vertex = {
    graph.V.has(Category, cat).head
  }

}
