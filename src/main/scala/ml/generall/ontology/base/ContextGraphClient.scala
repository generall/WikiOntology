package ml.generall.ontology.base

import ml.generall.ontology.structure.Traversal
import scala.collection.JavaConverters._


/**
  * Created by generall on 31.07.16.
  */
class ContextGraphClient(traversal: Traversal) extends GraphClientInterface{

  override def getByCategory(cat: String): VertexAdapter = if (traversal.nodes.contains(cat)) new NodeVertex(traversal.nodes(cat)) else null

  override def getSubNodes(x: VertexAdapter): List[VertexAdapter] = x match {
    case NodeVertex(node) => traversal.graph.incomingEdgesOf(node).asScala.map(edge => {
      NodeVertex(traversal.graph.getEdgeSource(edge))
    }).toList
    case other => Nil
  }

  override def getSuperNodes(x: VertexAdapter): List[VertexAdapter] = x match {
    case NodeVertex(node) => traversal.graph.outgoingEdgesOf(node).asScala.map(edge => {
      NodeVertex(traversal.graph.getEdgeTarget(edge))
    }).toList
    case other => Nil
  }
}
