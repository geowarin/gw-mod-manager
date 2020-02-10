package com.geowarin.modmanager.mod

import com.geowarin.modmanager.Category
import java.io.File

data class Mod(
  val root: File,
  val cleanModName: String,
  val metaData: ModMetaData,
  val category: Category
) {
  val categoryName
    get() = category.fullName

  val priority
    get() = category.prority
}

fun loadMods(
  modsDir: String,
  db: Map<String, Any?> = mapOf(),
  categories: Map<String, Category>
): List<Mod> {
  val modDirs = File(modsDir).listFiles()?.toList() ?: emptyList()
  return modDirs.map {
    val metadata = parseMetadata(it)
    if (metadata == null)
      null
    else {
      val cleanModName = cleanModName(metadata.name)
      val categoryTag = db[cleanModName] as String? ?: "unknown"
      val category = categories[categoryTag] ?: Category(999.0, "Unknown")
      Mod(it, cleanModName, metadata, category)
    }
  }.filterNotNull()
}


fun cleanModName(name: String): String {
  return name
}
