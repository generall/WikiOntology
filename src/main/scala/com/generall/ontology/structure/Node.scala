package com.generall.ontology.structure

/**
  * Created by generall on 17.07.16.
  */
abstract class Node(_id: Int) {
  def level() : Int
  def category() : String
  def superCats(): List[Node] = superNodes
  def subCats(): List[Node] = subNodes
  override def toString() = "Node( cat: " ++ category ++ "(" ++ level.toString ++ ") )"

  var subNodes:List[Node] = List()
  var superNodes:List[Node] = List()
  var weight: Double = _
  val id = _id
}

case class RootNode(_id: Int, cat: String) extends Node(_id){
  override def level(): Int = 0
  override def category(): String = cat
}

case class TrunkNode(_id: Int, cat: String, _level: Int) extends Node(_id){
  override def level(): Int = _level
  override def category(): String = cat
}

