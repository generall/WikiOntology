import com.generall.ontology.structure.{Concept, TraversalGremlinFactory}
import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by generall on 17.07.16.
  */
class TraversalSpec extends FlatSpec with Matchers {

  val factory = TraversalGremlinFactory
  //println(diff.toDot)

  def operationTest() = {
    val traversal1 = factory.construct(List("http://dbpedia.org/resource/Category:IPhone"))

    val traversal2 = factory.construct(List("http://dbpedia.org/resource/Category:Buses"))


    //val traversal = factory.construct(List("9th_Edition"))

    //factory.extendTraversal(traversal, "Windows_XP")

    factory.removeUnderweightNodes(traversal1)
    factory.removeUnderweightNodes(traversal2)

    val diff = traversal1.op(traversal2)((x, y) => {
      //if (x > factory.threshold && y > factory.threshold) 0.0 else 1.0
      List(x, y).min
    })

    factory.removeUnderweightNodes(diff)

    diff.nodes.values.toList.sortBy( - _.weight).foreach(println)

  }

  "conceptSpec" should "load concept`s categories"  in {
    val scala = new Concept("http://dbpedia.org/resource/Scala_(programming_language)")

    val kotlin = new Concept("http://dbpedia.org/resource/Kotlin_(programming_language)")

    val traversalScala = factory.construct(scala.categories)
    val traversalKotlin = factory.construct(kotlin.categories)

    val diff = traversalScala.op(traversalKotlin)( (x, y) =>
      (x - y)
      //List(x,y).min
    )

    factory.removeUnderweightNodes(diff)

    println(diff.toDot)
  }

}
