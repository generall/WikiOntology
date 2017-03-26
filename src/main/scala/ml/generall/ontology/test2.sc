import scala.collection.Searching._

val a = List(3, 1, 2)

a.sorted.search(10) match {
  case Found(_) => println("Yes")
  case _ => println("No")
}

val set = a.toSet

set.contains(3)

a.filter(_ > 2).exists(x => set.contains(10 - x))

