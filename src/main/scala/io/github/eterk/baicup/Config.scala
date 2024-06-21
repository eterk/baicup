package io.github.eterk.baicup

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

object Config {
  val needTypes: Seq[String] = Seq(
    ".jpeg",
    ".jpg",
    ".zip",
    ".png", // 图像文件格式

    ".wav", // 音频文件格式
    ".mp3", // 音频文件格式
    ".mp4", // 视频文件格式
    ".zip", // 压缩文件格式
    ".txt", // 文本文件格式
    ".csv", // 文本文件格式
    ".xml", // 文本文件格式
    ".gif", // 图像文件格式
    ".apk"
    //          ".bat" // 批处理文件格式
  ).distinct

  val pic = Seq(
    ".jpeg",
    ".jpg",
    ".png", // 图像文件格式
    ".gif"
  )
  val doc = Seq(
    ".pdf", // 文档文件格式
    ".docx", // 文档文件格式
    ".xlsx", // 电子表格文件格式
    ".txt", // 文本文件格式
    ".csv", // 文本文件格式
    ".xml" // 文本文件格式
  )
  val media = Seq(
    ".wav", // 音频文件格式
    ".mp3", // 音频文件格式
    ".mp4" // 视频文件格式
  )
  val software = Seq(
    ".apk"
  )

  val context = Map(
    "pic" -> pic,
    "doc" -> doc,
    "media" -> media,
    "software" -> software
  )
  val proto =
    FileTypes(context)
}

case class FileTypes(map: Map[String, Seq[String]]){
  def seq: Seq[String] =map.values.flatten.toSeq
}

object FileTypes extends JSONReadWrite[FileTypes] {

  override val decoder: Decoder[FileTypes] = deriveDecoder[FileTypes]
  override val encoder: Encoder[FileTypes] = deriveEncoder[FileTypes]
  override val proto: FileTypes = Config.proto
}