package ml.generall.ontology.base

import gremlin.scala._

/**
  * Created by generall on 17.07.16.
  */
object GraphClient extends TitanConnection with GraphClientInterface{

  val graph = connect().asScala
  val Category = Key[String]("category")

  def getSuperNodes(x:Vertex):List[Vertex] = {
    graph.V(x).out.toList
  }

  def getSubNodes(x:Vertex):List[Vertex] = {
    graph.V(x).in.toList
  }

  override def getSuperNodes(x:VertexAdapter):List[GremlinVertex] = {
    (x match {
      case GremlinVertex(vertex) => getSuperNodes(vertex)
      case adapter => graph.V.has(Category, adapter.category).out.toList()
    }).map(v => new GremlinVertex(v))
  }

  override def getSubNodes(x:VertexAdapter):List[GremlinVertex] = {
    (x match {
      case GremlinVertex(vertex) => getSubNodes(vertex)
      case adapter => graph.V.has(Category, adapter.category).in.toList()
    }).map(v => new GremlinVertex(v))
  }


  override def getByCategory(cat: String):GremlinVertex = {
    val vertex = graph.V.has(Category, cat).head
    new GremlinVertex(vertex)
  }

}
