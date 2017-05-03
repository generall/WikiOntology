package ml.generall.ontology.structure

import ml.generall.ontology.base.SqliteClient
import ml.generall.ontology.tools.ProbTools

/**
  * Created by generall on 26.07.16.
  */
case class Concept(_url: String) {
  val url: String = _url.replace("http://dbpedia.org/resource/", "")

  var ontology: Traversal = _

  val categories: List[(String, Double)] = loadRootCategories(url)

  def loadRootCategories(url: String): List[(String, Double)] = {
    val (cats, weights) = SqliteClient.getCategoriesPerConcept(url).unzip
    if(weights.isEmpty) {
      println(s"WARN: no categories for $url")
      return Nil
    }
    cats.zip(ProbTools.scaleArray(weights.map(w => 1.0 / Math.log(1.0 / w))))
  }
}
