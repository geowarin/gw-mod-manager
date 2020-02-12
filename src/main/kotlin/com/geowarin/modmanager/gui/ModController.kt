package com.geowarin.modmanager.gui

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.db.Rwms
import com.geowarin.modmanager.mod.*
import com.geowarin.modmanager.mod.ModStatus.*
import javafx.beans.property.SimpleStringProperty
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.xml
import tornadofx.*
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files

object ModsLoadRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class ModViewModel : ItemViewModel<Mod>() {
  val activeSearchTerm = SimpleStringProperty()
  val inactiveSearchTerm = SimpleStringProperty()
  val activeMods = SortedFilteredList<Mod>()
  val inactiveMods = SortedFilteredList<Mod>()

  init {
    activeMods.setAllPassThrough = true
    inactiveMods.setAllPassThrough = true
    activeMods.filterWhen(activeSearchTerm) { search, mod ->
      search.isNullOrEmpty() || mod.modName.toLowerCase().contains(search.toLowerCase())
    }
    inactiveMods.filterWhen(inactiveSearchTerm) { search, mod ->
      search.isNullOrEmpty() || mod.modName.toLowerCase().contains(search.toLowerCase())
    }
  }
}

class ModController : Controller() {
  val activeMods = mutableListOf<Mod>()
  val inactiveMods = mutableListOf<Mod>()
  lateinit var modsConfig: ModsConfig

  val modViewModel: ModViewModel by inject()

  init {
    subscribe<ModsLoadRequest> {
      loadMods()
    }
  }

  fun loadMods(fs: FileSystem = FileSystems.getDefault()) {
    activeMods.clear()
    inactiveMods.clear()

    val rimworldPaths = RimworldPaths(fs)

    val rwms = Rwms(fs)
    rwms.load()

    val steamMods = loadSteamMods(rwms, rimworldPaths)
    val localMods = loadLocalMods(rwms, rimworldPaths)
    val allMods = (steamMods + localMods).sortedBy { it.priority }

    modsConfig = parseModsConfig(rimworldPaths.configFolder)

    activeMods += modsConfig.activeMods.map { activeModId ->
      allMods.find { it.modId == activeModId }?.copy(status = ACTIVE) ?: modOnlyInConfig(activeModId)
    }
    inactiveMods += allMods.filter { !modsConfig.activeMods.contains(it.modId) }.map { it.copy(status = INACTIVE) }

    runLater {
      modViewModel.activeMods.setAll(activeMods)
      modViewModel.inactiveMods.setAll(inactiveMods)
    }
  }

  fun reorder(mod: Mod, targetIdx: Int) {
    val newItems: ArrayList<Mod> = ArrayList(activeMods)
    val draggedIdx = activeMods.indexOfFirst { it.modId == mod.modId }

    val draggedItem = newItems.removeAt(draggedIdx)
    val realTarget = if (targetIdx < draggedIdx) targetIdx + 1 else targetIdx
    newItems.add(realTarget, draggedItem)

    activeMods.clear()
    activeMods += newItems.map { recomputeModStatus(it, newItems) }

    runLater {
      modViewModel.activeMods.setAll(activeMods)
      modViewModel.item = mod
    }
  }

  fun activateMod(mod: Mod) {
    inactiveMods -= mod

    activeMods += recomputeModStatus(mod, activeMods)

    runLater {
      modViewModel.activeMods.setAll(activeMods)
      modViewModel.inactiveMods.setAll(inactiveMods)
    }
  }

  fun deactivateMod(mod: Mod) {
    activeMods -= mod

    val newStatus = if (modsConfig.activeMods.contains(mod.modId)) REMOVED_FROM_MODLIST else INACTIVE
    inactiveMods += mod.copy(status = newStatus)

    runLater {
      modViewModel.activeMods.setAll(activeMods)
      modViewModel.inactiveMods.setAll(inactiveMods)
    }
  }

  fun saveModList(paths: RimworldPaths) {
    modsConfig.copy(activeMods = activeMods.map { it.modId })
      .save(paths)
  }

  fun sortMods() {
    val sortedMods = activeMods.sortedBy { it.category.prority }
    activeMods.clear()
    activeMods += sortedMods.map { recomputeModStatus(it, sortedMods) }
    runLater {
      modViewModel.activeMods.setAll(activeMods)
    }
  }

  private fun recomputeModStatus(mod: Mod, activeMods: List<Mod>): Mod {
    val originalIndex = modsConfig.activeMods.indexOf(mod.modId)
    val newIndex = activeMods.filter { it.status != ADDED_TO_MODLIST }.indexOfFirst { it.modId == mod.modId }
    val newStatus = when {
      originalIndex == -1 -> ADDED_TO_MODLIST
      originalIndex == newIndex -> ACTIVE
      originalIndex > newIndex -> ACTIVE_MOVED_UP
      else -> ACTIVE_MOVED_DOWN
    }
    return mod.copy(status = newStatus)
  }
}

