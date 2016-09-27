package ml.generall.ontology.base

import com.thinkaurelius.titan.core.TitanGraph

/**
  * Created by generall on 17.07.16.
  */
trait TitanConnection {
  def connect(): TitanGraph = {
    import org.apache.commons.configuration.BaseConfiguration
    val conf = new BaseConfiguration()
    conf.setProperty("storage.backend", "berkeleyje")
    conf.setProperty("storage.directory", Config.DATA_DIR)
    import com.thinkaurelius.titan.core.TitanFactory
    TitanFactory.open(conf)
  }
}
