package dev.reactant.enrichedui.command

import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.command.ConsoleCommandSender
import picocli.CommandLine

@CommandLine.Command(
        name = "enrichedui",
        aliases = [],
        mixinStandardHelpOptions = true,
        description = []
)
internal class EnrichedUICommand : ReactantCommand() {
    override fun execute() {
        showUsage()
    }
}
