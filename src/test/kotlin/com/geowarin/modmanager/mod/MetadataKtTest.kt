package com.geowarin.modmanager.mod

import com.gitlab.mvysny.konsumexml.konsumeXml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MetadataKtTest {

  @Test
  fun `parse metadata`() {
    assertEquals(
      ModMetaData("", "", "", ""),
      """<ModMetaData></ModMetaData>""".parseMeta()
    )

    assertEquals(
      """line1
        |line2
""".trimMargin(),
      """
      |<ModMetaData>
      |  <description>
      |  <![CDATA[line1
      |line2]]>
      |  </description>
      |</ModMetaData>
    """.trimMargin().parseMeta().description
    )

    // https://gitlab.com/mvysny/konsume-xml/issues/8
    assertEquals(
      """line1
        |line2
""".trimMargin(),
      """
      <ModMetaData>
        <description>
        line1
      line2
        </description>
      </ModMetaData>
    """.trimIndent().parseMeta().description
    )
  }

}

private fun String.parseMeta() =
  this.konsumeXml().child("ModMetaData") { ModMetaData.xml(this) }
