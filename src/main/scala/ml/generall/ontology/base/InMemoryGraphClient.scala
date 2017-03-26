package ml.generall.ontology.base

import java.io.File
import java.nio.file.{Files, Paths}
import java.util

import ml.generall.ontology.structure.Node
import ml.generall.ontology.tools.Tools
import org.jgrapht.{DirectedGraph, EdgeFactory, Graphs, ext}
import org.jgrapht.graph.{DefaultEdge, SimpleDirectedGraph}
import org.jgrapht.ext.{CSVFormat, CSVImporter, EdgeProvider, VertexProvider}

import collection.JavaConverters._

/**
  * Created by generall on 26.03.17.
  */
object InMemoryGraphClient extends GraphClientInterface {

  val importer = new CSVImporter[String, DefaultEdge](new VertexProvider[String] {
    override def buildVertex(label: String, attributes: util.Map[String, String]): String = label
  }, new EdgeProvider[String, DefaultEdge] {
    override def buildEdge(from: String, to: String, label: String, attributes: util.Map[String, String]): DefaultEdge = new DefaultEdge
  }, CSVFormat.EDGE_LIST, '\t')

  val graph: DirectedGraph[String, DefaultEdge] = Tools.time(if (Files.exists(Paths.get(Config.GRAPH_CACHE))) {
    Tools.load(Config.GRAPH_CACHE)
  } else {
    val g = new SerializableGraph[String]
    importer.importGraph(g, new File(Config.GRAPH_TSV))
    Tools.save(Config.GRAPH_CACHE, g)
    g
  }, "InMemoryGraph initialization")

  override def getSubNodes(x: VertexAdapter): List[VertexAdapter] = Graphs.predecessorListOf(graph, x.category).asScala.map(SimpleVertex).toList

  override def getSuperNodes(x: VertexAdapter): List[VertexAdapter] = Graphs.successorListOf(graph, x.category).asScala.map(SimpleVertex).toList

  override def getByCategory(cat: String): Option[VertexAdapter] = if (graph.containsVertex(cat)) Some(SimpleVertex(cat)) else None
}
