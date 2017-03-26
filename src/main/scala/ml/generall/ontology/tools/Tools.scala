package ml.generall.ontology.tools

import java.io._

/**
  * Created by generall on 19.03.17.
  */
object Tools {
  def time[R](block: => R, msg: String = ""): R = {
    val t0 = System.currentTimeMillis()
    val result = block    // call-by-name
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) + s"ms $msg" )
    result
  }
  def save[T <: Serializable](fileName: String, serializable: T): Unit = {
    val baos = new FileOutputStream(fileName)
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(serializable)
    oos.close()
  }

  def load[T](fileName: String): T = {
    val ois = new ObjectInputStream(new FileInputStream(fileName))
    val res = ois.readObject.asInstanceOf[T]
    ois.close()
    res
  }

  def load[T](stream: InputStream): T = {
    val ois = new ObjectInputStream(stream)
    val res = ois.readObject.asInstanceOf[T]
    ois.close()
    res
  }
}
