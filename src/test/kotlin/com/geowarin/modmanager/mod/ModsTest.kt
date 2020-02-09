package com.geowarin.modmanager.mod

import com.geowarin.modmanager.loadDb
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

internal class ModsTest {
  val db = loadDb(javaClass.getResource("/rwmsdb.json"))

  @TestFactory
  fun `Mod category`() = listOf(
  "Medical Tab" to "ui",
  "Pharmacist" to "medicine",
  "Feed The Colonists" to "ai",
  "Meals On Wheels" to "ai",
  "Advanced Shield Belts" to "combat",
  "Save Our Ship 2" to "gameplay",
  "Pep In Your Step" to "annoyance",
  "Snap Out!" to "ai",
  "[FSF] No Default Shelf Storage" to "annoyance",
  "Various Space Ship Chunk" to "resource",
  "[KV] RimFridge - 1.0" to "temperature",
  "[RF] Rational Romance [1.0]" to "ai",
  "Humanoid Alien Races 2.0" to "libs",
  "Vanilla Furniture Expanded - Security" to "combat",
  "Vanilla Furniture Expanded - Farming" to "resource",
  "[1.0] RPG Style Inventory" to "ui",
  "Tilled Soil (Rebalanced) - 120%" to "resource",
  "[KV] Show Hair With Hats or Hide All Hats - 1.0" to "appearance",
  "Better Infestations 1.0" to "animal",
  "Lights Tab" to null,
  "Vanilla Furniture Expanded - Production" to "production",
  "[v1.0]-LinkableDoors" to "texture",
  "[KV] Trading Spot - 1.0" to "ui",
  "[RF] Rumor Has It.... [1.0]" to "ai",
  "Vanilla Weapons Expanded - Laser" to null,
  "[1.0] Apparel Organizer" to "ui",
  "Vanilla Furniture Expanded - Art" to "furniture",
  "[1.0]-StockpileForDisaster" to "furniture",
  "[1.0] Android tiers" to "gameplay",
  "Let's Trade! [1.0]" to "trading",
  "Trade Ships Drop Spot" to null,
  "Vanilla Furniture Expanded - Medical Module" to "furniture",
  "Vanilla Furniture Expanded - Spacer Module" to "furniture"
  ).map { (modName, category) ->
    DynamicTest.dynamicTest("$modName should be in the $category category") {
      val cleanModName = cleanModName(modName)
      assertEquals(category, db[cleanModName], "$modName should be in the $category category")
    }
  }
}
