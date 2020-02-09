package com.geowarin.modmanager

import com.geowarin.modmanager.mod.Mod
import com.geowarin.modmanager.mod.loadMods
import com.geowarin.modmanager.mod.parseModsConfig
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.Dimension.LinearUnits.px
import tornadofx.EventBus.RunOn.BackgroundThread


class MyStyle : Stylesheet() {

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
    }

    tackyButton {
      borderColor += box(topColor, rightColor, bottomColor, leftColor)
      fontSize = 20.px
    }

    viewport {
      minWidth = Dimension(2000.0, px)
      prefWidth = Dimension(2000.0, px)
    }
  }
}

class MyApp : App(MyView::class, MyStyle::class) {
  init {
    reloadStylesheetsOnFocus()
//    reloadViewsOnFocus()
  }
}

object ModsListRequest : FXEvent(BackgroundThread)

class MyController : Controller() {
  init {
    subscribe<ModsListRequest> {
      val mods = loadMods(MacPaths.steamModsFolder)

      val modsConfig = parseModsConfig(MacPaths.configFolder)
      val activeMods = mods.filter { modsConfig.activeMods.contains(it.root.name) }

      fire(ModsListEvent(mods, activeMods))
    }
  }
}

class ModsListEvent(val mods: List<Mod>, val activeMods: List<Mod>) : FXEvent()


class MyView : View("GW Mod manager") {
  override val root = vbox {
    button("Load mods") {
      addClass(MyStyle.tackyButton)
    }.action {
      fire(ModsListRequest)
    }
    hbox {
      tableview<Mod> {
        readonlyColumn("Name", Mod::cleanModName)
        readonlyColumn("Category", Mod::category)

        subscribe<ModsListEvent> { event ->
          items.setAll(event.mods)
          resizeColumnsToFitContent()
        }
      }
      tableview<Mod> {
        readonlyColumn("Name", Mod::cleanModName)
        readonlyColumn("Category", Mod::category)

        subscribe<ModsListEvent> { event ->
          items.setAll(event.activeMods)
          resizeColumnsToFitContent()
        }
      }
    }
  }
}

fun main(args: Array<String>) {
  val myController = MyController()
  launch<MyApp>(args)
}

