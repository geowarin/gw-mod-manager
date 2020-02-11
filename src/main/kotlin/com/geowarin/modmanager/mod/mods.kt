package com.geowarin.modmanager.mod

import com.geowarin.modmanager.Category
import java.io.File

data class Mod(
  val baseDir: File = File(""),
  val cleanModName: String,
  val metaData: ModMetaData = ModMetaData(),
  val category: Category = Category(999.0, "Unknown")
) {
  val categoryName
    get() = category.fullName

  val priority
    get() = category.prority

  val steamId
    get() = baseDir.name

  val imageURI
    get() = File(baseDir, "About/Preview.png").toURI()
}

fun loadMods(
  modsDir: String,
  db: Map<String, Any?> = mapOf(),
  categories: Map<String, Category>
): List<Mod> {
  val modDirs = File(modsDir).listFiles()?.toList() ?: emptyList()
  return modDirs.mapNotNull { baseDir: File ->
    val metadata = parseMetadata(baseDir)
    if (metadata == null)
      null
    else {
      val cleanModName = cleanModName(metadata.name)
      val categoryTag = db[cleanModName] as String? ?: "unknown"
      val category = categories[categoryTag] ?: Category(999.0, "Unknown")
      Mod(baseDir, cleanModName, metadata, category)
    }
  }
}

fun cleanModName(name: String): String {
  return name
}
