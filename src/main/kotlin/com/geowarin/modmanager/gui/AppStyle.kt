package com.geowarin.modmanager.gui

import javafx.scene.paint.Color
import tornadofx.*

class AppStyle : Stylesheet() {

  companion object {
    val tackyButton by cssclass()

    private val topColor = Color.RED
    private val rightColor = Color.DARKGREEN
    private val leftColor = Color.ORANGE
    private val bottomColor = Color.PURPLE
  }

  init {
    root {
      fontFamily = "Verdana"
      prefWidth = 1200.px
      prefHeight = 800.px
    }

    tackyButton {
      borderColor += box(topColor, rightColor, bottomColor, leftColor)
      fontSize = 20.px
    }
  }
}
