package com.geowarin.modmanager.db

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.geowarin.modmanager.ModLoaderPaths
import com.geowarin.modmanager.mod.MultiplayerCompat
import com.geowarin.modmanager.mod.MultiplayerCompatLevel
import com.geowarin.modmanager.utils.exists
import com.geowarin.modmanager.utils.parseJson
import com.geowarin.modmanager.utils.toURI
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.Reader
import java.net.URL
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KProperty1


val databaseResource = CachedResource(
  url = "https://api.bitbucket.org/2.0/repositories/shakeyourbunny/rwmsdb/src/master/rwmsdb.json",
  fileName = ModLoaderPaths::rwmsCache,
  loader = ::dbLoader
)

val categoriesResource = CachedResource(
  url = "https://api.bitbucket.org/2.0/repositories/shakeyourbunny/rwmsdb/src/master/rwms_db_categories.json",
  fileName = ModLoaderPaths::categoriesCache,
  loader = ::categoriesLoader
)

val multiplayerCompatResource = CachedResource(
  url = "https://docs.google.com/spreadsheets/d/1jaDxV8F7bcz4E9zeIRmZGKuaX7d0kvWWq28aKckISaY/export?format=csv&id=1jaDxV8F7bcz4E9zeIRmZGKuaX7d0kvWWq28aKckISaY&gid=0",
  fileName = ModLoaderPaths::multiplayerCompat,
  loader = ::multiplayerCompatLoader
)

fun multiplayerCompatLoader(reader: Reader): Map<String, MultiplayerCompat> {
  return csvReader().readAll(reader.readText()).drop(2).map { cells ->
    val compatLevel = MultiplayerCompatLevel.fromInt(cells[0].toInt())
    val modId = cells[2]
    val comment = cells[5]
    modId to MultiplayerCompat(compatLevel, comment)
  }.toMap()
}

data class CachedResource<T>(
  val url: String,
  val fileName: KProperty1<ModLoaderPaths, Path>,
  val loader: (Reader) -> T
)

data class Category(
  val prority: Double,
  val fullName: String
)

class Rwms(fs: FileSystem = FileSystems.getDefault()) {
  private val db: Map<String, String> = databaseResource.load(fs)
  private val categories: Map<String, Category> = categoriesResource.load(fs)
  private val multiplayerCompat: Map<String, MultiplayerCompat> = multiplayerCompatResource.load(fs)
  private val databaseOverride = loadDbOverrides(fs)

  fun getModCategory(cleanModName: String): Category {
    val categoryTag = databaseOverride[cleanModName] ?: db[cleanModName] ?: "unknown"
    return categories[categoryTag] ?: Category(999.0, "Unknown")
  }

  private fun loadDbOverrides(fs: FileSystem): JsonObject {
    val dbOverrides = ModLoaderPaths(fs).dbOverrides
    return when {
      dbOverrides.exists() -> dbOverrides.parseJson()
      else -> JsonObject()
    }
  }

  fun getMultiplayerCompat(modId: String): MultiplayerCompat {
    return multiplayerCompat[modId] ?: MultiplayerCompat()
  }
}

private fun categoriesLoader(reader: Reader): Map<String, Category> {
  val data = Klaxon().parseJsonObject(reader)
  return data.mapValues {
    val array = data.array<Any>(it.key)!!
    Category(array[0] as Double, array[1] as String)
  }
}

private fun dbLoader(reader: Reader): Map<String, String> {
  val data = Klaxon().parseJsonObject(reader)
  return data.obj("db")!!.map as Map<String, String>
}

private fun <T> CachedResource<T>.load(fs: FileSystem): T {
  val cached = fileName.get(ModLoaderPaths(fs))
  if (cached.exists()) {
    println("Loading $cached from cache")
    return justLoad(cached.toURI().toURL())
  }
  return downloadAndParse(fs)
}

private fun <T> CachedResource<T>.downloadAndParse(fs: FileSystem): T {
  val dbFile = this.downloadToCache(fs)
  return justLoad(dbFile.toURI().toURL())
}

private fun <T> CachedResource<T>.downloadToCache(fs: FileSystem): Path {
  val cached = fileName.get(ModLoaderPaths(fs))
  Files.createDirectories(cached.parent)
  URL(url).openStream().bufferedReader().use { input ->
    Files.newBufferedWriter(cached).use { output ->
      input.copyTo(output)
    }
  }
  return cached
}

private fun <T> CachedResource<T>.justLoad(url: URL): T {
  return url.openStream().bufferedReader().use { r -> loader(r) }
}
