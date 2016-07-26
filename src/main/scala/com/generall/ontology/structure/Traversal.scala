package com.generall.ontology.structure

import java.io.StringWriter

import org.jgrapht.DirectedGraph
import org.jgrapht.ext.{DOTExporter, IntegerNameProvider, VertexNameProvider}
import org.jgrapht.graph.{DefaultEdge, SimpleDirectedGraph}
import scala.collection.JavaConverters._


import scala.collection.mutable

/**
  * Created by generall on 17.07.16.
  */
class Traversal () {
  val graph: DirectedGraph[Node, DefaultEdge] = new SimpleDirectedGraph[Node, DefaultEdge](classOf[DefaultEdge])

  val nodes = new mutable.HashMap[String, Node]().withDefaultValue(EmptyNode)

  object NameProvider extends VertexNameProvider[Node]{
    override def getVertexName(vertex: Node): String = vertex.toString
  }

  def removeNode(node: Node) = {
    nodes.remove(node.category)
    graph.removeVertex(node)
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

  def op(that: Traversal)(operation: (Double, Double) => Double ):Traversal =
  {
    val res = new Traversal
    (this.nodes.keys ++ that.nodes.keys)
      .toList
      .distinct
      .foreach(key =>{
        val thisNode = this.nodes(key)
        val thatNode = that.nodes(key)
        val newWeight = operation(thisNode.weight, thatNode.weight)
        val newId = if(thisNode.id > thatNode.id) thisNode.id else thatNode.id
        val node = new Node( newId, key )
        node.weight = newWeight
        res.graph.addVertex(node)
        res.nodes(key) = node
      })

    this.graph.edgeSet().asScala.foreach(edge => {
      val fromNode = res.nodes(this.graph.getEdgeSource(edge).category)
      val toNode = res.nodes(this.graph.getEdgeTarget(edge).category)
      res.graph.addEdge(fromNode, toNode)
    })

    that.graph.edgeSet().asScala.foreach(edge => {
      val fromNode = res.nodes(this.graph.getEdgeSource(edge).category)
      val toNode = res.nodes(this.graph.getEdgeTarget(edge).category)
      res.graph.addEdge(fromNode, toNode)
    })

    res
  }
}
