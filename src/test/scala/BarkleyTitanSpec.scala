import ml.generall.ontology.base.TitanConnection
import gremlin.scala._
import org.scalatest._

class BarkleyTitanSpec extends FlatSpec with Matchers with TitanConnection {
  "Gremlin-Scala" should "connect to Titan database and retrieve Container_categories property" in {
    val graph = connect().asScala
    val Category = Key[String]("category")

    val root = graph.V(4336).toList()(0)

    val subRoot = graph.V(root).in().toList()

    println(subRoot.size)

    subRoot.foreach(x => println(x.value[String]("category")))

    println("Titan test finish!")


    /*
    val Paths = graph.V.has(Category, "0/Container_categories").out.out.path.toList

    Paths foreach { p: Path =>
      val pathDescription = p.objects.asScala map {
        case v: Vertex => v.value[String]("category")
      } mkString " -> "
      println(pathDescription)
    } 
    */
    
    graph.close
  }
}