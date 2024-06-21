package io.github.eterk.baicup

import org.scalatest.funsuite.AnyFunSuiteLike

import scala.concurrent.CancellationException
import scala.concurrent.ExecutionContext.Implicits.global

class FTPKitTest extends AnyFunSuiteLike {
  val pn = PathConvert.pathToName _
  val np = PathConvert.nameToPath _
  test("ds") {

    val path: String = "MIUI/sound_recorder#/call_ rec/姐夫( 123)_20240213172505.mp3"
    //    DCIM#.globalTrash#.mmexport1704768674187.jpg
    println(pn(path) == pn(np(pn(path))))


  }
  test("jsonr") {
    FileTypes.write(FileTypes.proto, "./target/file_types.json")
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

  test("dsa"){
    import scala.concurrent.{Future, Promise, ExecutionContext}
    import scala.util.{Success, Failure}

    // 创建一个 cancellation token
    val cancelToken = Promise[Unit]

    // 创建一个 Future 任务
    val futureTask = Future {
      // 模拟一个耗时的计算
      Thread.sleep(10000)
      println("Task completed")
    }(ExecutionContext.global)

    // 在 Future 的回调函数中检查 cancellation token 是否已经完成，如果是，就抛出一个 CancellationException
    futureTask.onComplete {
      case Success(_) =>
        if (cancelToken.isCompleted) {
          println(2)
          throw new CancellationException("Future cancelled")
        }
      case Failure(_) => // do nothing
    }(ExecutionContext.global)

    // 在 Future 的 onComplete 方法中注册一个回调函数，当 cancellation token 完成时，调用 Thread.interrupt() 来中断 Future 的执行线程
    futureTask.onComplete {
      case _ =>
        if (cancelToken.isCompleted) {
          println(1)
          Thread.currentThread().interrupt()
        }
    }(ExecutionContext.global)

    // 在某个时刻，调用 cancelToken.success(()) 来取消 Future 任务
//    cancelToken.success(())

    futureTask.foreach(x=>x)
  }


}


import scala.swing._
import scala.concurrent.{Future, Promise, ExecutionContext}
import scala.util.{Success, Failure}

// 创建一个简单的 GUI 界面，包含一个按钮和一个标签
object ButtonApp extends SimpleSwingApplication {
  // 创建一个按钮
  val button = new Button("Start/Stop")

  // 创建一个标签，用来显示程序的状态
  val label = new Label("Ready")

  // 创建一个 cancellation token，用来取消 Future 任务
  val cancelToken = Promise[Unit]

  // 创建一个 Future 任务，用来模拟一个耗时的计算
  val futureTask = Future {
    // 在另一个线程中运行
    Thread.sleep(10000)
    println("Task completed")
  }(ExecutionContext.global)

  // 在 Future 的回调函数中检查 cancellation token 是否已经完成，如果是，就抛出一个 CancellationException
  futureTask.onComplete {
    case Success(_) =>
      if (cancelToken.isCompleted) {
        throw new CancellationException("Future cancelled")
      }
    case Failure(_) => // do nothing
  }(ExecutionContext.global)

  // 在 Future 的 onComplete 方法中注册一个回调函数，当 cancellation token 完成时，调用 Thread.interrupt() 来中断 Future 的执行线程
  futureTask.onComplete {
    case _ =>
      if (cancelToken.isCompleted) {
        Thread.currentThread().interrupt()
      }
  }(ExecutionContext.global)

  // 定义一个变量，用来记录按钮的点击次数
  var clickCount = 0

  // 为按钮添加一个动作监听器，当按钮被点击时，执行相应的逻辑
  button.action = Action("Start/Stop") {
    // 点击次数加一
    clickCount += 1

    // 如果点击次数是奇数，就启动 Future 任务，并更新标签的文本
    if (clickCount % 2 == 1) {
      futureTask
      label.text = "Running"
    }

    // 如果点击次数是偶数，就取消 Future 任务，并更新标签的文本
    if (clickCount % 2 == 0) {
      cancelToken.success(())
      label.text = "Stopped"
    }
  }

  // 创建一个主面板，将按钮和标签添加到其中
  val mainPanel = new BoxPanel(Orientation.Vertical) {
    contents += button
    contents += label
  }

  // 创建一个顶层窗口，将主面板添加到其中
  def top = new MainFrame {
    title = "Button App"
    contents = mainPanel
  }
}


