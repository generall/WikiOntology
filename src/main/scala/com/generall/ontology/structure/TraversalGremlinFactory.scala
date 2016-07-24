package com.generall.ontology.structure

import com.generall.ontology.base.{Config, GraphClient}
import gremlin.scala._

import scala.collection.mutable

/**
  * Created by generall on 17.07.16.
  */
class TraversalGremlinFactory {
  val graphClient = new GraphClient
  def construct(initialCats : List[String]): Traversal = {
    val traversal = new Traversal
    initialCats.foreach(x => extendTraversal(traversal, x))
    traversal
  }

  def extendTraversal(traversal: Traversal, cat: String): Traversal = {
    extendTraversal(traversal, graphClient.getByCategory(cat))
  }

  def extendTraversal(traversal: Traversal, vertex: Vertex): Traversal = {
    val node = getNode(traversal, vertex)
    val initialWeight = 1.0
    growUp(traversal, node, vertex, initialWeight)
    traversal
  }

  def growUp(traversal: Traversal, initNode: Node, initVertex: Vertex, initWeight: Double): Unit = {

    val pendingMap = new mutable.HashMap[Vertex, (Node, Double) ]
    val seenVertices = new mutable.HashSet[Vertex]()

    var next = Option((initVertex, (initNode, initWeight)))

    while(next.isDefined){

      val (vertex, ( node, weight )) = next.get
      node.weight += weight

      seenVertices.add(vertex)
      // Collect all super-nodes of current node
      val superNodes = graphClient.getSuperNodes(vertex).map(
        vertex => {
          val node = getNode(traversal, vertex)
          (node, vertex)
        }
      ).filter( pair => !seenVertices.contains(pair._2) ) // ignore loops
      // Calculating delta weight for super-nodes

      superNodes.foreach( pair => traversal.graph.addEdge(node, pair._1) )

      val size : Double = superNodes.size
      val newDeltaWeight = weight / size
      // Update weights and add pending nodes to the queue
      superNodes.foreach( pair => {
        val (node, vertex) = pair
        pendingMap.get(vertex) match {
          case None => pendingMap(vertex) = (node, newDeltaWeight) // create new pending record
          case Some( (n : Node, w : Double) ) => pendingMap(vertex) = (n, w + newDeltaWeight) // update pending record
        }
      })
      next = pendingMap.headOption
      if(!pendingMap.isEmpty)
        pendingMap.remove(next.get._1)
    }

    // TODO: Add something about weight
    // TODO: make calculation customizable

  }

  def getNode(traversal: Traversal, vertex: Vertex): Node = {
    val cat = vertex.value[String](Config.PROP_CATEGORY)
    var node = traversal.nodes(cat)
    node match {
      case null => {
        node = new Node(vertex.hashCode(), cat)
        traversal.nodes(cat) = node
        traversal.graph.addVertex(node)
        node
      }
      case Node(_, _) => node
    }
  }
}
