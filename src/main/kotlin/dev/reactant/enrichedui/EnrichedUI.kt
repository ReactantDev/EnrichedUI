package dev.reactant.enrichedui

import dev.reactant.reactant.core.ReactantPlugin
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.plugin.java.JavaPlugin

@ReactantPlugin(["dev.reactant.enrichedui"])
class EnrichedUI : JavaPlugin() {

    companion object {
        @JvmStatic
        internal val logger: Logger = LogManager.getLogger("EnrichedUI")

        const val configFolder = "plugins/EnrichedUI/";
    }

}
