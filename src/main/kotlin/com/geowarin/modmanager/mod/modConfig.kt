package com.geowarin.modmanager.mod

import com.gitlab.mvysny.konsumexml.Names
import com.gitlab.mvysny.konsumexml.konsumeXml
import java.io.File

data class ModConfig(
  val version: String,
  val activeMods: List<String>
)

fun parseModsConfig(configFolder: File): ModConfig {
  val modsConfigFile = File(configFolder, "ModsConfig.xml")
  modsConfigFile.konsumeXml().use {
    val konsumer = it.nextElement(Names.of("ModsConfigData"), true)!!
    val version = konsumer.childText("version")
    val activeMods: List<String> = konsumer.child("activeMods") {
      this.childrenText("li")
    }
    return ModConfig(version, activeMods)
  }
}
