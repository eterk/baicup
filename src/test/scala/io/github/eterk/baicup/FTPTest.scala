package io.github.eterk.baicup

import org.apache.commons.net.ftp.{FTPClient, FTPClientConfig}
import org.scalatest.funsuite.AnyFunSuiteLike

import java.net.InetAddress
import java.time.LocalDateTime

class FTPTest extends AnyFunSuiteLike {


  test("connet") {


    def connect(host: String, port: Int) = {
      val client = new FTPClient()
      client.connect(InetAddress.getByName(host), port)
      println(client.getReplyString)
      client.getReply

    }


    connect("192.168.1.14", 7021)
  }
  //  test("pare") {
  //
  //  }

  test("sync") {
    //    ".*/Android/.*",
    val param =
      //      ControlParam.proto
      //      .copy(host = "192.168.1.14")
      Resource.defaultConf
        .copy(host = "192.168.1.3")

    println(param)
    val client = FTPKit(param)
    client.execute()

    //    println(client.ignore("ftp://192.168.1.14:7021/Android/data/com.sohu.inputmethod.sogou.xiaomi/files/"))

    //    info  :visit ftp://192.168.1.14:7021/Android/
    //    info  :visit=> ftp://192.168.1.14:7021/
    //      info  :visit ftp://192.168.1.14:7021/Android/data/
    //      info  :visit=> ftp://192.168.1.14:7021/
    //      info  :visit ftp://192.168.1.14:7021/Android/data/com.sohu.inputmethod.sogou.xiaomi/
    //      info  :visit=> ftp://192.168.1.14:7021/
    //      info  :visit ftp://192.168.1.14:7021/Android/data/com.sohu.inputmethod.sogou.xiaomi/files/
  }

}
