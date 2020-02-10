package com.geowarin.modmanager.gui

import javafx.scene.paint.Color
import tornadofx.*

class AppStyle : Stylesheet() {

  companion object {
    val fail by cssclass()
  }

  init {
    root {
      fontFamily = "Verdana"
      prefWidth = 1200.px
      prefHeight = 1000.px
    }

    fail{
      backgroundColor += c("#FF5722", .5)
      and(selected){
        backgroundColor += c("#0096C9", .5)
      }
    }
  }
}
