package com.generall.ontology.structure

import com.generall.ontology.base.{Config, GraphClient}
import gremlin.scala._

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
    growUp(traversal, getNode(traversal, vertex), vertex)
    traversal
  }

  def growUp(traversal: Traversal, node: Node, vertex: Vertex): Unit = {
    if (node.level == 0) return
    val superNodes = graphClient.getSuperNodes(vertex).map(
      vertex => {
        val node = getNode(traversal, vertex)
        node.subNodes = node :: node.subNodes
        (node, vertex)
      }
    )
    // TODO: Add something about weight
    node.superNodes = superNodes map(_._1)
    superNodes
      .foreach( pair => growUp(traversal, pair._1, pair._2))
  }

  def getNode(traversal: Traversal, vertex: Vertex): Node = {
    val cat = vertex.value[String](Config.PROP_CATEGORY)
    val level = vertex.value[Int](Config.PROB_LEVEL)
    val node = traversal.nodes(cat)
    node match {
      case null => {
        val node = if(level != 0) {
          new TrunkNode(vertex.hashCode(), cat, level)
        }else{
          val root = new RootNode(vertex.hashCode(), cat)
          traversal.root = root
          root
        }
        println(node)
        traversal.nodes += (( cat, node ))
        node
      }
      case RootNode(_, _) => node
      case TrunkNode(_,_,_) => node
    }
  }
}
