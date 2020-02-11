package com.geowarin.modmanager.db

import com.geowarin.modmanager.db.testUtils.mockWith
import com.geowarin.modmanager.db.testUtils.write
import com.geowarin.modmanager.utils.getCacheDir
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs.newFileSystem
import org.junit.jupiter.api.Test
import java.nio.file.FileSystem
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal class RwmsDatabaseKtTest {

  @Test
  fun test() {
    val fs: FileSystem = newFileSystem(Configuration.osX())
    getCacheDir(fs).resolve("rwmsdb.json").write(
      """
      {
        "db": {
          "Core": "core"
        }
      }
    """.trimIndent()
    )
    getCacheDir(fs).resolve("categories.json").write(
      """
      {
        "core": [
          42.0,
          "Core"
         ]
      }
    """.trimIndent()
    )

    val rwms = Rwms(fs)
    rwms.load()

    assertEquals(
      "core",
      rwms.db["Core"]
    )
    assertEquals(
      "Core",
      rwms.categories["core"]?.fullName
    )
    assertEquals(
      42.0,
      rwms.categories["core"]?.prority
    )
  }

  @Test
  fun test2() {
    val fs: FileSystem = newFileSystem(Configuration.osX())
    getCacheDir(fs).mockWith("/cache")

    val rwms = Rwms(fs)
    rwms.load()

    assertTrue(rwms.db.isNotEmpty())
    assertTrue(rwms.categories.isNotEmpty())
  }
}

