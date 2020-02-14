package com.geowarin.modmanager.mod

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.db.Category
import com.geowarin.modmanager.db.Rwms
import com.geowarin.modmanager.mod.ModType.LOCAL_MOD
import com.geowarin.modmanager.mod.ModType.STEAM_MOD
import com.geowarin.modmanager.utils.exists
import com.geowarin.modmanager.utils.toURI
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.streams.toList

data class Mod(
  val modName: String,
  val cleanModName: String = modName,
  val status: ModStatus = ModStatus.UNKNOWN,
  val modType: ModType,
  val baseDir: Path = Paths.get("/"),
  val metaData: ModMetaData? = null,
  val category: Category,
  val manifest: ModManifest? = null
) {
  val categoryName: String
    get() = category.fullName

  val priority: Double
    get() = category.prority

  val modId: String
    get() = when (modType) {
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

fun doLoadMods(
  modType: ModType,
  paths: RimworldPaths,
  rwms: Rwms
): List<Mod> {
  val modsFolder = when (modType) {
    STEAM_MOD -> paths.steamModsFolder
    LOCAL_MOD -> paths.localModsFolder
  }
  val mods = modsFolder.list().mapNotNull { baseDir ->
    val metadata = parseMetadata(baseDir)
    if (metadata == null)
      null
    else {
      val manifest = parseManifest(baseDir)
      val cleanModName = cleanModName(metadata.name)
      val category = rwms.getModCategory(cleanModName)
      Mod(
        modName = metadata.name,
        cleanModName = cleanModName,
        baseDir = baseDir,
        metaData = metadata,
        category = category,
        modType = modType,
        manifest = manifest
      )
    }
  }
  println("Loaded ${mods.size} mods from ${modsFolder.toUri()}")
  return mods
}

fun Path.list(): List<Path> {
  return Files.list(this).toList()
}

internal fun modOnlyInConfig(modId: String): Mod {
  return Mod(
    modName = modId,
    status = ModStatus.ACTIVE,
    modType = LOCAL_MOD,
    category = Category(999.0, "Not found")
  )
}

/**
 * For compatibility with rwmsdb. Spec at
 * https://bitbucket.org/shakeyourbunny/rwms/src/fecd14bc9eed98dfec4a8d15609120e38bf853f6/rwms_sort.py#lines-204
 */
fun cleanModName(name: String): String {
  val re = Regex("(v|V|)\\d+\\.\\d+(\\.\\d+|)([a-z]|)|\\[(1.0|([AB])\\d+)]|\\((1.0|([AB])\\d+)\\)|(for |R|)(1.0|([AB])\\d+)|\\.1([89])")

  var clean = re.replaceFirst(name, "")
  clean = re.replaceFirst(name, "")
  clean = clean.replace(" - ", ": ").replace(" : ", ": ")
  clean = clean.replace("  ", " ")
  // removes duplicated whitespaces
  clean = clean.split("\\s+").joinToString(" ").trim()

  // cleanup ruined names
  clean = clean.replace("()", "")
  clean = clean.replace("[]", "")

  // special cases
  clean = clean.replace("(v. )", "")  // Sora's RimFantasy: Brutal Start (v. )
  if (clean.endsWith(" Ver"))
    clean = clean.replace(" Ver", "")  // Starship Troopers Arachnids Ver
  if (clean.endsWith(" %"))
    clean = clean.replace(" %", "")  // Tilled Soil (Rebalanced): %
  if (clean.contains("[ "))
    clean = clean.replace("[ ", "[")  // Additional Traits [ Update]
  if (clean.contains("( & b19)"))
    clean = clean.replace("( & b19)", "")  // Barky's Caravan Dogs ( & b19)
  if (clean.contains("[19]"))
    clean = clean.replace("[19]", "")  // Sailor Scouts Hair [19]
  if (clean.contains("[/] Version"))
    clean = clean.replace("[/] Version", "")  // Fueled Smelter [/] Version

  clean = clean.removeSuffix(":")
  clean = clean.removePrefix(": ") // : ACP: More Floors Wool Patch
  clean = clean.removePrefix("-") // -FuelBurning

  clean = clean.trim()

  return clean
}
