package dev.reactant.enrichedui.command

import dev.reactant.enrichedui.element.frame.bgFrame
import dev.reactant.enrichedui.universal.UniversalResourceTable
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.ui.ReactantUIService
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
        name = "version",
        aliases = [],
        mixinStandardHelpOptions = true,
        description = ["Show a colorful version info <3"]
)
internal class EnrichedUIVersionInfoCommand(val uiService: ReactantUIService) : ReactantCommand() {
    override fun execute() {
        if (sender !is Player) sender.sendMessage("Only player is our target user... :C")
        else uiService.createUI(sender as Player, "Reactant Enriched UI Info") {
            view.setCancelModificationEvents(true)
            bgFrame(UniversalResourceTable.VERSION_BOARD) {

            }
        }
    }
}
