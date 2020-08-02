package dev.reactant.enrichedui.command.funny

import dev.reactant.enrichedui.EnrichedUIResourceTable
import dev.reactant.enrichedui.element.frame.bgFrame
import dev.reactant.enrichedui.element.typography.typography
import dev.reactant.enrichedui.universal.FaIconsTable
import dev.reactant.enrichedui.universal.UniversalResourceTable
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.ui.ReactantUIService
import dev.reactant.reactant.ui.element.style.inline
import dev.reactant.reactant.ui.kits.div
import dev.reactant.reactant.ui.kits.item
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
        name = "typing",
        aliases = [],
        mixinStandardHelpOptions = true,
        description = ["Show a funny typing machine"]
)
internal class EnrichedUITypingMachineCommand(
        private val uiService: ReactantUIService
) : ReactantCommand() {

    val typography
        get() = UniversalResourceTable.TYPOGRAPHY.ASCII.NOTO_SANS_MONO_2CHAR

    override fun execute() {
        requireSenderIsPlayer()

        uiService.createUI(sender as Player, "Reactant Enriched UI Typography") {
            view.setCancelModificationEvents(true)

            val machineText = BehaviorSubject.createDefault("")

            bgFrame(EnrichedUIResourceTable.TYPOGRAPHY_TEST_FRAME) {
                div {
                    typography(typography = typography) {
                        subscribe(machineText) { this.text = it }
                    }

                    div {
                        (65..90).map { it.toChar() }.forEach { char ->
                            typography(char.toString(), typography) {
                                display = inline
                                size(1, 1)

                                onClick.subscribe { machineText.onNext(machineText.value + char) }
                            }
                        }

                        item(FaIconsTable.FA_BACKSPACE_SOLID.similarItemStack) {
                            onClick.subscribe { machineText.onNext(machineText.value.dropLast(1)) }
                        }
                    }
                }
            }
        }
    }
}
