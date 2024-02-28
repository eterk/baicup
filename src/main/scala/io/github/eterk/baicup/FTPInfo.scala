package io.github.eterk.baicup

// 定义一个 case class，表示文件夹的属性
case class FTPInfo(permission: String, // 文件夹的权限
                   link: Int, // 文件夹的硬链接数
                   owner: String, // 文件夹的拥有者的用户名
                   group: String, // 文件夹的所属组的名称
                   size: Int, // 文件夹的大小，单位是字节
                   time: String, // 文件夹的最后修改时间
                   name: String // 文件夹的名称
                  ) {

  private def isDir: Boolean = if (permission.startsWith("d")) true else false


  def intercept() = {

    "#DCIM#.globalTrash"

  }

  def getTpe: String = {
    if (isDir) {
      "DIR"
    } else {
      val index = {
        name.lastIndexOf(".")
      }
      if (index == -1) {
        "UNKNOWN"
      } else {
        val tpe = name.substring(index)
        if (tpe == "") {
          "UNKNOWN"
        } else {
          tpe.toLowerCase()
        }
      }


    }

  }
}