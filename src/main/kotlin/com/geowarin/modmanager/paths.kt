package com.geowarin.modmanager

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path

object OS {
  private val os = System.getProperty("os.name").toLowerCase()
  val isWindows = os.indexOf("win") >= 0
  val isMac = os.indexOf("mac") >= 0
  val isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0
}

class ModLoaderPaths(fs: FileSystem = FileSystems.getDefault()) {
  val dbOverrides = getConfigDir(fs).resolve("database-overrides.json")
  val rwmsCache = getCacheDir(fs).resolve("rwmsdb.json")
  val categoriesCache = getCacheDir(  fs).resolve("categories.json")
}

class RimworldPaths(val fs: FileSystem = FileSystems.getDefault()) {
  val gameFolder
    get() = when {
      OS.isWindows -> fs.getPath(System.getenv("ProgramFiles(x86)"), "Steam/steamapps/common/RimWorld")
      OS.isMac -> fs.getPath(System.getProperty("user.home"), "Library/ApplicationSupport/Steam/steamapps/common/RimWorld")
      else -> throw Error()
    }

  val configFolder
    get() = when {
      OS.isWindows -> fs.getPath(System.getenv("APPDATA"), "../LocalLow/Ludeon\\ Studios/RimWorld\\ by\\ Ludeon\\ Studios/Config")
      OS.isMac -> fs.getPath(System.getProperty("user.home"), "Library/ApplicationSupport/RimWorld/Config")
      else -> throw Error()
    }

  val steamModsFolder
    get() = when {
      OS.isWindows -> fs.getPath(System.getenv("ProgramFiles(x86)"), "Steam/steamapps/workshop/content/294100")
      OS.isMac -> fs.getPath(System.getProperty("user.home"), "Library/ApplicationSupport/Steam/steamapps/workshop/content/294100")
      else -> throw Error()
    }

  val localModsFolder
    get() = when {
      OS.isWindows -> fs.getPath(System.getenv("ProgramFiles(x86)"), "Steam/steamapps/common/RimWorld/Mods")
      OS.isMac -> fs.getPath(System.getProperty("user.home"), "Library/ApplicationSupport/Steam/steamapps/common/RimWorld/RimWorldMac.app/Mods")
      else -> throw Error()
    }

  // /Users/geowarin/Library/Application Support/RimWorld/Saves

  val rimworldExecutable
    get() = when {
      OS.isWindows -> fs.getPath(System.getenv("ProgramFiles(x86)"), "TODO")
      OS.isMac -> fs.getPath(
        System.getProperty("user.home"),
        "Library/ApplicationSupport/Steam/steamapps/common/RimWorld/RimWorldMac.app/Contents/MacOS/RimWorldMac"
      )
      else -> throw Error()
    }
}

internal fun getCacheDir(fs: FileSystem = FileSystems.getDefault()): Path {
  return fs.getPath(System.getenv("XDG_CACHE_HOME")).resolve("gw-mod-manager")
    ?: fs.getPath(System.getProperty("user.home"), "gw-mod-manager", "cache")
}

private fun getConfigDir(fs: FileSystem = FileSystems.getDefault()): Path {
  return fs.getPath(System.getenv("XDG_CONFIG_HOME")).resolve("gw-mod-manager")
    ?: fs.getPath(System.getProperty("user.home"), "gw-mod-manager", "config")
}
