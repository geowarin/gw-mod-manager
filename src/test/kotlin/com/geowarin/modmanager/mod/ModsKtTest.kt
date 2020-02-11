package com.geowarin.modmanager.mod

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.db.Rwms
import com.geowarin.modmanager.db.testUtils.mockWith
import com.geowarin.modmanager.utils.getCacheDir
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.FileSystem

internal class ModsKtTest {

  @Test
  fun `load mods`() {
    val fs: FileSystem = Jimfs.newFileSystem(Configuration.osX())
    getCacheDir(fs).mockWith("/cache")
    val rimworldPaths = RimworldPaths(fs)
    rimworldPaths.localModsFolder.mockWith("/localMods")
    rimworldPaths.steamModsFolder.mockWith("/steamMods")

    val rwms = Rwms(fs)
    rwms.load()

    val localMods = loadLocalMods(rwms, rimworldPaths)
    assertEquals(
      listOf("Core"),
      localMods.map { it.cleanModName }
    )

    val steamMods = loadSteamMods(rwms, rimworldPaths)
    assertEquals(
      listOf("Prepare Landing", "HugsLib"),
      steamMods.map { it.cleanModName }
    )
    assertEquals(
      listOf("1095331978", "818773962"),
      steamMods.map { it.modId }
    )
    assertEquals(
      listOf("PrepareLanding", null),
      steamMods.map { it.manifest?.identifier }
    )
    val (prepareLanding, hugsLib) = steamMods
    assertEquals(
      listOf("HugsLib"),
      prepareLanding.manifest?.dependencies
    )


  }
}
