package ml.generall.ontology.base

import java.sql.{DriverManager, ResultSet}

/**
  * Created by generall on 26.07.16.
  */
object SqliteClient {

  Class.forName("org.sqlite.JDBC")

  val connection = DriverManager.getConnection(Config.CONCEPT_MAPPING_BASE)

  def getCategoriesPerConcept(concept: String) : List[String] = {

    // TODO: Retrieve weighting
    val sql = s"""
SELECT categories.url FROM articles
JOIN relations ON (articles.id = relations.id_art)
JOIN categories ON (relations.id_cat = categories.id)
WHERE articles.url = ? LIMIT 30
"""

    val stmt = connection.prepareStatement(sql)

    stmt.setString(1, Remapper.remap(concept))

    val queryResult = stmt.executeQuery()

    readQueryResult(queryResult)
  }

  def readQueryResult(queryResult: ResultSet ): List[String] = {
    if(queryResult.next()) queryResult.getString("url") :: readQueryResult(queryResult) else Nil
  }

}
