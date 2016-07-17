
graph = TitanFactory.open('conf/titan-berkeleyje.properties');

g = graph.traversal();


//ROOT_CATEGORY="0/Container_categories"
ROOT_CATEGORY="5th_Edition"


// root category for all the others
root = g.V().has("category", ROOT_CATEGORY)[0]; // cachedVertex


g.V(root).property("level", 0)

nset = [root].toSet();


def dfs
dfs = { nodeSet, currentNode, currentLevel ->
  def newLevel = currentLevel + 1;
  sz = g.V(currentNode).in().size();
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
        dfs(nodeSet, x, newLevel);
        nodeSet.remove(x);
      }
  }else{
    graph.tx().commit();
  }
}

dfs(nset, root, 0);

graph.tx().commit();

