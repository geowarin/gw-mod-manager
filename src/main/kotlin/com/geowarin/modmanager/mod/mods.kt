package com.geowarin.modmanager.mod

import com.geowarin.modmanager.loadDb
import java.io.File

data class Mod(
  val root: File,
  val cleanModName: String,
  val category: String
)

fun loadMods(modsDir: String): List<Mod> {
  val db = loadDb()

  val modDirs = File(modsDir).listFiles()?.toList() ?: emptyList()
  return modDirs.map {
    val metadata = parseMetadata(it)
    if (metadata == null)
      null
    else {
      val cleanModName = cleanModName(metadata.name)
      val category = db[cleanModName] as String? ?: "Unknown"
      Mod(it, cleanModName, category)
    }
  }.filterNotNull()
}


fun cleanModName(name: String): String {
  return name
}
