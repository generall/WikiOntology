package ml.generall.ontology.structure

import ml.generall.ontology.base.SqliteClient

/**
  * Created by generall on 26.07.16.
  */
case class Concept(_url: String) {
  val url = _url

  var ontology: Traversal = null

  val categories = loadRootCategories(_url)

  def loadRootCategories(url: String) = {
    SqliteClient.getCategoriesPerConcept(url)
  }
}
