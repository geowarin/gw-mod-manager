package com.geowarin.modmanager.db

import com.geowarin.modmanager.ModLoaderPaths
import com.geowarin.modmanager.testUtils.write
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs.newFileSystem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.FileSystem


internal class RwmsDatabaseKtTest {

  @Test
  fun `load database and categories from cache`() {
    val fs: FileSystem = newFileSystem(Configuration.osX())
    ModLoaderPaths(fs).rwmsCache.write(
      """
      {
        "db": {
          "Core": "core"
        }
      }
    """.trimIndent()
    )
    ModLoaderPaths(fs).categoriesCache.write(
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

    assertThat(rwms.getModCategory("Core"))
      .isEqualTo(Category(prority = 42.0, fullName = "Core"))
  }
}

