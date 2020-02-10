package com.geowarin.modmanager.mod

import com.gitlab.mvysny.konsumexml.Konsumer
import com.gitlab.mvysny.konsumexml.Names
import com.gitlab.mvysny.konsumexml.allChildrenAutoIgnore
import com.gitlab.mvysny.konsumexml.konsumeXml
import java.io.File

data class ModMetaData(
  val name: String = "",
  val author: String = "",
  val description: String = "",
  val url: String = ""
) {
  companion object {
    fun xml(k: Konsumer): ModMetaData {
      k.checkCurrent("ModMetaData")

      var name = ""
      var author = ""
      var description = ""
      var url = ""
      k.allChildrenAutoIgnore(
        Names.of(
          "name",
          "author",
          "description",
          "url"
        )
      ) {
        when (this.name?.localPart) {
          "name" -> name = this.text()
          "author" -> author = this.text()
          "description" -> description = this.text()
          "url" -> url = this.text()
          else -> this.skipContents()
        }
      }
      return ModMetaData(name, author, description, url)
    }
  }
}

fun parseMetadata(mod: File): ModMetaData? {
  val aboutFile = File(mod, "About/About.xml")
  if (!aboutFile.exists()) {
    return null
  }
  return aboutFile.konsumeXml().child("ModMetaData") { ModMetaData.xml(this) }
}
