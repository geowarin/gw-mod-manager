package com.geowarin.modmanager.gui

import com.geowarin.modmanager.*
import com.geowarin.modmanager.mod.loadMods
import com.geowarin.modmanager.mod.parseModsConfig
import tornadofx.*
import java.io.File

class ModController : Controller() {
  init {
    subscribe<ModsListRequest> {
      val db = database.load()
      val categories = categories.load()

      val allMods = loadMods(MacPaths.steamModsFolder, db, categories)

      val modsConfig = parseModsConfig(MacPaths.configFolder)
      val (activeMods, inactiveMods) = allMods.partition { modsConfig.activeMods.contains(it.baseDir.name) }

      fire(ModsListEvent(inactiveMods, activeMods))
    }
    subscribe<SelectedModChangedRequest> {
      if (it.mod != null) {
        val imageUrl = File(it.mod.baseDir, "About/Preview.png").toURI().toURL().toString()
        fire(SelectedModChangedEvent(it.mod.metaData.description, imageUrl))
      }
    }
  }
}

