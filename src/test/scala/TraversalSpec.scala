import com.generall.ontology.structure.TraversalGremlinFactory
import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by generall on 17.07.16.
  */
class TraversalSpec extends FlatSpec with Matchers {

  val factory = new TraversalGremlinFactory

  val traversal = factory.construct(List("9th_Edition"))

  println(traversal.root.cat)

}
