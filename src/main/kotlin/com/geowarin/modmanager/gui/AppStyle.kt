package com.geowarin.modmanager.gui

import javafx.scene.paint.Color.BLUE
import javafx.scene.paint.Paint
import tornadofx.*

class AppStyle : Stylesheet() {

  companion object {
    val fail by cssclass()
    val added by cssclass()
    val dragTarget by cssclass()
  }

  init {
    root {
      fontFamily = "Verdana"
      prefWidth = 1200.px
      prefHeight = 1000.px
    }

    dragTarget {
      borderWidth += box(0.px, 0.px, 3.px, 0.px)
      borderColor += box(BLUE)
    }

    fail {
      backgroundColor += c("#FF5722", .5)
      and(selected) {
        backgroundColor += c("#0096C9", .5)
      }
    }

    added {
      backgroundColor += c("#3eff4e", 0.50)
      and(selected) {
        backgroundColor += c("#0096C9", .5)
      }
    }
  }
}
