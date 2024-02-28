package io.github.eterk.baicup

object PathConvert {

  // fileName 是以/ 开头的windows 路径
  def pathToName(fileName: String): String = {
    fileName
      .replaceAll(" ", "%BLANK%")
      .replaceAll("#", "%-%")
      .replaceAll("\\/", "#")

  }

  def nameToPath(name: String): String = {
    name
      .replaceAll("%BLANK%", " ")
      .replaceAll("#", "\\/") // 还原#
      .replaceAll("%-%", "#") // 将# 换成/

  }
}