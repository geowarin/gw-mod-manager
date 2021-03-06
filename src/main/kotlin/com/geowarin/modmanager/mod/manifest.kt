package com.geowarin.modmanager.mod

import com.geowarin.modmanager.utils.exists
import com.geowarin.modmanager.utils.konsumeXml
import com.gitlab.mvysny.konsumexml.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

data class ModVersion(
  val version: String
)

data class Dependency(
  val modId: String,
  val modVersionSpec: String
)

data class ModManifest(
  val identifier: String,
  val version: String,
  val dependencies: List<String>,
  val incompatibleWith: List<String>,
  val loadBefore: List<String>,
  val loadAfter: List<String>,
  val suggests: List<String>
) {
  companion object {
    fun xml(k: Konsumer): ModManifest {
      k.checkCurrent("Manifest")

      var identifier = ""
      var version = ""
      var dependencies = emptyList<String>()
      var incompatibleWith = emptyList<String>()
      var loadBefore = emptyList<String>()
      var loadAfter = emptyList<String>()
      var suggests = emptyList<String>()
      k.allChildrenAutoIgnore(
        Names.of(
          "identifier",
          "version",
          "dependencies",
          "incompatibleWith",
          "loadBefore",
          "loadAfter",
          "suggests"
        )
      ) {
        when (this.name?.localPart) {
          "identifier" -> identifier = this.text()
          "version" -> version = this.text()
          "dependencies" ->  dependencies = this.childrenText("li")
          "incompatibleWith" -> incompatibleWith = this.childrenText("li")
          "loadBefore" -> loadBefore = this.childrenText("li")
          "loadAfter" -> loadAfter = this.childrenText("li")
          "suggests" -> suggests = this.childrenText("li")
          else -> this.skipContents()
        }
      }
      return ModManifest(identifier, version, dependencies, incompatibleWith, loadBefore, loadAfter, suggests)
    }
  }
}

fun parseManifest(mod: Path): ModManifest? {
  val aboutFile = mod.resolve("About/Manifest.xml")
  if (!aboutFile.exists()) {
    return null
  }
  return aboutFile.konsumeXml().child("Manifest") { ModManifest.xml(this) }
}

