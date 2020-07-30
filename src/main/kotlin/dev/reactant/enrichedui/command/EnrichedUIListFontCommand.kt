package dev.reactant.enrichedui.command

import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.command.ConsoleCommandSender
import picocli.CommandLine
import java.awt.GraphicsEnvironment

@CommandLine.Command(
        name = "lsfont",
        aliases = [],
        mixinStandardHelpOptions = true,
        description = ["List all available system fonts name"]
)
internal class EnrichedUIListFontCommand : ReactantCommand() {
    override fun execute() {
        if (sender !is ConsoleCommandSender) sender.sendMessage("Only console can use this command")
        else GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames().forEach { stdout.out(it) }
    }
}
