package dev.reactant.enrichedui.command.funny

import dev.reactant.enrichedui.EnrichedUIResourceTable
import dev.reactant.enrichedui.element.frame.bgFrame
import dev.reactant.enrichedui.element.typography.TypographyAlignment
import dev.reactant.enrichedui.element.typography.typography
import dev.reactant.enrichedui.universal.FaIconsTable
import dev.reactant.enrichedui.universal.UniversalResourceTable
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.reactant.extensions.trySetColor
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.ui.ReactantUIService
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.ui.element.style.fixed
import dev.reactant.reactant.ui.element.style.inline
import dev.reactant.reactant.ui.kits.div
import dev.reactant.reactant.ui.kits.item
import dev.reactant.reactant.utils.content.item.airItemStack
import dev.reactant.reactant.utils.content.item.itemStackOf
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import picocli.CommandLine
import kotlin.math.pow

@CommandLine.Command(
        name = "piano",
        aliases = [],
        mixinStandardHelpOptions = true,
        description = ["Show a piano"]
)
internal class EnrichedUIPianoCommand(
        private val uiService: ReactantUIService
) : ReactantCommand() {

    val typography = UniversalResourceTable.TYPOGRAPHY.ASCII.NOTO_SANS_MONO_2CHAR
    val effectTime = 20

    override fun execute() {
        requireSenderIsPlayer()

        uiService.createUI(sender as Player, "Reactant Enriched UI Typography") {
            view.setCancelModificationEvents(true)

            val tick = BehaviorSubject.createDefault(0)
            element.scheduler.interval(1).subscribe(tick)

            val tunings = listOf("G", "A", "B", "C", "D", "E", "F")
            val tuningsPitchRate = listOf(1, 3, 5, 6, 8, 10, 11)
            val keysLastClick = tunings.map { BehaviorSubject.createDefault(-effectTime) }.toTypedArray()
            val octaveUp = BehaviorSubject.createDefault(false)

            val keysEffectRemainTime = keysLastClick.map {
                Observables.combineLatest(it, tick).map { (keysLastClickTicks, currentTicks) ->
                    ((keysLastClickTicks + effectTime) - currentTicks).coerceAtLeast(0)
                }
            }.toTypedArray()

            bgFrame(EnrichedUIResourceTable.TYPOGRAPHY_TEST_FRAME) {
                div {
                    keysEffectRemainTime.mapIndexed { index, effectRemainTime ->
                        typography(tunings[index], typography, TypographyAlignment.Left) {
                            display = inline
                            size(1, 1)
                            subscribe(effectRemainTime) { time ->
                                itemStackModifier = { _, item ->
                                    if (time == 0) itemStackOf(Material.AIR)
                                    else item.apply { trySetColor(Color.fromRGB(((time.toDouble() / effectTime) * 255).toInt(), 0, 0)) }
                                }
                            }
                        }
                    }
                }

                div {
                    marginTop = actual(2)
                    size(3, 1)

                    item {
                        subscribe(octaveUp) {
                            slotItem =
                                    if (it) FaIconsTable.FA_ANGLE_UP_SOLID.similarItemStack
                                    else airItemStack()
                        }
                    }
                }

                item(FaIconsTable.FA_MUSIC_SOLID.similarItemStack.apply {
                    itemMeta<ItemMeta> {
                        setDisplayName("Reactant's Piano!")
                        lore = listOf(
                                "Hover your mouse on this icon, and press your hotbar key!",
                                "  1~7 to play from G to F",
                                "  9 to toggle octave level"
                        )
                    }
                }) {
                    position = fixed
                    bottom = actual(0)
                    right = actual(0)
                    marginTop = actual(2)
                    onClick.subscribe {
                        if (it.bukkitEvent.hotbarButton != -1) {
                            when {
                                it.bukkitEvent.hotbarButton < 7 -> {
                                    keysLastClick[it.bukkitEvent.hotbarButton].onNext(tick.value)
                                    val pitch = 2.0.pow((12.0 * (if (octaveUp.value) 0 else -1) + tuningsPitchRate[it.bukkitEvent.hotbarButton]) / 12)
                                    it.player.playSound(it.player.location, Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, pitch.toFloat())
                                }
                                it.bukkitEvent.hotbarButton == 8 -> octaveUp.onNext(!octaveUp.value)
                            }
                        }
                    }
                }

            }
        }
    }
}
