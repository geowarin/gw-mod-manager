package com.geowarin.modmanager.db

import com.beust.klaxon.Klaxon
import com.geowarin.modmanager.utils.exists
import com.geowarin.modmanager.utils.getCacheDir
import com.geowarin.modmanager.utils.toURI
import java.io.Reader
import java.net.URL
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path


val databaseResource = CachedResource(
  url = "https://api.bitbucket.org/2.0/repositories/shakeyourbunny/rwmsdb/src/master/rwmsdb.json",
  fileName = "rwmsdb.json",
  loader = ::dbLoader
)

val categoriesResource = CachedResource(
  url = "https://api.bitbucket.org/2.0/repositories/shakeyourbunny/rwmsdb/src/master/rwms_db_categories.json",
  fileName = "categories.json",
  override = "categories-override.json",
  loader = ::categoriesLoader
)

data class CachedResource<T>(
  val url: String,
  val fileName: String,
  val loader: (Reader) -> T,
  val override: String? = null
)

data class Category(
  val prority: Double,
  val fullName: String
)

class Rwms(val fs: FileSystem = FileSystems.getDefault()) {
  var db: Map<String, String> = emptyMap()
  var categories: Map<String, Category> = emptyMap()

  fun load() {
    db = databaseResource.load(fs)
    categories = categoriesResource.load(fs)
  }
}

val klaxon = Klaxon()

fun categoriesLoader(reader: Reader): Map<String, Category> {
  val data = klaxon.parseJsonObject(reader)
  return data.mapValues {
    val array = data.array<Any>(it.key)!!
    Category(array[0] as Double, array[1] as String)
  }
}

fun dbLoader(reader: Reader): Map<String, String> {
  val data = klaxon.parseJsonObject(reader)
  return data.obj("db")!!.map as Map<String, String>
}

fun <T> CachedResource<T>.load(fs: FileSystem): T {
  val cachedDb = getCacheDir(fs).resolve(this.fileName)
  if (cachedDb.exists()) {
    println("Loading ${this.fileName} from cache")
    return justLoad(cachedDb.toURI().toURL())
  }
  return downloadAndParse(fs)
}

private fun <T> CachedResource<T>.downloadAndParse(fs: FileSystem): T {
  val dbFile = this.downloadToCache(fs)
  return justLoad(dbFile.toURI().toURL())
}

private fun <T> CachedResource<T>.downloadToCache(fs: FileSystem): Path {
  val cacheDbFile = getCacheDir(fs).resolve(this.fileName)
  Files.createDirectories(cacheDbFile.parent)
  URL(url).openStream().bufferedReader().use { input ->
    Files.newBufferedWriter(cacheDbFile).use { output ->
      input.copyTo(output)
    }
  }
  return cacheDbFile
}

fun <T> CachedResource<T>.justLoad(url: URL): T {
  return url.openStream().bufferedReader().use(loader)
}
