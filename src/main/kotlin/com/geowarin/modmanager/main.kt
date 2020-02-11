package com.geowarin.modmanager

import com.geowarin.modmanager.gui.AppStyle
import com.geowarin.modmanager.gui.ModController
import com.geowarin.modmanager.gui.ModsLoadRequest
import com.geowarin.modmanager.gui.SelectedModChangedRequest
import com.geowarin.modmanager.mod.Mod
import com.geowarin.modmanager.mod.ModStatus.ADDED_TO_MODLIST
import javafx.collections.ObservableList
import javafx.scene.control.TableRow
import javafx.scene.input.KeyCode
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.Priority
import javafx.scene.paint.Color.*
import tornadofx.*
import java.awt.Desktop
import java.net.URI

class MyApp : App(MyView::class, AppStyle::class)

class ToolbarView : View() {
  override val root = menubar {
    menu("File") {

    }
  }
}

typealias ModAction = (Mod) -> Unit

class ModListStrategy(
  val title: String,
  val modList: ObservableList<Mod>,
  val modAction: ModAction
)


class ModListFragment : Fragment() {
  //  val mods: FilteredList<Mod> = FilteredList(FXCollections.observableArrayList<Mod>())
//  val search = SimpleStringProperty().onChange { srch ->
//    mods.setPredicate { srch.isNullOrBlank() || it.cleanModName.toLowerCase().contains(srch.toLowerCase()) }
//  }
  val modListStrategy: ModListStrategy by param()
  val modController: ModController by inject()

  override val root = borderpane {
    fitToParentWidth()
    top = label(modListStrategy.title)
    val tableview = tableview(modListStrategy.modList) {
      readonlyColumn("Name", Mod::cleanModName).weightedWidth(weight = 70, minContentWidth = true)
      readonlyColumn("Category", Mod::categoryName).weightedWidth(20, minContentWidth = true).cellFormat { priority ->
        text = priority
        style { textFill = if (priority == "Unknown") RED else BLACK }
      }
      readonlyColumn("Priority", Mod::priority).weightedWidth(10, minContentWidth = true)
      readonlyColumn("Status", Mod::status).weightedWidth(10, minContentWidth = true)

      setRowFactory {
        val tableRow = object : TableRow<Mod>() {
          override fun updateItem(item: Mod?, empty: Boolean) {
            super.updateItem(item, empty)
            this.toggleClass(AppStyle.added, item?.status == ADDED_TO_MODLIST)
          }
        }
        tableRow
      }
      bindSelected(modController)
      contextmenu {
        item("Browse on steam").action {
          selectedItem?.apply { openInBrowser("https://steamcommunity.com/sharedfiles/filedetails/?id=${steamId}") }
        }
        item("Browse mod url").action {
          selectedItem?.metaData?.apply { openInBrowser(url) }
        }
        item("Open in explorer").action {
          selectedItem?.apply { Desktop.getDesktop().open(baseDir) }
        }
      }
      modListStrategy.modList.onChange {
        requestResize()
      }
    }
    tableview.onSelectionChange { mod ->
      fire(SelectedModChangedRequest(mod))
    }
    tableview.setOnKeyPressed { e ->
      if (e.code == KeyCode.ENTER) {
        tableview.selectedItem?.apply { modListStrategy.modAction(this) }
      }
    }
    tableview.onDoubleClick {
      tableview.selectedItem?.apply { modListStrategy.modAction(this) }
    }
    center = tableview
  }
}

fun openInBrowser(address: String) {
  if (!address.isBlank() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    Desktop.getDesktop().browse(URI(address))
  }
}

class DescriptionFragment : Fragment() {
  val modController: ModController by inject()

  override val root =
    hbox {
      style {
        backgroundColor += WHITE
      }
      useMaxWidth = true

      pane {
        setPrefSize(200.0, 200.0)
        style {
          backgroundSize = multi(BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false))
          backgroundRepeat = multi(BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT)
        }
        modController.itemProperty.onChange { mod ->
          val imageURI = mod?.imageURI
          if (imageURI != null) {
            style(append = true) {
              backgroundImage += imageURI
            }
          }
        }
      }
      webview {
        prefHeight = 200.0
        hgrow = Priority.ALWAYS
        modController.itemProperty.onChange { mod ->
          if (mod != null) {
            engine.loadContent("<div style='white-space: pre; font-family: Verdana; word-wrap: break-word; padding: 10px'>${mod.metaData?.description}</div>")
          }
        }
      }
    }
}

class MyView : View("GW Mod manager") {
  val modController: ModController by inject()

  init {
    // for dev only
    fire(ModsLoadRequest)
  }

  override fun onBeforeShow() {
    fire(ModsLoadRequest)
  }

  val inactiveModList = find(
    ModListFragment::class, mapOf(
      ModListFragment::modListStrategy to ModListStrategy(
        title = "Inactive mods",
        modList = modController.inactiveMods,
        modAction = modController::activateMod
      )
    )
  )
  val activeModList = find(
    ModListFragment::class, mapOf(
      ModListFragment::modListStrategy to ModListStrategy(
        title = "Active mods",
        modList = modController.activeMods,
        modAction = modController::deactivateMod
      )
    )
  )

  override val root =
    borderpane {
      top = find(ToolbarView::class).root
      center = splitpane {
        add(inactiveModList)
        add(activeModList)
      }
      bottom = find(DescriptionFragment::class).root
    }
}

fun main(args: Array<String>) {
  launch<MyApp>(args)
}

