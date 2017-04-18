package ml.generall.ontology.tools

/**
  * Created by generall on 18.04.17.
  */
object ProbTools {

  implicit def d2opt[T](t: T): Option[T] = Option(t)

  def softMaxNormalizationCoef[T](list: Iterable[T])(implicit num: Numeric[T]): Double = {
    list.foldLeft(0.0)((sum, x) => sum + Math.exp(num.toDouble(x)))
  }

  def normalizationCoef[T](list: Iterable[T])(implicit num: Numeric[T]): Double = num.toDouble(list.sum)

  def normalize[T](list: Iterable[T], coef: Option[Double] = None)(implicit num: Numeric[T]): Iterable[Double] = {
    val normCoef = coef.getOrElse(normalizationCoef(list))
    list.map(num.toDouble(_) / normCoef)
  }

  def softMax[T](list: Iterable[T], coef: Option[Double] = None)(implicit num: Numeric[T]): Iterable[Double] = {
    val normCoef = coef.getOrElse(softMaxNormalizationCoef(list))
    list.map(x => Math.exp(num.toDouble(x)) / normCoef)
  }

  def logistic(x: Double, k: Double = 1.0): Double = 2 / (1 + Math.exp(-x / k)) - 1.0

  def scaleArray(list: List[Double]): List[Double] = {
    val max = list.max
    list.map(_ / max)
  }
}
