package com.generall.ontology.structure

/**
  * Created by generall on 17.07.16.
  */
case class Node(_id: Int, _cat: String) {

  def category : String = _cat
  override def toString() = "N(" ++ category ++ " (" ++ weight.toString ++ ") )"

  var weight: Double = 0.0
  val id = _id
}