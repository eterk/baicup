package io.github.eterk.baicup

// 定义一个 case class，表示文件类型的相关信息
case class FormatInfo(tpe: String, // 文件类型
                      dirNum: Int, //涉及的文件夹数量
                      fileSize: Int, // 总的占用存储大小
                      pathSeq: Seq[String] //涉及到的文件路径
                     ) {
  override def toString: String = {
    s"$tpe=>  dir:$dirNum fileNums:$fileNums  memoryUsage:${fileSize / 1024.0 / 1024.0}MB "
  }

  private def fileNums: Int = pathSeq.size
}