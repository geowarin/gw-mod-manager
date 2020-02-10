package com.geowarin.modmanager.gui

import com.geowarin.modmanager.MacPaths
import com.geowarin.modmanager.categories
import com.geowarin.modmanager.database
import com.geowarin.modmanager.load
import com.geowarin.modmanager.mod.Mod
import com.geowarin.modmanager.mod.loadMods
import com.geowarin.modmanager.mod.parseModsConfig
import tornadofx.*
import java.io.File

object ModsLoadRequest : FXEvent(EventBus.RunOn.BackgroundThread)
class ModsListResponse(val inactiveMods: List<Mod>, val activeMods: List<Mod>) : FXEvent()

class SelectedModChangedRequest(val mod: Mod?) : FXEvent(EventBus.RunOn.BackgroundThread)
class SelectedModChangedResponse(val description: String, val imageUrl: String) : FXEvent()

class ModActivationRequest(val mod: Mod) : FXEvent(EventBus.RunOn.BackgroundThread)
class ModDeactivationRequest(val mod: Mod) : FXEvent(EventBus.RunOn.BackgroundThread)

class ModController : Controller() {
  val activeMods = mutableListOf<Mod>()
  val inactiveMods = mutableListOf<Mod>()

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

      fire(ModsListResponse(inactiveMods, activeMods))
    }
    subscribe<SelectedModChangedRequest> {
      if (it.mod != null) {
        val imageUrl = File(it.mod.baseDir, "About/Preview.png").toURI().toURL().toString()
        fire(SelectedModChangedResponse(it.mod.metaData.description, imageUrl))
      }
    }
    subscribe<ModActivationRequest> { e ->
      activeMods += e.mod
      inactiveMods -= e.mod
      fire(ModsListResponse(inactiveMods, activeMods))
    }
    subscribe<ModDeactivationRequest> { e ->
      activeMods -= e.mod
      inactiveMods += e.mod
      fire(ModsListResponse(inactiveMods, activeMods))
    }
  }
}

