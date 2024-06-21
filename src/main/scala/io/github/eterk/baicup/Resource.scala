package io.github.eterk.baicup

import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets

object ResourceUtil {
  val getResource: String => String = (path: String) => {
    require(path.startsWith("/"))
    if (getClass.getResource("/") == null) {
      new File(path.tail).getPath
    } else {
      getClass.getResource(path).getFile
    }
  }

  import java.nio.file.{Paths, Files, StandardOpenOption}

  def write(content: String, path: String): Unit = {
    val filePath = Paths.get(path)
    val bytes = content.getBytes(StandardCharsets.UTF_8)
    Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
  }

  def readFile(fileName: String, encode: String): String = {
    val source = scala.io.Source.fromFile(fileName, encode)
    val res = source.mkString
    source.close()
    res
  }

}

object Resource {

  import ResourceUtil._

  lazy val iconImage = new File((getResource("/static/ico.ico")))
  lazy val lastConf: ControlParam = ControlParam.read(getResource("/last_config.json")).repair()
  lazy val defaultConf: ControlParam = ControlParam.read(getResource("/static/default_config.json")).repair()
  lazy val fileTypes: FileTypes = FileTypes.read(getResource("/static/file_types.json"))




  //  def write(content: String, path: String): Unit = {
  //
  //    // 创建一个文件对象，用于写入数据
  //    val file = new File(path)
  //    // 创建一个打印流对象，用于输出数据
  //    val pw = new PrintWriter(file, StandardCharsets.UTF_8)
  //    // 将正文内容写入文件
  //    pw.write(content)
  //    // 关闭打印流对象
  //    pw.close()
  //  }

  // 定义一个函数，接受一个文件名的序列，返回一个字符串
  def concatFiles(fileNames: Seq[String], encode: String): String = {
    // 创建一个空的字符串构建器
    val sb = new StringBuilder()
    // 遍历每个文件名
    for (fileName <- fileNames) {
      // 使用scala.io.Source从文件中读取内容，指定编码为utf-8
      val str = readFile(fileName, encode)
      sb.append(str)
    }
    // 返回字符串构建器的结果
    sb.toString()
  }

}
