package ml.generall.ontology.base

import java.sql.{DriverManager, ResultSet}

/**
  * Created by generall on 26.07.16.
  */
object SqliteClient {

  Class.forName("org.sqlite.JDBC")

  val connection = DriverManager.getConnection(Config.CONCEPT_MAPPING_BASE)

  def getCategoriesPerConcept(concept: String) : List[String] = {

    val stmt = connection.createStatement()

    // TODO: Retrieve weighting
    val sql = s"""
SELECT categories.url FROM articles
JOIN relations ON (articles.id = relations.id_art)
JOIN categories ON (relations.id_cat = categories.id)
WHERE articles.url = "${concept}" LIMIT 30
"""

    val queryResult = stmt.executeQuery(sql)

    readQueryResult(queryResult)
  }

  def readQueryResult(queryResult: ResultSet ): List[String] = {
    if(queryResult.next()) queryResult.getString("url") :: readQueryResult(queryResult) else Nil
  }

}
