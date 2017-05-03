package ml.generall.ontology.base

object Config {
  val DATA_DIR="/home/generall/runable/titan-1.0.0-hadoop1/db/berkeley"
  val PROP_CATEGORY="category"
  val PROB_LEVEL="level"
  val DATA_PATH: String = sys.env.getOrElse("DATA_PATH", "/home/generall/data/dbpedia/ontology/")
  val CONCEPT_MAPPING_BASE = s"jdbc:sqlite:${DATA_PATH}art_to_cat.db"
  val GRAPH_TSV = s"${DATA_PATH}clustered_graph_no_loops.tsv"
  val redirects_file = sys.env.getOrElse("REDIRECTS_FILENAME", "redirects.tsv")
  val REMAPPING_TSV = s"${DATA_PATH}${redirects_file}"
  //val REMAPPING_TSV = s"${DATA_PATH}redirects_fake.tsv"
  val GRAPH_CACHE = "/tmp/graph_cache.bin"

}
