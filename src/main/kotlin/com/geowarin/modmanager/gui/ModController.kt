package com.geowarin.modmanager.gui

import com.geowarin.modmanager.Paths
import com.geowarin.modmanager.Rwms
import com.geowarin.modmanager.mod.*
import com.geowarin.modmanager.mod.ModStatus.*
import com.geowarin.modmanager.mod.ModType.LOCAL_MOD
import tornadofx.*

object ModsLoadRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class SelectedModChangedRequest(val mod: Mod?) : FXEvent(EventBus.RunOn.BackgroundThread)

class ModController : ItemViewModel<Mod>() {
  val activeMods = mutableListOf<Mod>().asObservable()
  val inactiveMods = mutableListOf<Mod>().asObservable()

  val originalMods = mutableListOf<String>()

  init {
    subscribe<ModsLoadRequest> {
      activeMods.clear()
      inactiveMods.clear()
      originalMods.clear()

      val rwms = Rwms()
      rwms.load()

      val steamMods = loadSteamMods(rwms)
      val localMods = loadLocalMods(rwms)
      val allMods = (steamMods + localMods).sortedBy { it.priority }

      val modsConfig = parseModsConfig(Paths.configFolder)
      originalMods += modsConfig.activeMods

      activeMods += modsConfig.activeMods.map { activeModId ->
        allMods.find { it.modId == activeModId }?.copy(status = ACTIVE) ?: Mod(
          cleanModName = activeModId,
          status = ACTIVE,
          modType = LOCAL_MOD
        )
      }
      inactiveMods += allMods.filter { !modsConfig.activeMods.contains(it.modId) }.map { it.copy(status = INACTIVE) }
    }
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

    val newStatus = if (originalMods.contains(mod.modId)) REMOVED_DROM_MODLIST else INACTIVE
    inactiveMods += mod.copy(status = newStatus)
  }
}

