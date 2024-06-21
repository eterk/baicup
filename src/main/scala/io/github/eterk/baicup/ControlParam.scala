package io.github.eterk.baicup


import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.io.File
import java.time.LocalDateTime

case class ControlParam(host: String,
                        port: Int,
                        user: String,
                        password: String,
                        dest: String,
                        src: String,
                        needTypes: Seq[String],
                        skipPath: Seq[String],
                        delete: Boolean) {
  override def toString: String = {
    s"""
       |${if (delete) "cut" else "copy"} from ${host}:${port}${src}
       |to ${dest}
       |need ${needTypes.mkString(",")}
       |${skipPath.mkString("skip ", "\n     ", "")}
       |""".stripMargin
  }

  def repair(): ControlParam = {
    val newDest = new File(dest).getAbsolutePath + "/" +
      LocalDateTime.now().toString.replaceAll(":", "-").replaceAll("T", "_").take(19)

    val i = this
      .copy(dest = newDest)
    var newSrc = endWithFS(src)
    if (newSrc.startsWith("/")) {
      newSrc = newSrc.tail
    }
    i.copy(src = newSrc)
  }
}

object ControlParam extends JSONReadWrite[ControlParam] {


  val (user, password) = "anonymous" -> "anonymous"

  override val proto: ControlParam = ControlParam("192.168.2.182", 7021, user, password, "./backup/", "/", Seq.empty, Seq.empty, delete = false)


  override val decoder: Decoder[ControlParam] = deriveDecoder[ControlParam]
  override val encoder: Encoder[ControlParam] = deriveEncoder[ControlParam]
}
