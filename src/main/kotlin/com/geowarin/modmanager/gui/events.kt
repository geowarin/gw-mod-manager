package com.geowarin.modmanager.gui

import com.geowarin.modmanager.mod.Mod
import tornadofx.*

object ModsListRequest : FXEvent(EventBus.RunOn.BackgroundThread)
class ModsListEvent(val mods: List<Mod>, val activeMods: List<Mod>) : FXEvent()
