package dev.reactant.uikit

import dev.reactant.reactant.core.ReactantPlugin
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.plugin.java.JavaPlugin

@ReactantPlugin(["dev.reactant.uikit"])
class UIKit : JavaPlugin() {

    companion object {
        @JvmStatic
        internal val log: Logger = LogManager.getLogger("UIKit")

        const val configFolder = "plugins/UIKit/";
    }

}
