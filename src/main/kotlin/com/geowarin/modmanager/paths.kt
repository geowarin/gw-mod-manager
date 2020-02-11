package com.geowarin.modmanager

import java.nio.file.FileSystem
import java.nio.file.FileSystems

object OS {
  private val os = System.getProperty("os.name").toLowerCase()
  val isWindows = os.indexOf("win") >= 0
  val isMac = os.indexOf("mac") >= 0
  val isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0
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

}
