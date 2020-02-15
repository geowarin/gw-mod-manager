package com.geowarin.modmanager.gui

import com.geowarin.modmanager.ModLoaderPaths
import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.getCacheDir
import com.geowarin.modmanager.mod.ModStatus
import com.geowarin.modmanager.mod.ModsConfig
import com.geowarin.modmanager.mod.MultiplayerCompat
import com.geowarin.modmanager.mod.MultiplayerCompatLevel
import com.geowarin.modmanager.testUtils.mockWith
import com.geowarin.modmanager.testUtils.write
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import java.nio.file.FileSystem
import java.nio.file.Files

internal class ModControllerTest {

  @BeforeEach
  fun setup() {
    FxToolkit.registerPrimaryStage()
  }

  val hugsLibId = "818773962"
  val prepareLandingId = "1095331978"

  @Test
  fun `load mods`() {
    val (fs, rimworldPaths) = mockPaths(
      ModsConfig(
        rimworldVersion = "1.0.2408 rev749",
        activeMods = listOf("Core", hugsLibId)
      )
    )

    val modController = ModController()
    modController.loadMods(fs)

    assertThat(modController.activeMods.map { it.cleanModName })
      .containsExactly("Core", "HugsLib")
    assertThat(modController.inactiveMods.map { it.cleanModName })
      .containsExactly("Prepare Landing", "Allow Tool")

    val (prepareLanding, allowTools) = modController.inactiveMods
    assertThat(prepareLanding.multiplayerCompat).isEqualTo(MultiplayerCompat(MultiplayerCompatLevel.WORKS, ""))
    assertThat(allowTools.multiplayerCompat).isEqualTo(MultiplayerCompat(MultiplayerCompatLevel.DOES_NOT_WORK, "Forbid, unforbid, select similar, haul urgently and finish off doesn't work."))
  }

  @Test
  fun `sort mods`() {
    val (fs, rimworldPaths) = mockPaths(
      ModsConfig(
        rimworldVersion = "1.0.2408 rev749",
        activeMods = listOf(hugsLibId, "Core")
      )
    )
    val modController = ModController()
    modController.loadMods(fs)
    modController.sortMods()
    assertThat(modController.activeMods.map { it.cleanModName to it.status })
      .containsExactly(
        "Core" to ModStatus.ACTIVE_MOVED_UP,
        "HugsLib" to ModStatus.ACTIVE_MOVED_DOWN
      )
  }

  @Test
  fun `reorder mods`() {
    val (fs, rimworldPaths) = mockPaths(
      ModsConfig(
        rimworldVersion = "1.0.2408 rev749",
        activeMods = listOf("Core", hugsLibId)
      )
    )

    val modController = ModController()
    modController.loadMods(fs)

    val (core, hugsLib) = modController.activeMods
    modController.reorder(core, 1)

    assertThat(modController.activeMods.map { it.cleanModName to it.status })
      .containsExactly(
        "HugsLib" to ModStatus.ACTIVE_MOVED_UP,
        "Core" to ModStatus.ACTIVE_MOVED_DOWN
      )
  }

  @Test
  fun `override mod category`() {
    val (fs, rimworldPaths) = mockPaths(
      ModsConfig(
        rimworldVersion = "1.0.2408 rev749",
        activeMods = listOf("Core", hugsLibId)
      )
    )
    ModLoaderPaths(fs).dbOverrides
      .write("""
        {
          "HugsLib": "joy"
        }
      """.trimIndent())

    val modController = ModController()
    modController.loadMods(fs)

    val hugsLib = modController.activeMods.first { it.modId == hugsLibId }
    assertThat(hugsLib.category.fullName)
      .isEqualTo("joy items")
  }

  private fun mockPaths(modsConfig: ModsConfig): Pair<FileSystem, RimworldPaths> {
    val fs: FileSystem = Jimfs.newFileSystem(Configuration.osX())
    getCacheDir(fs).mockWith("/cache")
    val rimworldPaths = RimworldPaths(fs)
    rimworldPaths.localModsFolder.mockWith("/localMods")
    rimworldPaths.steamModsFolder.mockWith("/steamMods")

    Files.createDirectories(rimworldPaths.configFolder)
    modsConfig.save(rimworldPaths)
    return Pair(fs, rimworldPaths)
  }

}
