package com.geowarin.modmanager.gui

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.db.Rwms
import com.geowarin.modmanager.mod.*
import com.geowarin.modmanager.mod.ModStatus.*
import tornadofx.*
import java.nio.file.FileSystem
import java.nio.file.FileSystems

object ModsLoadRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class ModViewModel: ItemViewModel<Mod>() {
  val activeMods = observableListOf<Mod>()
  val inactiveMods = observableListOf<Mod>()
}

fun <T> MutableCollection<T>.setAll(items: Collection<T>) {
  this.clear()
  this += items
}

class ModController : Controller() {
  val activeMods = mutableListOf<Mod>()
  val inactiveMods = mutableListOf<Mod>()
  val originalMods = mutableListOf<String>()

  val modViewModel: ModViewModel by inject()

  init {
    subscribe<ModsLoadRequest> {
      loadMods()
    }
  }

  fun loadMods(fs: FileSystem = FileSystems.getDefault()) {
    activeMods.clear()
    inactiveMods.clear()
    originalMods.clear()

    val rimworldPaths = RimworldPaths(fs)

    val rwms = Rwms(fs)
    rwms.load()

    val steamMods = loadSteamMods(rwms, rimworldPaths)
    val localMods = loadLocalMods(rwms, rimworldPaths)
    val allMods = (steamMods + localMods).sortedBy { it.priority }

    val modsConfig = parseModsConfig(rimworldPaths.configFolder)
    originalMods += modsConfig.activeMods

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

  private fun recomputeModStatus(mod: Mod, activeMods: List<Mod>): Mod {
    val originalIndex = originalMods.indexOf(mod.modId)
    val newIndex = activeMods.filter { it.status != ADDED_TO_MODLIST }.indexOfFirst { it.modId == mod.modId }
    val newStatus = when {
      originalIndex == -1 -> ADDED_TO_MODLIST
      originalIndex == newIndex -> ACTIVE
      originalIndex > newIndex -> ACTIVE_MOVED_UP
      else -> ACTIVE_MOVED_DOWN
    }
    return mod.copy(status = newStatus)
  }

  fun deactivateMod(mod: Mod) {
    activeMods -= mod

    val newStatus = if (originalMods.contains(mod.modId)) REMOVED_FROM_MODLIST else INACTIVE
    inactiveMods += mod.copy(status = newStatus)

    runLater {
      modViewModel.activeMods.setAll(activeMods)
      modViewModel.inactiveMods.setAll(inactiveMods)
    }
  }
}

