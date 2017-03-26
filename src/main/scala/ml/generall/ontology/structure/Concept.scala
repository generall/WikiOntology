package ml.generall.ontology.structure

import ml.generall.ontology.base.SqliteClient

/**
  * Created by generall on 26.07.16.
  */
case class Concept(_url: String) {
  val url: String = _url.replace("http://dbpedia.org/resource/", "")

  var ontology: Traversal = _

  val categories: List[String] = loadRootCategories(url)

  def loadRootCategories(url: String): List[String] = {
    SqliteClient.getCategoriesPerConcept(url)
  }
}
