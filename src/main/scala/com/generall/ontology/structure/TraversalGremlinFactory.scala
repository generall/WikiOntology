package com.generall.ontology.structure

import com.generall.ontology.base.{Config, GraphClient}
import gremlin.scala._

import scala.collection.mutable

/**
  * Created by generall on 17.07.16.
  */
object TraversalGremlinFactory {
  val graphClient = new GraphClient
  val threshold = 0.5 // Minimum delta weight per traversal

  def construct(initialCats : List[String]): Traversal = {
    val traversal = new Traversal
    val delayedMap = new  mutable.HashMap[Vertex, (Node, Double)]
    initialCats.foreach(x => extendTraversal(traversal, x, delayedMap))
    traversal
  }

  /**
    * Add nodes to traversal from button to top starting with node named cat
    *
    * @param traversal traversal object, contains ontology graph
    * @param cat name of category to start traversal with
    * @param delayedMap map of nodes with has not enough weight to pass traversal
    * @return traversal object
    */
  def extendTraversal(
                       traversal: Traversal,
                       cat: String,
                       delayedMap: mutable.HashMap[Vertex, (Node, Double)] = null
                     ): Traversal = {
    val thisDelayedMap = if (delayedMap == null)   new mutable.HashMap[Vertex, (Node, Double)] else delayedMap
    extendTraversal(traversal, graphClient.getByCategory(cat), thisDelayedMap)
  }

  def extendTraversal(
                       traversal: Traversal,
                       vertex: Vertex,
                       delayedMap: mutable.HashMap[Vertex, (Node, Double)]
                     ): Traversal = {
    val node = getNode(traversal, vertex)
    val initialWeight = 1.0
    growUp(traversal, node, vertex, initialWeight, delayedMap)
    traversal
  }

  def growUp(
              traversal: Traversal,
              initNode: Node,
              initVertex: Vertex,
              initWeight: Double,
              delayedMap: mutable.HashMap[Vertex, (Node, Double)]
            ): Unit = {

    val pendingMap = new mutable.HashMap[Vertex, (Node, Double) ]

    val seenVertices = new mutable.HashSet[Vertex]()

    var next = Option((initVertex, (initNode, initWeight)))

    while(next.isDefined){
      val (vertex, ( node, weight )) = next.get
      node.weight += weight // add traversal weight to vertex weight

      seenVertices.add(vertex)
      // Collect all super-nodes of current node
      val superNodes = graphClient.getSuperNodes(vertex).map(
        vertex => {
          val node = getNode(traversal, vertex)
          (node, vertex)
        }
      ).filter( pair => !seenVertices.contains(pair._2) ) // ignore loops


      superNodes.foreach( pair => traversal.graph.addEdge(node, pair._1) )

      // Calculating delta weight for super-nodes
      val size : Double = superNodes.size
      val newDeltaWeight = weight / size

      // Update weights and add pending nodes to the queue
      superNodes.foreach( pair => {
        var isDelayed = false
        val (node, vertex) = pair
        val record = delayedMap.get(vertex) match {
          case Some( (n : Node, w : Double) ) =>
            isDelayed = true
            (node, w + newDeltaWeight)
          case None => pendingMap.get(vertex) match {
            case None => (node, newDeltaWeight)
            case Some( (n : Node, w : Double) ) => (n, w + newDeltaWeight)
          }
        }
        val isBigEnough = record._2 > threshold
        if(isBigEnough){
          pendingMap(vertex) = record
          if (isDelayed) delayedMap.remove(vertex)
        } else {
          delayedMap(vertex) = record
        }

      })
      next = pendingMap.headOption
      if(pendingMap.nonEmpty)
        pendingMap.remove(next.get._1)
    }

    // TODO: Add something about weight
    // TODO: make calculation customizable

  }

  def removeUnderweightNodes(traversal: Traversal): Traversal ={
    traversal.nodes.values.filter(_.weight < threshold).foreach(x => traversal.removeNode(x))
    traversal
  }

  def getNode(traversal: Traversal, vertex: Vertex): Node = {
    val cat = vertex.value[String](Config.PROP_CATEGORY)
    var node = traversal.nodes(cat)
    node match {
      case EmptyNode => {
        node = new Node(vertex.hashCode(), cat)
        traversal.nodes(cat) = node
        traversal.graph.addVertex(node)
        node
      }
      case Node(_, _) => node
    }
  }
}
