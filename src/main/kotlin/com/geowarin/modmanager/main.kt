package com.geowarin.modmanager

import com.geowarin.modmanager.gui.AppStyle
import com.geowarin.modmanager.gui.ModController
import com.geowarin.modmanager.gui.ModsListEvent
import com.geowarin.modmanager.gui.ModsListRequest
import com.geowarin.modmanager.mod.Mod
import javafx.scene.layout.Priority
import tornadofx.*


class MyApp : App(MyView::class, AppStyle::class) {
  init {
    reloadStylesheetsOnFocus()
//    reloadViewsOnFocus()
  }
}

class MyView : View("GW Mod manager") {
  init {
    find(ModController::class)
  }

  override val root = vbox {
    button("Load mods") {
      addClass(AppStyle.tackyButton)
    }.action {
      fire(ModsListRequest)
    }
    hbox {
      tableview<Mod> {
        readonlyColumn("Name", Mod::cleanModName)
        readonlyColumn("Category", Mod::categoryName)
        readonlyColumn("Priority", Mod::priority)

        hgrow = Priority.ALWAYS
        subscribe<ModsListEvent> { event ->
          items.setAll(event.mods)
          resizeColumnsToFitContent()
        }
      }.onSelectionChange {
        println(it)
      }
      tableview<Mod> {
        readonlyColumn("Name", Mod::cleanModName)
        readonlyColumn("Category", Mod::categoryName)
        readonlyColumn("Priority", Mod::priority)

        hgrow = Priority.ALWAYS
        subscribe<ModsListEvent> { event ->
          items.setAll(event.activeMods)
          resizeColumnsToFitContent()
        }
      }
    }
  }
}

fun main(args: Array<String>) {
  launch<MyApp>(args)
}

