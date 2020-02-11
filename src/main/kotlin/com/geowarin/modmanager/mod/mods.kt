package com.geowarin.modmanager.mod

import com.geowarin.modmanager.db.Category
import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.db.Rwms
import com.geowarin.modmanager.mod.ModType.LOCAL_MOD
import com.geowarin.modmanager.mod.ModType.STEAM_MOD
import com.geowarin.modmanager.utils.exists
import com.geowarin.modmanager.utils.toURI
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.streams.toList

data class Mod(
  val cleanModName: String,
  val status: ModStatus = ModStatus.UNKNOWN,
  val modType: ModType,
  val baseDir: Path = Paths.get("/"),
  val metaData: ModMetaData? = null,
  val category: Category? = null,
  val manifest: ModManifest? = null
) {
  val categoryName: String
    get() = category?.fullName ?: "Unknown"

  val priority: Double
    get() = category?.prority ?: 999.0

  val modId: String
    get() = when(modType){
      STEAM_MOD -> baseDir.fileName.toString()
      LOCAL_MOD -> cleanModName
    }

  val imageURI: URI?
    get() {
      val preview = baseDir.resolve("About/Preview.png")
      if (preview.exists())
        return preview.toURI()
      return null
    }

  override fun equals(other: Any?): Boolean {
    if (other == null || other !is Mod)
      return false
    return baseDir == other.baseDir
  }

  override fun hashCode() = Objects.hashCode(baseDir)
}

enum class ModStatus {
  ADDED_TO_MODLIST,
  REMOVED_FROM_MODLIST,
  INACTIVE,
  ACTIVE,
  ACTIVE_MOVED_UP,
  ACTIVE_MOVED_DOWN,
  UNKNOWN
}

enum class ModType {
  STEAM_MOD,
  LOCAL_MOD
}

fun loadSteamMods(rwms: Rwms, paths: RimworldPaths): List<Mod> {
  return loadMods(paths.steamModsFolder, STEAM_MOD, rwms.db, rwms.categories)
}

fun loadLocalMods(rwms: Rwms, paths: RimworldPaths): List<Mod> {
  return loadMods(paths.localModsFolder, LOCAL_MOD, rwms.db, rwms.categories)
}

fun loadMods(
  modsDir: Path,
  modType: ModType,
  db: Map<String, Any?> = mapOf(),
  categories: Map<String, Category>
): List<Mod> {
  val modDirs = Files.list(modsDir).toList()
  val mods = modDirs.mapNotNull { baseDir ->
    val metadata = parseMetadata(baseDir)
    if (metadata == null)
      null
    else {
      val manifest = parseManifest(baseDir)
      val cleanModName = cleanModName(metadata.name)
      val categoryTag = db[cleanModName] as String? ?: "unknown"
      val category = categories[categoryTag] ?: Category(999.0, "Not found")
      Mod(
        cleanModName = cleanModName,
        baseDir = baseDir,
        metaData = metadata,
        category = category,
        modType = modType,
        manifest = manifest
      )
    }
  }
  println("Loaded ${mods.size} mods from ${modsDir.toUri()}")
  return mods
}

fun cleanModName(name: String): String {
  return name
}
