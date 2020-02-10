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
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import tornadofx.*
import java.awt.Desktop
import java.net.URI


class MyApp : App(MyView::class, AppStyle::class) {
  init {
    reloadStylesheetsOnFocus()
//    reloadViewsOnFocus()
  }
}

class SelectedModChangedRequest(val mod: Mod?) : FXEvent(EventBus.RunOn.BackgroundThread)
class SelectedModChangedEvent(val description: String, val imageUrl: String) : FXEvent()

class ToolbarView : View() {
  override val root = hbox {
    button("Load mods") {
      addClass(AppStyle.tackyButton)
    }.action {
      fire(ModsListRequest)
    }
  }
}

class ModListView : View() {
  val mods: FilteredList<Mod> = FilteredList(FXCollections.observableArrayList<Mod>())
  val search = SimpleStringProperty().onChange { srch ->
    mods.setPredicate { srch.isNullOrBlank() || it.cleanModName.toLowerCase().contains(srch.toLowerCase()) }
  }

  override val root = vbox {
    textfield(search)
    tableview<Mod>(mods) {
      readonlyColumn("Name", Mod::cleanModName)
      readonlyColumn("Category", Mod::categoryName)
      readonlyColumn("Priority", Mod::priority)

      hgrow = Priority.ALWAYS
      subscribe<ModsListEvent> { event ->
        val source: ObservableList<Mod> = mods.source as ObservableList<Mod>
        source.setAll(event.mods)

      }
      contextmenu {
        item("Browse on steam").action {
          selectedItem?.apply { openInBrowser("https://steamcommunity.com/sharedfiles/filedetails/?id=${steamId}") }
        }
        item("Browse mod url").action {
          selectedItem?.apply { openInBrowser(metaData.url) }
        }
      }
    }.onSelectionChange { mod ->
      fire(SelectedModChangedRequest(mod))
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
      imageview {
        preserveRatioProperty().set(true)
        setMaxSize(200.0, 200.0)
//        setPrefSize(200.0, 200.0)
        subscribe<SelectedModChangedEvent> {
          image = Image(it.imageUrl)
        }
      }
      textarea {
        useMaxWidth = true
        subscribe<SelectedModChangedEvent> {
          text = it.description
        }
      }
    }
}

class MyView : View("GW Mod manager") {
  init {
    find(ModController::class)
  }

  override fun onBeforeShow() {
    fire(ModsListRequest)
  }

  val toolbarView = find(ToolbarView::class)
  val modListView = find(ModListView::class)
  val descriptionView = find(DescriptionView::class)

  override val root = vbox {

    borderpane {
      top = toolbarView.root
      center = modListView.root
      bottom = descriptionView.root
    }
  }
}

fun main(args: Array<String>) {
  launch<MyApp>(args)
}

