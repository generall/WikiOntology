import com.generall.ontology.base.GraphClient
import com.generall.ontology.structure.{Traversal, Node, Concept, TraversalGremlinFactory}
import org.scalatest.{FlatSpec, Matchers}

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

    //println(common_context.toDot)
  }

  "graphClientSpec" should "retrieve vertices by categories and return categories" in {
    val client = new GraphClient
    val subCats = client.getSubNodes("http://dbpedia.org/resource/Category:Technology_WikiProjects")

    assert(subCats.nonEmpty)
  }


  "operationTest" should "test operation on dummy data" in {
    val nodeA1 = new Node(1, "cat1")
    val nodeA2 = new Node(2, "cat2")
    val nodeA3 = new Node(3, "cat3")

    nodeA1.weight = 1.0
    nodeA2.weight = 2.0
    nodeA3.weight = 0.5

    val nodeB1 = new Node(4, "cat2")
    val nodeB2 = new Node(5, "cat3")
    val nodeB3 = new Node(6, "cat4")

    nodeB1.weight = 0.3
    nodeB2.weight = 1.0
    nodeB3.weight = 0.6

    val travA = new Traversal
    travA.nodes(nodeA1.category) = nodeA1
    travA.nodes(nodeA2.category) = nodeA2
    travA.nodes(nodeA3.category) = nodeA3

    travA.graph.addVertex(nodeA1)
    travA.graph.addVertex(nodeA2)
    travA.graph.addVertex(nodeA3)

    travA.graph.addEdge(nodeA1, nodeA2)
    travA.graph.addEdge(nodeA2, nodeA3)

    val travB = new Traversal
    travB.nodes(nodeB1.category) = nodeB1
    travB.nodes(nodeB2.category) = nodeB2
    travB.nodes(nodeB3.category) = nodeB3

    travB.graph.addVertex(nodeB1)
    travB.graph.addVertex(nodeB2)
    travB.graph.addVertex(nodeB3)

    travB.graph.addEdge(nodeB1, nodeB2)
    travB.graph.addEdge(nodeB1, nodeB3)
    travB.graph.addEdge(nodeB2, nodeB3)

    val common1 = travA intersect travB
    val common2 = travB intersect travA

    val commonDiff = (common1.nodes.values.toList.sortBy(x => x.category) zip common2.nodes.values.toList.sortBy(x => x.category)).filter(pair => {
      pair._1.category != pair._2.category
    })

    assert(commonDiff.isEmpty)

  }

}
