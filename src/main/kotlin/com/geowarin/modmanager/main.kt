package com.geowarin.modmanager

import com.geowarin.modmanager.gui.*
import com.geowarin.modmanager.mod.Mod
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

typealias ModListSelector = (ModsListResponse) -> List<Mod>
typealias ModAction = (Mod) -> Unit

class ModListStrategy(
  val title: String,
  val modListSelector: ModListSelector,
  val modAction: ModAction
)


class ModListFragment : Fragment() {
  //  val mods: FilteredList<Mod> = FilteredList(FXCollections.observableArrayList<Mod>())
//  val search = SimpleStringProperty().onChange { srch ->
//    mods.setPredicate { srch.isNullOrBlank() || it.cleanModName.toLowerCase().contains(srch.toLowerCase()) }
//  }
  val modListStrategy: ModListStrategy by param()

  override val root = borderpane {
    fitToParentWidth()
    top = label(modListStrategy.title)
    val tableview = tableview<Mod> {
      readonlyColumn("Name", Mod::cleanModName).weightedWidth(weight = 70, minContentWidth = true)
      readonlyColumn("Category", Mod::categoryName).weightedWidth(20, minContentWidth = true).cellFormat { priority ->
        text = priority
        style { textFill = if (priority == "Unknown") RED else BLACK }
      }
      readonlyColumn("Priority", Mod::priority).weightedWidth(10, minContentWidth = true)

      subscribe<ModsListResponse> { event ->
        items.setAll(modListStrategy.modListSelector(event))
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
  override val root =
    hbox {
      style {
        backgroundColor += WHITE
      }
      useMaxWidth = true
      pane {
        setPrefSize(200.0, 200.0)
        subscribe<SelectedModChangedResponse> { e ->
          style {
            backgroundImage += URI(e.imageUrl)
            backgroundSize = multi(BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false))
            backgroundRepeat = multi(BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT)
          }
        }
      }
      webview {
        prefHeight = 200.0
        hgrow = Priority.ALWAYS
        subscribe<SelectedModChangedResponse> {
          engine.loadContent("<div style='white-space: pre; font-family: Verdana; word-wrap: break-word; padding: 10px'>${it.description}</div>")
        }
      }
    }
}

class MyView : View("GW Mod manager") {
  init {
    find(ModController::class)
    // for dev only
    fire(ModsLoadRequest)
  }

//  override fun onBeforeShow() {
//    fire(ModsLoadRequest)
//  }

  val inactiveModList = find(ModListFragment::class, mapOf(ModListFragment::modListStrategy to ModListStrategy(
    title = "Inactive mods",
    modListSelector = { e -> e.inactiveMods },
    modAction = { mod ->
      fire(ModActivationRequest(mod))
    }
  )))
  val activeModList = find(ModListFragment::class, mapOf(ModListFragment::modListStrategy to ModListStrategy(
    title = "Active mods",
    modListSelector = { e -> e.activeMods },
    modAction = { mod ->
      fire(ModDeactivationRequest(mod))
    }
  )))

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

