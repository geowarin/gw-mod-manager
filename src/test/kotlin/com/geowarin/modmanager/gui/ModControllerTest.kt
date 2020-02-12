package com.geowarin.modmanager.gui

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.testUtils.mockWith
import com.geowarin.modmanager.testUtils.write
import com.geowarin.modmanager.mod.ModStatus
import com.geowarin.modmanager.utils.getCacheDir
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import java.nio.file.FileSystem
import java.nio.file.Files

internal class ModControllerTest {

  @Test
  fun `load mods`() {
    FxToolkit.registerPrimaryStage()

    val fs: FileSystem = Jimfs.newFileSystem(Configuration.osX())
    getCacheDir(fs).mockWith("/cache")
    val rimworldPaths = RimworldPaths(fs)
    rimworldPaths.localModsFolder.mockWith("/localMods")
    rimworldPaths.steamModsFolder.mockWith("/steamMods")
    rimworldPaths.configFolder.resolve("ModsConfig.xml").write(
      """
      <ModsConfigData>
        <version>1.0.2408 rev749</version>
        <activeMods>
          <li>Core</li>
          <li>818773962</li>
        </activeMods>
      </ModsConfigData>
    """.trimIndent()
    )

    val modController = ModController()
    modController.loadMods(fs)

    assertEquals(
      listOf("Core", "HugsLib"),
      modController.activeMods.map { it.cleanModName }
    )

    assertEquals(
      listOf("Prepare Landing"),
      modController.inactiveMods.map { it.cleanModName }
    )

    val (core, hugsLib) = modController.activeMods
    modController.reorder(core, 1)

    assertEquals(
      listOf(
        "HugsLib" to ModStatus.ACTIVE_MOVED_UP,
        "Core" to ModStatus.ACTIVE_MOVED_DOWN
      ),
      modController.activeMods.map { it.cleanModName to it.status }
    )
    modController.saveModList(rimworldPaths)

    assertEquals(
      """
      <?xml version="1.0" encoding="utf-8"?>
      <ModsConfigData>
        <version>1.0.2408 rev749</version>
        <activeMods>
          <li>818773962</li>
          <li>Core</li>
        </activeMods>
      </ModsConfigData>""".trimIndent(),
      Files.newBufferedReader(rimworldPaths.configFolder.resolve("ModsConfig.xml")).readText()
    )
  }
}
