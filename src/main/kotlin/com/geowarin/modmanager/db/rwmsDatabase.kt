package com.geowarin.modmanager

import com.beust.klaxon.Klaxon
import com.geowarin.modmanager.utils.getCacheDir
import java.io.File
import java.io.FileWriter
import java.io.Reader
import java.net.URL


val database = CachedResource(
  url = "https://api.bitbucket.org/2.0/repositories/shakeyourbunny/rwmsdb/src/master/rwmsdb.json",
  fileName = "rwmsdb.json",
  loader = ::dbLoader
)

val categories = CachedResource(
  url = "https://api.bitbucket.org/2.0/repositories/shakeyourbunny/rwmsdb/src/master/rwms_db_categories.json",
  fileName = "categories.json",
  loader = ::categoriesLoader
)

data class CachedResource<T>(
  val url: String,
  val fileName: String,
  val loader: (Reader) -> T
)

data class Category(
  val prority: Double,
  val fullName: String
)

val klaxon = Klaxon()

fun categoriesLoader(reader: Reader): Map<String, Category> {
  val data = klaxon.parseJsonObject(reader)
  val categoriesByTag = data.mapValues {
    val array = data.array<Any>(it.key)!!
    Category(array[0] as Double, array[1] as String)
  }.toMutableMap()
  categoriesByTag["unknown"] = Category(999.0, "Unknown")
  return categoriesByTag
}

fun dbLoader(reader: Reader): Map<String, String> {
  val data = klaxon.parseJsonObject(reader)
  return data.obj("db")!!.map as Map<String, String>
}

fun <T> CachedResource<T>.load(): T {
  val cachedDb = File(getCacheDir(), this.fileName)
  if (cachedDb.exists()) {
    println("Loading ${this.fileName} from cache")
    return justLoad(cachedDb.toURI().toURL())
  }
  return downloadAndParse()
}

private fun <T> CachedResource<T>.downloadAndParse(): T {
  val dbFile = this.downloadToCache()
  return justLoad(dbFile.toURI().toURL())
}

private fun <T> CachedResource<T>.downloadToCache(): File {
  val cacheDbFile = File(getCacheDir(), fileName)
  cacheDbFile.parentFile.mkdirs()
  URL(url).openStream().bufferedReader().use { input ->
    FileWriter(cacheDbFile).use { output ->
      input.copyTo(output)
    }
  }
  return cacheDbFile
}

fun <T> CachedResource<T>.justLoad(url: URL): T {
  return url.openStream().bufferedReader().use(loader)
}
