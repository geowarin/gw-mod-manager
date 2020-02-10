package com.geowarin.modmanager.gui

import com.geowarin.modmanager.MacPaths
import com.geowarin.modmanager.categories
import com.geowarin.modmanager.database
import com.geowarin.modmanager.load
import com.geowarin.modmanager.mod.loadMods
import com.geowarin.modmanager.mod.parseModsConfig
import tornadofx.*

class ModController : Controller() {
  init {
    subscribe<ModsListRequest> {
      val db = database.load()
      val categories = categories.load()

      val mods = loadMods(MacPaths.steamModsFolder, db, categories)

      val modsConfig = parseModsConfig(MacPaths.configFolder)
      val activeMods = mods.filter { modsConfig.activeMods.contains(it.root.name) }

      fire(ModsListEvent(mods, activeMods))
    }
  }
}

