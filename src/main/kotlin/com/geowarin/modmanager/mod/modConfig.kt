package com.geowarin.modmanager.mod

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.utils.konsumeXml
import com.gitlab.mvysny.konsumexml.Names
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.xml
import java.nio.file.Files
import java.nio.file.Path

data class ModsConfig(
  val rimworldVersion: String,
  val activeMods: List<String>
) {
  fun save(paths: RimworldPaths) {
    val people = xml("ModsConfigData") {
      globalProcessingInstruction("xml", "version" to "1.0", "encoding" to "utf-8")
      "version" {
        -rimworldVersion
      }
      "activeMods" {
        for (activeMod in activeMods) {
          "li" {
            -activeMod
          }
        }
      }
    }
    Files.newBufferedWriter(paths.configFolder.resolve("ModsConfig.xml")).use {
      it.write(people.toString(PrintOptions(singleLineTextElements = true)))
    }
  }
}

fun parseModsConfig(configFolder: Path): ModsConfig {
  val modsConfigFile = configFolder.resolve("ModsConfig.xml")
  modsConfigFile.konsumeXml().use {
    val konsumer = it.nextElement(Names.of("ModsConfigData"), true)!!
    val version = konsumer.childText("version")
    val activeMods: List<String> = konsumer.child("activeMods") {
      this.childrenText("li")
    }
    return ModsConfig(version, activeMods)
  }
}
