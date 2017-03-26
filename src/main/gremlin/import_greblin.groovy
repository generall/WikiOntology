

PATH_TO_CONFIG='conf/titan-berkeleyje.properties'
PATH_TO_GRAPH='/home/generall/data/dbpedia/ontology/clustered_graph.tsv'
//PATH_TO_GRAPH='/home/generall/data/dbpedia/ontology/ontology.tsv'
//PATH_TO_GRAPH='/home/generall/Dropbox/Sci/ml/ontology/src/test/resources/fake_ontology.tsv'

//graph = TinkerGraph.open()
graph = TitanFactory.open(PATH_TO_CONFIG)

//graph.createIndex('userId', Vertex.class) (1)

graph.tx().rollback() //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.makePropertyKey('category').dataType(String.class).make()
mgmt.buildIndex('CategoriesIndex', Vertex.class).addKey(name).buildCompositeIndex()
mgmt.commit()


g = graph.traversal()

getOrCreate = { id ->
  g.V().has('category', id).tryNext().orElseGet{ g.addV('category', id).next() }
}

n = 0

new File(PATH_TO_GRAPH).eachLine {
  if (!it.startsWith("#")){
    n += 1
    if(n % 10000 == 0){
      println n
      graph.tx().commit()
    }
    (fromVertex, toVertex) = it.split('\t').collect(getOrCreate) //(2)
    fromVertex.addEdge('broader', toVertex)
  }
}

graph.tx().commit();
