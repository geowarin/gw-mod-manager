package com.geowarin.modmanager.gui

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.db.testUtils.mockWith
import com.geowarin.modmanager.db.testUtils.write
import com.geowarin.modmanager.utils.getCacheDir
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Test
import java.nio.file.FileSystem
import kotlin.test.assertEquals

internal class ModControllerTest {

  @Test
  fun `load mods`() {
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

//    modController.activateMod()
  }
}
