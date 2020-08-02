package dev.reactant.enrichedui.command.funny

import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.command.ConsoleCommandSender
import picocli.CommandLine

@CommandLine.Command(
        name = "fun",
        aliases = [],
        mixinStandardHelpOptions = true,
        description = ["Some funny example of enriched ui, enjoy!"]
)
internal class EnrichedUIFunCommand : ReactantCommand() {
    override fun execute() {
        showUsage()
    }
}
