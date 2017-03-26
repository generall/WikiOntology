import ml.generall.ontology.base.{ContextGraphClient, GraphClient, InMemoryGraphClient, SimpleVertex}
import ml.generall.ontology.structure.{Concept, Node, Traversal, TraversalFactory}
import ml.generall.ontology.tools.Tools
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.io.File

/**
  * Created by generall on 17.07.16.
  */
class TraversalSpec extends FlatSpec with Matchers {

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) / 1000000 + "ms")
    result
  }

  val factory = new TraversalFactory(InMemoryGraphClient)
  //println(diff.toDot)

  def operationTest() = {
    val traversal1 = factory.constructConcept(List("http://dbpedia.org/resource/Category:IPhone"))

    val traversal2 = factory.constructConcept(List("http://dbpedia.org/resource/Category:Buses"))


    //val traversal = factory.construct(List("9th_Edition"))

    //factory.extendTraversal(traversal, "Windows_XP")

    factory.removeUnderweightNodes(traversal1)
    factory.removeUnderweightNodes(traversal2)

    val diff = traversal1.op(traversal2)(
      (thisNodes, thatNodes) => (thisNodes.keySet ++ thatNodes.keySet).toSet)((x, y) => {
      //if (x > factory.threshold && y > factory.threshold) 0.0 else 1.0
      List(x, y).min
    })

    factory.removeUnderweightNodes(diff)

    diff.nodes.values.toList.sortBy(-_.weight).foreach(println)

  }

  "conceptSpec" should "load concept`s categories" in {
    val scala = new Concept("http://dbpedia.org/resource/Scala_(programming_language)")

    val kotlin = new Concept("http://dbpedia.org/resource/Kotlin_(programming_language)")

    val groovy = new Concept("http://dbpedia.org/resource/Groovy_(programming_language)")

    val traversalScala = factory.constructConcept(scala.categories, 0.4)
    val traversalKotlin = factory.constructConcept(kotlin.categories, 0.4)
    val traversalGroovy = factory.constructConcept(groovy.categories, 0.4)

    val diff = traversalScala sub traversalKotlin

    factory.removeUnderweightNodes(diff)

    diff.getLeafs().foreach(x => println(x.category))

    File("scala.dot").writeAll(traversalScala.toDot.replaceAll("http://dbpedia.org/resource/Category:", ""))
    File("kotlin.dot").writeAll(traversalScala.toDot.replaceAll("http://dbpedia.org/resource/Category:", ""))
    File("diff.dot").writeAll(diff.toDot.replaceAll("http://dbpedia.org/resource/Category:", ""))

    println(diff.toDot)
  }

  "confSpec" should "generate dot files for conference" in {
    val c1 = List("http://dbpedia.org/resource/Category:Mathematics", "http://dbpedia.org/resource/Category:Education")
    val c2 = List("http://dbpedia.org/resource/Category:Computer_science", "http://dbpedia.org/resource/Category:Geometry")
    val c3 = List("http://dbpedia.org/resource/Category:Agriculture", "http://dbpedia.org/resource/Category:Cattle_breeds")

    val traversalC1 = factory.constructConcept(c1, 0.1)
    val traversalC2 = factory.constructConcept(c2, 0.1)
    val traversalC3 = factory.constructConcept(c3, 0.1)

    val int1 = traversalC1 intersect traversalC2
    val int2 = traversalC1 intersect traversalC3
    val int3 = traversalC2 intersect traversalC3

    factory.removeUnderweightNodes(int1)
    factory.removeUnderweightNodes(int2)
    factory.removeUnderweightNodes(int3)


    println("Mathematics and Computer_science: ", int1.getLeafs().map(x => x.weight).sum)
    println("Mathematics and Cattle_breeds: ", int2.getLeafs().map(x => x.weight).sum)
    println("Computer_science and Cattle_breeds: ", int3.getLeafs().map(x => x.weight).sum)

    println(int1.toDot.replaceAll("http://dbpedia.org/resource/Category:", ""))
    println(int2.toDot.replaceAll("http://dbpedia.org/resource/Category:", ""))
    println(int3.toDot.replaceAll("http://dbpedia.org/resource/Category:", ""))


  }

  "contextSpec" should "generate context ontology from category" in {

    // delete from context http://dbpedia.org/resource/Category:WikiProjects

    val wiki_projects_context = time {
      factory.constructContext(List("http://dbpedia.org/resource/Category:Technology_WikiProjects"))
    }

    println("Nodes count: " ++ wiki_projects_context.nodes.size.toString)

    val programing_language_context = time {
      factory.constructContext(List("http://dbpedia.org/resource/Category:Programming_languages"))
    }

    println("Nodes count: " ++ programing_language_context.nodes.size.toString)

    val common_context = time {
      programing_language_context sub wiki_projects_context
    }

    //println(common_context.toDot)
  }

  "contextClientSpec" should "Create graph client from previous graph traversal" in {
    val wiki_projects_context = time {
      factory.constructContext(List("http://dbpedia.org/resource/Category:Technology_WikiProjects"))
    }

    val contextClient = new ContextGraphClient(wiki_projects_context)

    val contextFactory = new TraversalFactory(contextClient)

    val scala = new Concept("http://dbpedia.org/resource/Scala_(programming_language)")

    val traversalScala = contextFactory.constructConcept(scala.categories)

    contextFactory.removeUnderweightNodes(traversalScala)

    println(traversalScala.toDot)

  }

  "graphClientSpec" should "retrieve vertices by categories and return categories" in {
    val client = GraphClient
    val subCats = client.getSubNodes(SimpleVertex("http://dbpedia.org/resource/Category:Technology_WikiProjects"))

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

  "constructConcept" should "construct something" in {
    val threshold = 0.2
    val init_cats = Concept("http://dbpedia.org/resource/Titanic_(1997_film)").categories
    val a = factory.constructConcept(init_cats, threshold)
    a.getTop(threshold).foreach(x => println(x._cat))
  }

  "constructConcept" should "be quick" in {
    val threshold = 1.0

    val vars = List(
      "http://dbpedia.org/resource/Titanic_(1997_film)",
      "http://dbpedia.org/resource/RMS_Titanic",
      "http://dbpedia.org/resource/Titanic",
      "http://dbpedia.org/resource/Iceberg_Theory",
      "http://dbpedia.org/resource/Iceberg_(fashion_house)",
      "http://dbpedia.org/resource/Iceberg",
      "http://dbpedia.org/resource/James_Cameron"
    )

    Tools.time({
      vars.map(art => {
        Tools.time(Concept(art).categories, "SQL " + art)
      }).map(cats => {
        Tools.time(factory.constructConcept(cats, threshold), "gremlin")
      })
    }, "Total time")
  }

  "graphClient" should "be quick" in {

    val cats = List("C_Best_Sound_Mixing_Academy_Award_winners",
      "C_Best_Picture_Academy_Award_winners",
      "C_Academy_Award_winners",
      "C_Pacific_Ocean",
      "C_Best_Film_Editing_Academy_Award_winners",
      "C_American_disaster_films",
      "C_Film_scores",
      "C_Best_Original_Song_Academy_Award_winning_songs",
      "C_2012_films",
      "C_Paramount_Pictures_films",
      "C_Romance_films",
      "C_American_films",
      "C_Vancouver",
      "C_Love",
      "C_3D_films_by_country",
      "C_Nova_Scotia",
      "C_IMAX_venues",
      "C_Best_Sound_Editing_Academy_Award_winners",
      "C_American_drama_films",
      "C_Films_whose_director_won_the_Best_Director_Golden_Globe")

    cats
      .map(cat => Tools.time(InMemoryGraphClient.getByCategory(cat), "getByCategory: " + cat))
      .map(vertex => vertex.map(x => Tools.time(InMemoryGraphClient.getSuperNodes(x), "getSuperNodes: " + x )) )
  }





}
