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

  def getSubNodes(x:Vertex):List[Vertex] = {
    graph.V(x).in.toList
  }

  def getSuperNodes(x:String):List[String] = {
    graph.V.has(Category, x).out.values[String]("category").toList()
  }

  def getSubNodes(x:String):List[String] = {
    graph.V.has(Category, x).in.values[String]("category").toList()
  }


  def getByCategory(cat: String):Vertex = {
    graph.V.has(Category, cat).head
  }

}
