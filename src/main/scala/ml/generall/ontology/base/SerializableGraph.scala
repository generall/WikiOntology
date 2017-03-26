package ml.generall.ontology.base

import org.jgrapht.graph.{DefaultEdge, SimpleDirectedGraph}

/**
  * Created by generall on 26.03.17.
  */
class SerializableGraph[A] extends SimpleDirectedGraph[A, DefaultEdge](classOf[DefaultEdge]) with Serializable{

}
