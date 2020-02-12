package com.geowarin.modmanager.mod

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.testUtils.readText
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.FileSystem
import java.nio.file.Files

internal class ModsConfigTest {

  @Test
  fun save() {
    val fs: FileSystem = Jimfs.newFileSystem(Configuration.osX())
    val rimworldPaths = RimworldPaths(fs)
    Files.createDirectories(rimworldPaths.configFolder)

    ModsConfig(
      rimworldVersion = "1.0.2408 rev749",
      activeMods = listOf("Core", "818773962")
    ).save(rimworldPaths)

    Assertions.assertThat(
      rimworldPaths.configFolder.resolve("ModsConfig.xml").readText()
    ).isEqualToNormalizingWhitespace(
      """
      <?xml version="1.0" encoding="utf-8"?>
      <ModsConfigData>
        <version>1.0.2408 rev749</version>
        <activeMods>
          <li>Core</li>
          <li>818773962</li>
        </activeMods>
      </ModsConfigData>"""
    )
  }
}
