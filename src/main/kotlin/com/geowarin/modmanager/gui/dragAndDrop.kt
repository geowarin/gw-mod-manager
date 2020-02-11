package com.geowarin.modmanager

import com.geowarin.modmanager.gui.AppStyle
import javafx.event.Event
import javafx.scene.control.TableRow
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import tornadofx.*

fun <T> TableRow<T>.enableDnDReordering(
  rowIdProvider: (T) -> String,
  updater: (T, Int) -> Unit = { _, _ -> }
) {
  val thisRow: TableRow<T> = this

  fun acceptDrop(event: DragEvent): Boolean {
    val gestureSource = event.gestureSource
    return gestureSource !== thisRow && event.dragboard.hasString() && (gestureSource as? TableRow<*>)?.tableView == thisRow.tableView
  }

  setOnDragDetected { e ->
    if (item == null) {
      return@setOnDragDetected
    }

    val dragboard = startDragAndDrop(TransferMode.MOVE)
    val content = ClipboardContent()
    content.putString(rowIdProvider(item))
    //                dragboard.dragView = rectangle(0.0,0.0, 20.0, 20.0)
    dragboard.setContent(content)

    e.consume()
  }
  setOnDragOver { event ->
    if (acceptDrop(event)) {
      event.acceptTransferModes(TransferMode.MOVE)
    }
    event.consume()
  }

  setOnDragEntered { event ->
    if (acceptDrop(event)) {
      addClass(AppStyle.dragTarget)
    }
  }

  setOnDragExited { event ->
    if (acceptDrop(event)) {
      removeClass(AppStyle.dragTarget)
    }
  }

  setOnDragDropped { event ->
    if (item == null) {
      return@setOnDragDropped
    }

    val db = event.dragboard
    var success = false
    if (db.hasString()) {
      val draggedItem = tableView.items.find { rowIdProvider(it) == db.string }
      val targetIdx = tableView.items.indexOf(item)
      if (draggedItem != null) {
        updater(draggedItem, targetIdx)
        success = true
      }
    }
    event.isDropCompleted = success
    event.consume()
  }

  setOnDragDone(Event::consume)
}
