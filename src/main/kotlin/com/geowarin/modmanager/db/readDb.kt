package com.geowarin.modmanager

import com.beust.klaxon.Klaxon
import java.net.URL

val categoriesUrl =
  "https://api.bitbucket.org/2.0/repositories/shakeyourbunny/rwmsdb/src/master/rwms_db_categories.json"
val databaseUrl = "https://api.bitbucket.org/2.0/repositories/shakeyourbunny/rwmsdb/src/master/rwmsdb.json"

fun loadDb(url: URL = URL(databaseUrl)): MutableMap<String, Any?> {
  val db = url.openStream().reader().use { reader ->
    val data = Klaxon().parseJsonObject(reader)
    data.obj("db")!!.map
  }
  return db
}
