package com.geowarin.modmanager.mod

import com.geowarin.modmanager.utils.konsumeXml
import com.gitlab.mvysny.konsumexml.Names
import java.io.File
import java.nio.file.Path

data class ModConfig(
  val version: String,
  val activeMods: List<String>
)

fun parseModsConfig(configFolder: Path): ModConfig {
  val modsConfigFile = configFolder.resolve("ModsConfig.xml")
  modsConfigFile.konsumeXml().use {
    val konsumer = it.nextElement(Names.of("ModsConfigData"), true)!!
    val version = konsumer.childText("version")
    val activeMods: List<String> = konsumer.child("activeMods") {
      this.childrenText("li")
    }
    return ModConfig(version, activeMods)
  }
}
