package com.geowarin.modmanager.gui

import com.geowarin.modmanager.db.Category
import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.db.Rwms
import com.geowarin.modmanager.mod.Mod
import com.geowarin.modmanager.mod.ModStatus.*
import com.geowarin.modmanager.mod.ModType.LOCAL_MOD
import com.geowarin.modmanager.mod.loadLocalMods
import com.geowarin.modmanager.mod.loadSteamMods
import com.geowarin.modmanager.mod.parseModsConfig
import tornadofx.*

object ModsLoadRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class SelectedModChangedRequest(val mod: Mod?) : FXEvent(EventBus.RunOn.BackgroundThread)

class ModController : ItemViewModel<Mod>() {
  val activeMods = mutableListOf<Mod>().asObservable()
  val inactiveMods = mutableListOf<Mod>().asObservable()

  val originalMods = mutableListOf<String>()

  init {
    subscribe<ModsLoadRequest> {
      loadMods()
    }
  }

  private fun loadMods() {
    activeMods.clear()
    inactiveMods.clear()
    originalMods.clear()

    val rwms = Rwms()
    rwms.load()

    val steamMods = loadSteamMods(rwms)
    val localMods = loadLocalMods(rwms)
    val allMods = (steamMods + localMods).sortedBy { it.priority }

    val modsConfig = parseModsConfig(RimworldPaths().configFolder)
    originalMods += modsConfig.activeMods

    activeMods += modsConfig.activeMods.map { activeModId ->
      allMods.find { it.modId == activeModId }?.copy(status = ACTIVE) ?: defaultMod(activeModId)
    }
    inactiveMods += allMods.filter { !modsConfig.activeMods.contains(it.modId) }.map { it.copy(status = INACTIVE) }
  }

  private fun defaultMod(modId: String): Mod {
    return Mod(
      cleanModName = modId,
      status = ACTIVE,
      modType = LOCAL_MOD,
      category = Category(999.0, "Not found")
    )
  }

  fun activateMod(mod: Mod) {
    inactiveMods -= mod

    activeMods += recomputeModStatus(mod, activeMods)
  }

  fun recomputeModStatus(mod: Mod, activeMods: List<Mod>): Mod {
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
  }
}

