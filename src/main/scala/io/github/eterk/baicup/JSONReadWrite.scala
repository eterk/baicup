package io.github.eterk.baicup

import io.circe.{Decoder, Encoder}


trait JSONReadWrite[T] {
  val decoder: Decoder[T]
  val encoder: Encoder[T]

  val proto: T

  def toJSON(o: T): String = {

    encoder(o).noSpaces
  }

  def fromJSONStr(str: String): Option[T] = {
    //    import io.circe.generic.auto._
    //    implicit val decoder: Decoder[T] = deriveDecoder[T]
    import io.circe.parser._
    decode(str)(decoder).toOption
  }

  def read(path: String): T = {

    val jsonStr: String = ResourceUtil.readFile(path, "UTF-8")
    fromJSONStr(jsonStr).getOrElse {
      logger.error(s"read json $path failed,use proto instead!")
      proto
    }
  }

  def write(o: T, path: String): Unit = {
    ResourceUtil.write(toJSON(o), path)
  }
}