package com.geowarin.modmanager

import com.geowarin.modmanager.gui.AppStyle
import com.geowarin.modmanager.gui.ModController
import com.geowarin.modmanager.gui.ModsListEvent
import com.geowarin.modmanager.gui.ModsListRequest
import com.geowarin.modmanager.mod.Mod
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.input.KeyCode
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.Priority
import tornadofx.*
import java.awt.Desktop
import java.net.URI
import javax.swing.text.TableView

class MyApp : App(MyView::class, AppStyle::class)

class SelectedModChangedRequest(val mod: Mod?) : FXEvent(EventBus.RunOn.BackgroundThread)
class SelectedModChangedEvent(val description: String, val imageUrl: String) : FXEvent()

class ToolbarView : View() {
  override val root = menubar {
    menu("File") {

    }
  }
}

class ModListView : View() {
//  val mods: FilteredList<Mod> = FilteredList(FXCollections.observableArrayList<Mod>())
//  val search = SimpleStringProperty().onChange { srch ->
//    mods.setPredicate { srch.isNullOrBlank() || it.cleanModName.toLowerCase().contains(srch.toLowerCase()) }
//  }

  override val root = anchorpane {
//    textfield(search)
    val tableview = tableview<Mod> {
      readonlyColumn("Name", Mod::cleanModName).weightedWidth(weight = 70, minContentWidth = true)
      readonlyColumn("Category", Mod::categoryName).weightedWidth(20, minContentWidth = true)
      readonlyColumn("Priority", Mod::priority).weightedWidth(10, minContentWidth = true)

      anchorpaneConstraints {
        topAnchor = 0.0
        bottomAnchor = 0.0
      }
      fitToParentWidth()
      subscribe<ModsListEvent> { event ->
        items.setAll(event.mods)
        requestResize()
      }
      contextmenu {
        item("Browse on steam").action {
          selectedItem?.apply { openInBrowser("https://steamcommunity.com/sharedfiles/filedetails/?id=${steamId}") }
        }
        item("Browse mod url").action {
          selectedItem?.apply { openInBrowser(metaData.url) }
        }
        item("Open in explorer").action {
          selectedItem?.apply { Desktop.getDesktop().open(baseDir) }
        }
      }
    }
    tableview.onSelectionChange { mod ->
      fire(SelectedModChangedRequest(mod))
    }
    tableview.setOnKeyPressed { e ->
      if (e.code == KeyCode.ENTER) {
        println("enter")
      }
    }
    tableview.onDoubleClick {
      println("double")
    }
  }
}

fun openInBrowser(address: String) {
  if (!address.isBlank() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    Desktop.getDesktop().browse(URI(address))
  }
}

class DescriptionView : View() {
  override val root =
    vbox {
      useMaxWidth = true
      pane {
        setPrefSize(200.0, 200.0)
        subscribe<SelectedModChangedEvent> { e ->
          style {
            backgroundImage += URI(e.imageUrl)
            backgroundSize = multi(BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false))
            backgroundRepeat = multi(BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT)
          }
        }
      }
      webview {
        prefHeight = 200.0
        subscribe<SelectedModChangedEvent> {
          engine.loadContent("<div style='white-space: pre; font-family: Verdana; word-wrap: break-word; padding: 10px'>${it.description}</div>")
        }
      }
    }
}

class MyView : View("GW Mod manager") {
  init {
    find(ModController::class)
    // for dev only
    fire(ModsListRequest)
  }

  override fun onBeforeShow() {
    fire(ModsListRequest)
  }

  override val root =
    borderpane {
      top = find(ToolbarView::class).root
      center = find(ModListView::class).root
      bottom = find(DescriptionView::class).root
    }
}

fun main(args: Array<String>) {
  launch<MyApp>(args)
}

