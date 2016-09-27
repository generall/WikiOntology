package ml.generall.ontology.structure

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

  val nodes: mutable.Map[String, Node]  = new mutable.HashMap[String, Node]().withDefaultValue(EmptyNode)

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

  def op(that: Traversal)
        (keysMergeOp: (mutable.Map[String, Node], mutable.Map[String, Node]) => Set[String])
        (operation: (Double, Double) => Double): Traversal = {
    val res = new Traversal

    // Set of keys available in operation result
    val keysSet = keysMergeOp(this.nodes, that.nodes)

    keysSet
      .map(key => {
        val thisNode = this.nodes(key)
        val thatNode = that.nodes(key)
        val newWeight = operation(thisNode.weight, thatNode.weight)
        val newId = if (thisNode.id > thatNode.id) thisNode.id else thatNode.id
        val node = new Node(newId, key)
        node.weight = newWeight
        res.graph.addVertex(node)
        res.nodes(key) = node
        // get all edges from both graphs outgoing from currect
        val thisEdges = thisNode match {
          case EmptyNode => List()
          case Node(_, _) => this.graph.outgoingEdgesOf(thisNode).asScala.map(edge => {
            (key, this.graph.getEdgeTarget(edge).category)
          })
        }
        val thatEdges = thatNode match {
          case EmptyNode => List()
          case Node(_, _) => that.graph.outgoingEdgesOf(thatNode).asScala.map(edge => {
            (key, that.graph.getEdgeTarget(edge).category)
          })
        }
        thisEdges ++ thatEdges
      })
      .flatMap(x => x)
      .filter(pair => keysSet.contains(pair._2))
      .foreach(edge => {
        val fromNode = res.nodes(edge._1)
        val toNode = res.nodes(edge._2)
        res.graph.addEdge(fromNode, toNode)
      })
    res
  }

  def sub(that: Traversal): Traversal= {
    this.op(that)( (this_nodes, that_nodes) => this_nodes.keySet.toSet ) ((x,y) => x - y)
  }

  def intersect(that: Traversal): Traversal = {
    this.op(that)( (this_nodes, that_nodes) => this_nodes.keySet.intersect(that_nodes.keySet).toSet)( (x, y) => List(x, y).min )
  }

  def getLeafs(): List[Node] = {
    val leafs = graph.vertexSet().asScala.filter(node => {
      graph.inDegreeOf(node) == 0
    }).toList
    leafs
  }

  def getTop(threshold: Double): List[Node] = {
    nodes.values.filter( node => {
      node.weight >= threshold
    }).toList
  }
}
