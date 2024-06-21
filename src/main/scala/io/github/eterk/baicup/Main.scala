package io.github.eterk.baicup

import com.formdev.flatlaf.FlatLightLaf
import io.github.eterk.elements._
import io.github.eterk.mian.input._
import io.github.eterk.mian.layout.{Div, SeqPanel}
import org.apache.commons.imaging.Imaging

import scala.swing._
import scala.util.Try


object Main extends SimpleSwingApplication {

  import scala.swing._
  // 设置 flatlaf 为当前的外观和感觉，并更新所有已经存在的组件
  FlatLightLaf.setup()

  val frame: MainFrame = new MainFrame {
    val dimension = new Dimension(1200, 600)

    title = "baicup文件备份工具"
    iconImage = Imaging.getBufferedImage(Resource.iconImage)
    contents = MainPanel(Resource.defaultConf)
    maximumSize = dimension
    minimumSize = dimension
    centerOnScreen()
  }

  //重写top方法，返回一个主窗口，包含主面板
  override def top: Frame = frame

}


case class MainPanel(initParam: ControlParam) extends BorderPanel {

  def loadConfig(param: ControlParam): Unit = {
    setRst(param)
    server.display(param.host)
    port.display(param.port)
    dest.display(param.dest)
    delete.display(param.delete)
    skipList = param.skipPath
    typs.display(param.needTypes)

  }

  def getParam(): ControlParam = {
    val del = delete.result
    val host = server.result
    val portNum = port.result

    ControlParam(host, portNum, initParam.user, initParam.password, "/DCIM", dest.result, typs.result.distinct, skipList, del)


  }

  def setRst(param: ControlParam) = {
    server.setResult(param.host)
    port.setResult(param.port)
    dest.setResult(param.dest)
    delete.setResult(param.delete)
    typs.setResult(param.needTypes)
  }

  val server = TextInput("服务器地址", 15)
  val port = IntInput("端口号", 2)
  val dest = DirFileInput("DIR", "根目录")


  //todo  第一次打开时候报错
  val config = new DirFileInput("FILE:json", "加载配置文件", 10, "选择", s => Try(loadConfig(ControlParam.read(s))))


  //    wrapper(new Button("配置占位符"))
  //    DirFileInput("FILE:json", "配置文件")
  val delete: SwitchButton = SwitchButton("删除", "是", "否")
//    val skip = LineTxtEditor("跳过的路径","浏览")
  var skipList = Seq("")

  val skip = TextLineBox("编辑跳过文件", 20, () => Seq.empty, (s: Seq[String]) => skipList = s)

  val typs = CheckBoxInput("备份的文件", Resource.fileTypes.seq, 4)

  val console =
    new Label()
//      ConsoleOutput()

  val exeButton = wrapper {
    val button =
      MyComponent(() => FTPKit(getParam()).execute())
    //
    //    val button = new Button("Execute") {
    //      val dim = new Dimension(100, 25)
    //      maximumSize = dim
    //      minimumSize = dim
    //      preferredSize = dim
    //      reactions += {
    //        case ButtonClicked(_) => {
    //          Try {
    //            getParam()
    //            MyComponent()
    //            FTPKit(getParam()).execute()
    //          } match {
    //            case Failure(exception) => System.err.println(exception.getMessage)
    //            case Success(value) => System.out.println("执行完毕")
    //          }
    //
    //        }
    //
    //      }
    //    }


    new GridPanel(1, 1) {
      contents += button
    }


  }


  //  val seq: Seq[ComponentWrapper] = Seq(wrapper(serverPort), dest, skip, delete)


  def layout2: Panel = {
    val zone1 = Div(Seq(Seq(server.component, port.component, delete.component), Seq(dest.component, skip)), Seq(Seq(0.7, 0.3, 0.1), Seq(0.7, 0.3)), Seq(0.5, 0.5))
    val zone2 = Div(Seq(Seq(config.component, exeButton.component), Seq(typs.component)), Seq(Seq(0.7, 0.3), Seq(1)), Seq(0.4, 0.6))

    Div(Seq(Seq(zone1, zone2), Seq(console)), Seq(Seq(0.4, 0.6), Seq(1)), Seq(0.4, 0.6))

  }


  private def layout1: SplitPane = {
    val seq = Seq(server, port, delete, dest)
    val r1 =
      seqPanel(seq, Seq(1, 1, 1, 1), new Dimension(10, 10), Orientation.Vertical)(SeqPanel.emptyTheme)

    val r2 = seqPanel(Seq(typs, exeButton), Seq(4, 1), new Dimension(100, 10), Orientation.Vertical)(SeqPanel.emptyTheme)

    val r12 =
      seqPanel(Seq(r1, r2), new Dimension(50, 200), Orientation.Horizontal)

    val r34 =
      seqPanel(Seq(wrapper(console)), new Dimension(50, 200), Orientation.Horizontal)
    //将版本号标签放置在北边
    //  layout(r12) = BorderPanel.Position.North

    new SplitPane(Orientation.Horizontal, r12, r34) {
      continuousLayout = true
    }

  }



  //将列表面板放置在中间
  layout(layout2) = {
    loadConfig(initParam)
    BorderPanel.Position.Center
  }
  //

}

