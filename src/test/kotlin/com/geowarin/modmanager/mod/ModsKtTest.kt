package com.geowarin.modmanager.mod

import com.geowarin.modmanager.RimworldPaths
import com.geowarin.modmanager.db.Rwms
import com.geowarin.modmanager.db.testUtils.mockWith
import com.geowarin.modmanager.utils.getCacheDir
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Test
import java.nio.file.FileSystem
import kotlin.test.assertEquals

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
      localMods.map { it.modId }
    )

    val steamMods = loadSteamMods(rwms, rimworldPaths)
    assertEquals(
      listOf("1095331978"),
      steamMods.map { it.modId }
    )
  }
}
