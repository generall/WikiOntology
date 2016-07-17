package com.generall.ontology.structure

import scala.collection.mutable

/**
  * Created by generall on 17.07.16.
  */
class Traversal () {
  var root: RootNode = null
  val nodes: mutable.Map[String, Node] = new mutable.HashMap[String, Node]() withDefaultValue null
}
