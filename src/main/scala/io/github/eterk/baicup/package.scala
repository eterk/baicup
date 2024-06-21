package io.github.eterk

//import org.apache.logging.log4j.{LogManager, Logger}

package object baicup {

  val logger = TempLogger
  //  val logger: Logger = LogManager.getLogger("io.github.eterk")

  val endWithFS: String => String = (src: String) => {
    if (!src.endsWith("/")) {
      src + "/"
    } else {
      src
    }
  }


}

object TempLogger {

  def p(level: String)(msg: Any) = println(level + "  :" + msg)

  val warn = p("warn") _

  val error = p("error") _

  val info = p("info") _

  val trace = p("trace") _
}