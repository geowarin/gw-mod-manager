package com.geowarin.modmanager.gui

import com.geowarin.modmanager.MacPaths
import com.geowarin.modmanager.categories
import com.geowarin.modmanager.database
import com.geowarin.modmanager.load
import com.geowarin.modmanager.mod.Mod
import com.geowarin.modmanager.mod.loadMods
import com.geowarin.modmanager.mod.parseModsConfig
import javafx.beans.value.ObservableObjectValue
import tornadofx.*
import java.io.File
import java.net.URI

object ModsLoadRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class SelectedModChangedRequest(val mod: Mod?) : FXEvent(EventBus.RunOn.BackgroundThread)

class ModController : ItemViewModel<Mod>() {
  val activeMods = mutableListOf<Mod>().asObservable()
  val inactiveMods = mutableListOf<Mod>().asObservable()

  init {
    subscribe<ModsLoadRequest> {
      activeMods.clear()
      inactiveMods.clear()

      val db = database.load()
      val categories = categories.load()

      val steamMods = loadMods(MacPaths.steamModsFolder, db, categories)
      val localMods = loadMods(MacPaths.localModsFolder, db, categories)
      val allMods = (steamMods + localMods).sortedBy { it.priority }

      val modsConfig = parseModsConfig(MacPaths.configFolder)

      activeMods += modsConfig.activeMods.map { activeModId ->
        allMods.find { it.baseDir.name == activeModId } ?: Mod(cleanModName = activeModId)
      }
      inactiveMods += allMods.filter { !modsConfig.activeMods.contains(it.baseDir.name) }
    }
  }
  fun activateMod(mod: Mod) {
    activeMods += mod
    inactiveMods -= mod
  }
  fun deactivateMod(mod: Mod) {
    activeMods -= mod
    inactiveMods += mod
  }
}

