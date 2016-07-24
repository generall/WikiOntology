
//export JAVA_OPTS="$JAVA_OPTS -Xss64M"
// if stackOverFlow error


graph = TitanFactory.open('conf/titan-berkeleyje.properties');

g = graph.traversal();


ROOT_CATEGORY="http://dbpedia.org/resource/Category:Container_categories"
//ROOT_CATEGORY="5th_Edition"


// root category for all the others
root = g.V().has("category", ROOT_CATEGORY)[0]; // cachedVertex

def dfs
dfs = {n, nodeSet, currentNode, currentLevel ->
  def newLevel = currentLevel + 1;
  sz = g.V(currentNode).in().size();
  newN = n + 1;
  if(newN % 1000 == 0){
    println newN
  }
  if(newN % 10000 == 0){
    graph.tx().commit()
  }
  if(sz > 0){

    // Detect cyctes and remove
    g.V(currentNode)
      .inE()
      .filter({
          x -> nodeSet.contains(x.get().outVertex())
          }).drop().iterate();

    g.V(currentNode)
      .in()
      .filter({ x -> !x.get().values("level").hasNext() })
      .property("level", newLevel)
      .each { x -> // cachedVertex
        nodeSet.add(x);
        dfs(newN, nodeSet, x, newLevel);
        nodeSet.remove(x);
      }
  }else{
    //graph.tx().commit();
  }
}


def goUp
goUp = { currentNode, nodeSet ->
  
  g.V(currentNode)
    .outE()
    .filter({
        x -> nodeSet.contains(x.get().inVertex())
        }).drop().iterate();

  sz = g.V(currentNode).out().size();
  if (sz > 0) {
    def nextNode = g.V(currentNode).out()[0];
    nodeSet.add(nextNode);
    return goUp(nextNode, nodeSet);
  } else {
    return currentNode;
  }
}

def initial_index
initial_index = {

  g.V(root).property("level", 0);

  nset = [root].toSet();

  dfs(0, nset, root, 0);

  graph.tx().commit();
}



def make_connected
make_connected = {
  def n = 0;
  new File(PATH_TO_CATEGORIES).eachLine {
    N += 1;
    if(n % 10000 == 0){
      println n
      graph.tx().commit()
    }
    g.V().has("category", it).filter({x -> !x.get().values("level").hasNext()})
  }
}

initial_index();
