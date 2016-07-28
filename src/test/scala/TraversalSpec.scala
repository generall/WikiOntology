import com.generall.ontology.structure.{Concept, TraversalGremlinFactory}
import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by generall on 17.07.16.
  */
class TraversalSpec extends FlatSpec with Matchers {

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) / 1000000 + "ms")
    result
  }

  val factory = TraversalGremlinFactory
  //println(diff.toDot)

  def operationTest() = {
    val traversal1 = factory.constructConcept(List("http://dbpedia.org/resource/Category:IPhone"))

    val traversal2 = factory.constructConcept(List("http://dbpedia.org/resource/Category:Buses"))


    //val traversal = factory.construct(List("9th_Edition"))

    //factory.extendTraversal(traversal, "Windows_XP")

    factory.removeUnderweightNodes(traversal1)
    factory.removeUnderweightNodes(traversal2)

    val diff = traversal1.op(traversal2)(
      (thisNodes, thatNodes) => (thisNodes.keySet ++ thatNodes.keySet).toSet) ((x, y) => {
      //if (x > factory.threshold && y > factory.threshold) 0.0 else 1.0
      List(x, y).min
    })

    factory.removeUnderweightNodes(diff)

    diff.nodes.values.toList.sortBy( - _.weight).foreach(println)

  }

  "conceptSpec" should "load concept`s categories"  in {
    val scala = new Concept("http://dbpedia.org/resource/Scala_(programming_language)")

    val kotlin = new Concept("http://dbpedia.org/resource/Kotlin_(programming_language)")

    val groovy = new Concept("http://dbpedia.org/resource/Groovy_(programming_language)")

    val traversalScala = factory.constructConcept(scala.categories)
    val traversalKotlin = factory.constructConcept(kotlin.categories)
    val traversalGroovy = factory.constructConcept(groovy.categories)

    val diff = traversalScala sub traversalKotlin

    factory.removeUnderweightNodes(diff)

    diff.getLeafs().foreach(x => println(x.category))

    println(diff.toDot)
  }

  "contextSpec" should "generate context ontology from category" in {

    // delete from context http://dbpedia.org/resource/Category:WikiProjects

    val wiki_projects_context = time { factory.constructContext(List("http://dbpedia.org/resource/Category:Technology_WikiProjects")) }

    println( "Nodes count: " ++ wiki_projects_context.nodes.size.toString)

    val programing_language_context = time { factory.constructContext(List("http://dbpedia.org/resource/Category:Programming_languages")) }

    println( "Nodes count: " ++ programing_language_context.nodes.size.toString)

    val common_context = time { programing_language_context sub wiki_projects_context }

    println(common_context.toDot)
  }


}
