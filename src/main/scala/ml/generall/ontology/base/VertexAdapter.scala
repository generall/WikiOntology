package ml.generall.ontology.base

import ml.generall.ontology.structure.Node
import gremlin.scala.Vertex

/**
  * Created by generall on 31.07.16.
  */
abstract class VertexAdapter {
  def category: String
}

case class GremlinVertex( _vertex:Vertex) extends VertexAdapter{
  override def category = _vertex.value[String]("category")
  def vertex = _vertex
}

case class NodeVertex( _node:Node) extends VertexAdapter{
  override def category = _node.category
  def node = _node
}

case class SimpleVertex(cat: String) extends VertexAdapter{
  override def category: String = cat
}
