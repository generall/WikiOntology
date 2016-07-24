package com.generall.ontology.structure

import java.io.StringWriter

import org.jgrapht.DirectedGraph
import org.jgrapht.ext.{IntegerNameProvider, DOTExporter, VertexNameProvider}
import org.jgrapht.graph.{SimpleDirectedGraph, DefaultEdge}

import scala.collection.mutable

/**
  * Created by generall on 17.07.16.
  */
class Traversal () {
  var roots: List[Node] = Nil
  val graph: DirectedGraph[Node, DefaultEdge] = new SimpleDirectedGraph[Node, DefaultEdge](classOf[DefaultEdge])

  val nodes = new mutable.HashMap[String, Node]().withDefaultValue(null)

  object NameProvider extends VertexNameProvider[Node]{
    override def getVertexName(vertex: Node): String = vertex.toString
  }

  def toDot: String = {
    val e = new DOTExporter[Node, DefaultEdge](
      new IntegerNameProvider[Node],
      NameProvider,
      null
    )
    val buf =new StringWriter()
    e.export(buf, graph)
    buf.toString
  }
}
