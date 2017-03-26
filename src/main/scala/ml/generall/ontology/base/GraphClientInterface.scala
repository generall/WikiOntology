package ml.generall.ontology.base

/**
  * Created by generall on 31.07.16.
  */
trait GraphClientInterface {
  def getSubNodes(x:VertexAdapter):List[VertexAdapter]
  def getSuperNodes(x:VertexAdapter):List[VertexAdapter]
  def getByCategory(cat: String):Option[VertexAdapter]
}
