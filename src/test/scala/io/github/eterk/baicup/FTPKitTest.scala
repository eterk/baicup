package io.github.eterk.baicup

import org.scalatest.funsuite.AnyFunSuiteLike

class FTPKitTest extends AnyFunSuiteLike {
  val pn = PathConvert.pathToName _
  val np = PathConvert.nameToPath _
  test("ds") {

    val path: String = "MIUI/sound_recorder#/call_ rec/姐夫( 123)_20240213172505.mp3"
    //    DCIM#.globalTrash#.mmexport1704768674187.jpg
    println(pn(path) == pn(np(pn(path))))


  }
  test("load") {
    val info = FTPKit.load[SummaryInfo]("S:\\tmp\\mobile_backup\\2024-02-25\\scan_result")

    info.get
      .map
      .values
      .toSeq
      .map(x => {
        val link = x.pathSeq
        val needEmpty =
          link.find(path => pn(path) != pn(np(pn(path))))
        if (needEmpty.isEmpty) {
          true
        } else {
          println(needEmpty.get)
        }


      })


  }


}