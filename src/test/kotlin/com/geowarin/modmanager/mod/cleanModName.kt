package com.geowarin.modmanager.mod

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

internal class cleanModName {

  @TestFactory
  fun `clean mode name`() = listOf(
    "[KV] RimFridge - 1.0" to "[KV] RimFridge",
    "[RF] Rational Romance [1.0]" to "[RF] Rational Romance",
    "Humanoid Alien Races 2.0" to "Humanoid Alien Races",

    "Vanilla Furniture Expanded - Security" to "Vanilla Furniture Expanded: Security",

    "[1.0] RPG Style Inventory" to "RPG Style Inventory",
    "Tilled Soil (Rebalanced) - 120%" to "Tilled Soil (Rebalanced)",

    "Better Infestations 1.0" to "Better Infestations",
    "[v1.0]-LinkableDoors" to "LinkableDoors",

    "[KV] Trading Spot - 1.0" to "[KV] Trading Spot",
    "[1.0]-StockpileForDisaster" to "StockpileForDisaster"
  ).map { (modName, cleaned) ->
    DynamicTest.dynamicTest("$modName should be cleaned to $cleaned") {
      val cleanModName = cleanModName(modName)
      assertEquals(cleaned, cleanModName, "$modName should be cleaned to $cleaned")
    }
  }
}
