package dev.reactant.enrichedui.command.funny

import dev.reactant.enrichedui.EnrichedUI
import dev.reactant.enrichedui.EnrichedUIResourceTable
import dev.reactant.enrichedui.element.frame.bgFrame
import dev.reactant.enrichedui.element.typography.TypographyAlignment
import dev.reactant.enrichedui.element.typography.typography
import dev.reactant.enrichedui.universal.FaIconsTable
import dev.reactant.enrichedui.universal.UniversalResourceTable
import dev.reactant.reactant.extensions.trySetColor
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.ui.ReactantUIService
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.ui.kits.div
import dev.reactant.reactant.ui.kits.item
import dev.reactant.reactant.ui.kits.span
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.bukkit.Color
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
        name = "typography",
        aliases = [],
        mixinStandardHelpOptions = true,
        description = ["Show typography rgb gui"]
)
internal class EnrichedUITextRGBCommand(
        private val uiService: ReactantUIService
) : ReactantCommand() {
    @CommandLine.Parameters(arity = "1", paramLabel = "TEXT",
            description = ["Text to be show with typography", "Hints: You can use \"SOME TEXT\" to wrap your space character"])
    var text: String = ""

    @CommandLine.Option(names = ["-n", "--number-only"], description = ["Only allow number char to enable 3 chars 1 item"])
    var numberOnly = false

    val typography
        get() = if (numberOnly) UniversalResourceTable.TYPOGRAPHY.NUMBER.NOTO_SANS_MONO_3CHAR
        else UniversalResourceTable.TYPOGRAPHY.ASCII.NOTO_SANS_MONO_2CHAR

    override fun execute() {
        requireSenderIsPlayer()

        text.filter { !typography.supportedChars.contains(it) }.let {
            if (it.isNotEmpty()) stderr.out("Typography ${typography.identifier} do not support the following chars: [${it.map { "'${it}'" }.joinToString(", ")}]")
            else {

                uiService.createUI(sender as Player, "Reactant Enriched UI Typography") {
                    view.setCancelModificationEvents(true)

                    val showingText = BehaviorSubject.createDefault(text)
                    val showingAlignment = BehaviorSubject.createDefault(TypographyAlignment.Left)
                    val r = BehaviorSubject.createDefault(0)
                    val g = BehaviorSubject.createDefault(0)
                    val b = BehaviorSubject.createDefault(0)

                    @Suppress("UNCHECKED_CAST")
                    val rgbObservable = Observables.combineLatest(r, g, b) { nr: Int, ng: Int, nb: Int -> arrayOf(nr, ng, nb) }

                    bgFrame(EnrichedUIResourceTable.TYPOGRAPHY_TEST_FRAME) {
                        div {

                            typography(showingText.value, typography, showingAlignment.value) {
                                id = "text"
                                subscribe(showingText) { text = it }
                                subscribe(showingAlignment) { alignment = it }
                                subscribe(rgbObservable) { rgb ->
                                    itemStackModifier = { _, item -> item.apply { trySetColor(Color.fromRGB(rgb[0], rgb[1], rgb[2])) } }
                                }
                            }

                            typography("ButterJar suck", UniversalResourceTable.TYPOGRAPHY.ASCII.NOTO_SANS_MONO_2CHAR) {}


                            div {
                                padding(1, 0)

                                listOf(r, g, b).forEachIndexed { index, channel ->
                                    span {
                                        size(1, 3)
                                        item(FaIconsTable.FA_ARROW_UP_SOLID.similarItemStack) {
                                            onClick.subscribe {
                                                channel.onNext((channel.value + 15).coerceAtMost(255))
                                                EnrichedUI.logger.info(channel.value)
                                            }
                                        }

                                        typography(channel.value.toString(), typography, TypographyAlignment.Right) {
                                            subscribe(channel) { text = channel.value.toString() }
                                        }

                                        item(FaIconsTable.FA_ARROW_DOWN_SOLID.similarItemStack) {
                                            onClick.subscribe { channel.onNext((channel.value - 15).coerceAtLeast(0)) }
                                        }
                                    }
                                }

                                span {
                                    padding(1, 0)
                                    marginLeft = actual(1)

                                    item(FaIconsTable.FA_ALIGN_LEFT_SOLID.similarItemStack) {
                                        onClick.subscribe { showingAlignment.onNext(TypographyAlignment.Left) }
                                    }

                                    item(FaIconsTable.FA_ALIGN_CENTER_SOLID.similarItemStack) {
                                        onClick.subscribe { showingAlignment.onNext(TypographyAlignment.CenterLeft) }
                                    }

                                    item(FaIconsTable.FA_ALIGN_RIGHT_SOLID.similarItemStack) {
                                        onClick.subscribe { showingAlignment.onNext(TypographyAlignment.Right) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
