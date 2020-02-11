package com.geowarin.modmanager.gui

import tornadofx.*

class AppStyle : Stylesheet() {

  companion object {
    val fail by cssclass()
    val added by cssclass()
  }

  init {
    root {
      fontFamily = "Verdana"
      prefWidth = 1200.px
      prefHeight = 1000.px
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
