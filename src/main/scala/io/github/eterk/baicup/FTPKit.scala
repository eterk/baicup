package io.github.eterk.baicup

import java.io._
import java.net.URL
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.time.{LocalDate, LocalDateTime}
import scala.util.{Failure, Success, Try}


object FTPKit {


  // 定义一个函数，用来从一个 ftp 链接复制一个文件到一个本地路径，并重命名
  def copyFileFromFtp(ftpLink: String, localPath: String, newName: String): Unit = {
    // 创建一个 URL 对象，连接 ftp 服务器
    val url = new URL(ftpLink)
    // 创建一个 InputStream 对象，读取 ftp 服务器上的文件
    val in: InputStream = new BufferedInputStream(url.openStream())
    // 创建一个 OutputStream 对象，写入本地路径的文件
    val out: OutputStream = new BufferedOutputStream(new FileOutputStream(localPath + newName))
    // 创建一个字节数组，用来存储读取的数据
    val buffer = new Array[Byte](128)
    // 创建一个变量，用来记录读取的字节数
    var len = 0
    // 循环读取数据，直到结束
    while ( {
      len = in.read(buffer);
      len != -1
    }) {
      // 将读取的数据写入输出流
      out.write(buffer, 0, len)
    }
    // 关闭输入流和输出流
    in.close()
    out.close()
  }

  // 定义一个函数，将一个文件复制到一个目标目录
  def copyFilesToDir(file: File, dir: String): Unit = {
    // 创建一个 File 对象，表示目标目录
    val target = new File(dir)
    // 如果目标目录不存在，就创建它
    if (!target.exists()) {
      target.mkdirs()
    }
    // 获取文件名
    val fileName = file.getName
    // 创建一个 Path 对象，表示目标文件的路径
    val targetPath = Paths.get(dir + fileName)
    // 使用 Files.copy 方法，复制文件到目标目录
    Files.copy(file.toPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
    println(s"Copied $fileName to $dir")
  }


  // 定义一个函数，将一行目录信息转换成 DirInfo 对象
  private def parseFtpInfo(line: String): FTPInfo = {
    // 使用空格分割目录信息，得到一个数组
    val arr = line.split("\\s+")
    // 创建一个 DirInfo 对象，使用数组中的元素作为参数
    FTPInfo(
      permission = arr(0),
      link = arr(1).toInt,
      owner = arr(2),
      group = arr(3),
      size = arr(4).toInt,
      time = arr.slice(5, 8).mkString(" "),
      name = arr.slice(8, arr.size).mkString(" ")
    )
  }


  // 定义一个函数，从一个 ftp 链接获取目录信息，并返回一个 Map，其中键是文件类型，值是 FormatInfo
  def getSummaryByFormat(ftpLink: String): SummaryInfo = {
    // 创建一个 Map 对象，存储文件类型和 FormatInfo 的映射
    val summaryMap = scala.collection.mutable.Map[String, FormatInfo]()
    // 调用辅助函数，从根目录开始遍历
    traverseDir(ftpLink, summaryMap, "")

    // 返回一个不可变的 Map

    val newMap =
      summaryMap.map {
        case (k, v) =>
          val newV = v.copy(pathSeq = v.pathSeq.map(_.replaceFirst(ftpLink, "/")))
          k -> newV
      }.toMap

    SummaryInfo(LocalDateTime.now(), newMap)

  }


  // 定义一个辅助函数，递归地遍历 ftp 目录的所有子目录和文件，更新 Map 中的数据
  private def traverseDir(ftpLink: String,
                          summaryMap: scala.collection.mutable.Map[String, FormatInfo],
                          dir: String): Unit = {

    // 创建一个 URL 对象，连接 ftp 服务器
    val url = new URL(ftpLink + dir)


    // 创建一个 BufferedReader 对象，读取目录信息
    Try(new BufferedReader(new InputStreamReader(url.openStream()))) match {

      case Failure(exception) => println(s"visit => $url failed")
      case Success(br) =>
        var line = br.readLine()

        while (line != null) {
          val info = parseFtpInfo(line)
          info.getTpe match {
            case "DIR" => // 获取文件夹名
              // 在 summaryMap 中增加文件夹的数量
              summaryMap
                .keys
                .foreach { k =>
                  val info = summaryMap(k)
                  summaryMap(k) = info.copy(dirNum = info.dirNum + 1)
                }
              // 递归地遍历该文件夹
              traverseDir(ftpLink, summaryMap, dir + info.name + "/")
            case tpe =>
              val fileLink = ftpLink + dir + info.name
              // 在 summaryMap 中增加文件的数量，文件的大小，以及文件的路径
              val formatInfo: FormatInfo = summaryMap.getOrElse(tpe, FormatInfo(tpe, 0, 0, Seq()))
              summaryMap(tpe) = formatInfo.copy(
                fileSize = formatInfo.fileSize + info.size,
                pathSeq = formatInfo.pathSeq :+ fileLink
              )
          }

          // 读取下一行
          line = br.readLine()
        }
        // 关闭 BufferedReader 对象
        br.close()
    }
  }

  // 定义一个 saveObj 函数，用来存储 scala 对象到本地路径
  def saveObj[T](obj: T, path: String): Boolean = {
    // 使用 try-catch 语句来处理可能的异常
    try {
      // 创建一个 FileOutputStream 对象，表示要写入的文件流
      val fos = new FileOutputStream(path)
      // 创建一个 ObjectOutputStream 对象，表示要写入的对象流
      val oos = new ObjectOutputStream(fos)
      // 使用 ObjectOutputStream.writeObejct 方法，将 obj 对象写入对象流
      oos.writeObject(obj)
      // 关闭 ObjectOutputStream 对象
      oos.close()
      // 关闭 FileOutputStream 对象
      fos.close()
      // 返回 true，表示存储成功
      true
    } catch {
      // 如果发生异常，打印异常信息
      case e: Exception =>
        e.printStackTrace()
        // 返回 false，表示存储失败
        false
    }
  }

  // 定义一个 load 函数，用来读取 scala 对象从本地路径
  def load[T](path: String): Option[T] = {
    // 使用 try-catch 语句来处理可能的异常
    try {
      // 创建一个 FileInputStream 对象，表示要读取的文件流
      val fis = new FileInputStream(path)
      // 创建一个 ObjectInputStream 对象，表示要读取的对象流
      val ois = new ObjectInputStream(fis)
      // 使用 ObjectInputStream.readObject 方法，从对象流中读取一个对象，然后转换为 T 类型
      val obj = ois.readObject().asInstanceOf[T]
      // 关闭 ObjectInputStream 对象
      ois.close()
      // 关闭 FileInputStream 对象
      fis.close()
      // 返回 Some(obj)，表示读取成功
      Some(obj)
    } catch {
      // 如果发生异常，打印异常信息
      case e: Exception =>
        e.printStackTrace()
        // 返回 None，表示读取失败
        None
    }
  }


  def deleteFilesFromFTPWithBat(server: String,
                                port: Int,
                                user: String,
                                pass: String,
                                tmpDir: String,
                                suffix: String,
                                filePaths: Seq[String]): Unit = {
    import java.io._
    import scala.sys.process._

    val ftpCmdFileName = s"$tmpDir/cmd_$suffix.txt"
    val batFile = new File(s"$tmpDir/del_${suffix}.bat")
    val ftpCommandsFile = new File(ftpCmdFileName)
    val writer = new PrintWriter(batFile)

    writer.println(s"ftp -n -s:$ftpCmdFileName")
    writer.close()


    val ftpCommandsWriter =
      //      new PrintWriter(new OutputStreamWriter(new FileOutputStream(ftpCommandsFile), Charset.forName("GBK")))
      new PrintWriter(ftpCommandsFile) // 中文目录无法处理
    ftpCommandsWriter.println(s"open $server $port")
    ftpCommandsWriter.println(s"user $user $pass")
    filePaths.foreach(path => ftpCommandsWriter.println(s"delete \"$path\""))
    ftpCommandsWriter.println("quit")
    ftpCommandsWriter.close()

    //    batFile.delete()
    //    ftpCommandsFile.delete()
    s"cmd /c ${batFile.getAbsolutePath}".!
  }


  def scp(outputDir: String, ftpRoot: String, map: Map[String, FormatInfo]): Unit = {
    map
      .foreach {
        case (k, v) =>
          val output = outputDir + k + "/"
          println(output)

          val target = new File(output)
          // 如果目标目录不存在，就创建它
          if (!target.exists()) {
            target.mkdirs()
          }

          v.pathSeq
            .foreach(f => {

              val newName = PathConvert.pathToName(f)

              if (f != PathConvert.nameToPath(newName)) {
                println(" 无法还原" + f + "  " + newName)
              }


              val info = f + "   " + newName

              Try(copyFileFromFtp(ftpRoot + f, output, newName))
                .recover {
                  case e => println(info + " " + e.getMessage)
                }

            })

      }
  }

  /**
   * 扫描ftp 上的文件，将ftp 服务器中的文件，按照格式后缀，归类复制到本地，重命名为路径名+文件名。最后删除ftp 服务器中已经备份的数据
   * 需要注意的点
   * ftp 服务器一般有编码设置，需要和计算机的编码一致。否则有些汉字目录会操作失败
   * 目录中的总中文址和空格是需要处理的点
   * 有一些已知的目录可以跳过，避免将一些没有价值的数据复制过来，比如回收站
   * 可以将windows 系统设置为utf-8 / 控制面板-区域-设置utf-8，
   *
   * @param args
   */

  def main(args: Array[String]): Unit = {

    val outputDir = new File(s"s://tmp/pad_backup/${LocalDate.now()}")
    if (!outputDir.exists()) {
      println("创建")
      outputDir.mkdirs()
    }

    val (user, password) =
      "anonymous" -> "anonymous"
    //     "admin" -> "admin"

    val kit = FTPKit("192.168.2.182", 7021, user, password, outputDir)
    println(kit)
    kit.execute()
  }


}

case class FTPKit(host: String, port: Int, user: String, password: String, dest: File) {

  import FTPKit._

  private val ftpRoot: String = s"ftp://${host}:${port}/"
  private val scanRstCachePath: String = dest.getAbsolutePath + "/" + "scan_result"
  private val backupDir: String = dest.getAbsolutePath + "/" + "backup/"
  private val delTmpDir: String = dest.getAbsolutePath + "/" + "del_cmd/"
  private val createIfNotExists = (dir: String) => {
    if (!new File(dir).exists()) {
      new File(dir).mkdir()
    }
  }

  createIfNotExists(backupDir)
  createIfNotExists(delTmpDir)


  override def toString: String = {
    Seq(ftpRoot, scanRstCachePath, backupDir).mkString("\n")

  }

  def load(): SummaryInfo = {

    if (!new File(scanRstCachePath).exists()) {
      println("summary")
      val mapG: SummaryInfo = getSummaryByFormat(ftpRoot)
      saveObj(mapG, scanRstCachePath)
    }

    FTPKit.load[SummaryInfo](scanRstCachePath).get
  }

  private def del(map: Map[String, FormatInfo]): Unit = {
    val delete = deleteFilesFromFTPWithBat(host, port, user, password, delTmpDir, _, _)

    map.foreach {
      case (k, v) =>
        println("delete:" + k)
        val filesMap = v.pathSeq.zipWithIndex.groupMap(_._2 / 80)(_._1)
        filesMap.foreach(x => {
          println(x._1 + "/" + filesMap.size)
          val tmpName = k.replaceAll("\\.", "") + "_" + x._1 ++ "_" + System.nanoTime()
          delete(tmpName, x._2)
        })
      //

    }


  }

  // 定义一个 Seq，包含我列举的常用格式
  private val needTypes = Seq(
    ".jpeg",
    ".jpg",
    ".zip",
    ".png", // 图像文件格式
    //      ".pdf", // 文档文件格式
    //      ".docx", // 文档文件格式
    ".xlsx", // 电子表格文件格式
    ".wav", // 音频文件格式
    ".mp3", // 音频文件格式
    ".mp4", // 视频文件格式
    ".zip", // 压缩文件格式
    //      ".txt", // 文本文件格式
    ".csv", // 文本文件格式
    //      ".xml", // 文本文件格式
    ".gif", // 图像文件格式
    //      ".bat" // 批处理文件格式
  ).distinct


  def execute(): Unit = {


    val map = load().map

    val needPart: Map[String, FormatInfo] =
      map
        .filter(x => needTypes.contains(x._1))

    scp(backupDir, ftpRoot, needPart)

    del(needPart)
  }


}