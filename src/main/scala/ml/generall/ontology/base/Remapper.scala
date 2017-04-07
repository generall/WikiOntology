package ml.generall.ontology.base

import scala.collection.mutable
import scala.io.Source

/**
  * Created by generall on 07.04.17.
  */
object Remapper {

  val map = new mutable.HashMap[String, String]

  val filename = Config.REMAPPING_TSV

  for (line <- Source.fromFile(filename).getLines) {
    val articles = line.split("\\s")
    if (articles.size == 2) {
      map(articles(0)) = articles(1)
    } else {
      throw new RuntimeException("malformed input")
    }
  }

  def remap(article: String): String = map.getOrElse(article, article)
}
