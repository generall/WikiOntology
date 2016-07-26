import java.io.StringWriter

import org.jgrapht.ext.{DOTExporter, IntegerNameProvider, VertexNameProvider}
import org.jgrapht.graph.{DefaultEdge, SimpleDirectedGraph};

val g = new SimpleDirectedGraph[(String, String) , DefaultEdge](classOf[DefaultEdge])
g.addVertex(("a", "a"))
g.addVertex(("b", "b"))
g.addVertex(("c", "c"))
g.addEdge(("a", "a"), ("c", "c"))
g
object NameProvider extends VertexNameProvider[(String, String) ]{
  override def getVertexName(vertex: (String, String)): String = vertex._1
}
val e = new DOTExporter[(String, String), DefaultEdge](
  new IntegerNameProvider[(String, String)],
  NameProvider,
  null
)
val buf =new StringWriter();
e.export(buf, g)
buf.toString
