package com.generall.ontology.structure

import com.generall.ontology.base.{GraphClientInterface, VertexAdapter}

import scala.collection.mutable

/**
  * Created by generall on 17.07.16.
  */
class TraversalFactory(graphClient: GraphClientInterface) {
  val threshold = 0.1 // Minimum delta weight per traversal

  /**
    * Constructs ontology graph for list of categories.
 *
    * @param initialCats list of initial categories, typically taken from concept
    * @param threshold minimum weight barrier
    * @return category graph
    */
  def constructConcept(initialCats: List[String], threshold: Double = threshold): Traversal = {
    val traversal = new Traversal
    val delayedMap = new mutable.HashMap[VertexAdapter, (Node, Double)]
    initialCats.foreach(x => extendTraversal(traversal, x, delayedMap, threshold))
    traversal
  }

  def constructContext(initialCats: List[String], threshold: Double = threshold): Traversal = {
    val traversal = new Traversal
    val delayedMap = new mutable.HashMap[VertexAdapter, (Node, Double)]
    initialCats.foreach(x => extendContext(traversal, x, delayedMap, threshold))
    traversal
  }

  /**
    * Add nodes to traversal from button to top starting with node named cat
    *
    * @param traversal  traversal object, contains ontology graph
    * @param cat        name of category to start traversal with
    * @param delayedMap map of nodes with has not enough weight to pass traversal
    * @return traversal object
    */
  def extendTraversal(
                       traversal: Traversal,
                       cat: String,
                       delayedMap: mutable.HashMap[VertexAdapter, (Node, Double)] = null,
                       threshold: Double = threshold
                     ): Traversal = {
    val thisDelayedMap = if (delayedMap == null) new mutable.HashMap[VertexAdapter, (Node, Double)] else delayedMap
    val initialWeight = 1.0
    val vertex = graphClient.getByCategory(cat)
    if (vertex != null) growUp(traversal, vertex, initialWeight, thisDelayedMap, threshold)
    traversal
  }

  def extendContext(
                     traversal: Traversal,
                     cat: String,
                     delayedMap: mutable.HashMap[VertexAdapter, (Node, Double)] = null,
                     threshold: Double = threshold
                   ): Traversal = {
    val thisDelayedMap = if (delayedMap == null) new mutable.HashMap[VertexAdapter, (Node, Double)] else delayedMap
    val initialWeight = 1.0
    val vertex = graphClient.getByCategory(cat)
    if (vertex != null) growDown(traversal, vertex, initialWeight, thisDelayedMap, threshold)
    traversal
  }



  def growUp(traversal: Traversal,
             initVertex: VertexAdapter,
             initWeight: Double,
             delayedMap: mutable.HashMap[VertexAdapter, (Node, Double)],
             threshold: Double)
  = grow(
    traversal,
    initVertex,
    initWeight,
    delayedMap,
    threshold
  )(
    x => graphClient.getSuperNodes(x).map((x, _))
  )( (lst, oldWeight, weight) => oldWeight + weight / lst.size)

  def growDown(traversal: Traversal,
               initVertex: VertexAdapter,
               initWeight: Double,
               delayedMap: mutable.HashMap[VertexAdapter, (Node, Double)],
               threshold: Double)
  = grow(
    traversal,
    initVertex,
    initWeight,
    delayedMap,
    threshold
  )(
    x => graphClient.getSubNodes(x).map((x, _))
  )( (lst, oldWeight, weight) => 1.0 /*oldWeight + weight / lst.size*/)


  /**
    * Expansion of current traversal with adding new nodes and updating weight
 *
    * @param traversal traversal to extend
    * @param initVertex initial vertex of graph
    * @param initWeight weight of initial node
    * @param delayedMap map of nodes with have weight less then threshold
    * @param threshold minimum weight of node
    * @param retrieve strategy on expansion
    * @param weighting strategy of weighting
    */
  def grow(
            traversal: Traversal,
            initVertex: VertexAdapter,
            initWeight: Double,
            delayedMap: mutable.HashMap[VertexAdapter, (Node, Double)],
            threshold: Double
          )
          (retrieve: (VertexAdapter => List[(VertexAdapter, VertexAdapter)]))
          (weighting: ((List[(Node, VertexAdapter)], Double, Double) => Double))
  : Unit = {

    val pendingMap = new mutable.HashMap[VertexAdapter, (Node, Double)]

    val seenVertices = new mutable.HashSet[VertexAdapter]()

    val initNode = getNode(traversal, initVertex)

    var next = Option((initVertex, (initNode, initWeight)))

    while (next.isDefined) {
      val (vertex, (node, weight)) = next.get
      node.weight += weight // add traversal weight to vertex weight

      seenVertices.add(vertex)
      // Collect all neighbours-nodes of current node
      val neighbourNodes = retrieve(vertex)
        .filter(pair => !seenVertices.contains(pair._1) || !seenVertices.contains(pair._2)) // ignore loops
        .map(
        edge => {
          val (from, to) = edge
          if (from == vertex) {
            val res = (getNode(traversal, to), to)
            traversal.graph.addEdge(node, res._1)
            res
          } else {
            val res = (getNode(traversal, from), from)
            traversal.graph.addEdge(res._1, node)
            res
          }
        }
      )

      // Update weights and add pending nodes to the queue
      neighbourNodes.foreach(pair => {
        var isDelayed = false
        val (node, vertex) = pair
        val record = delayedMap.get(vertex) match {
          case Some((n: Node, w: Double)) =>
            isDelayed = true
            (node, weighting(neighbourNodes, w, weight))
          case None => pendingMap.get(vertex) match {
            case None => (node,  weighting(neighbourNodes, 0.0, weight))
            case Some((n: Node, w: Double)) => (n,  weighting(neighbourNodes, w, weight))
          }
        }
        val isBigEnough = record._2 > threshold // check weight of node
        if (isBigEnough) { // move from delayed to pending if required
          pendingMap(vertex) = record
          if (isDelayed) delayedMap.remove(vertex)
        } else { // do not move to pending
          delayedMap(vertex) = record
        }

      })
      next = pendingMap.headOption
      if (pendingMap.nonEmpty)
        pendingMap.remove(next.get._1)
    }

  }

  def removeUnderweightNodes(traversal: Traversal): Traversal = {
    traversal.nodes.values.filter(_.weight < threshold).foreach(x => traversal.removeNode(x))
    traversal
  }

  def getNode(traversal: Traversal, vertex: VertexAdapter): Node = {
    val cat = vertex.category
    var node = traversal.nodes(cat)
    node match {
      case EmptyNode =>
        node = new Node(cat.hashCode(), cat)
        traversal.nodes(cat) = node
        traversal.graph.addVertex(node)
        node
      case Node(_, _) => node
    }
  }
}
