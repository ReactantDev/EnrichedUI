package dev.reactant.enrichedui.element.typography

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.ui.element.style.fillParent
import dev.reactant.reactant.ui.kits.ReactantUIDivElement
import dev.reactant.reactant.ui.kits.ReactantUIDivElementEditing
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.floor

enum class TypographyAlignment {
    Left, CenterLeft, CenterRight, Right
}


open class TypographyElementEditing<out T : TypographyElement>(element: T)
    : ReactantUIDivElementEditing<T>(element) {
    var text by MutablePropertyDelegate(element::text)
    var typography by MutablePropertyDelegate(element::typography)
    var alignment by MutablePropertyDelegate(element::alignment)
    var itemStackModifier by MutablePropertyDelegate(element::itemStackModifier)
}

fun ReactantUIElementEditing<ReactantUIElement>.typography(text: String = "", typography: Typography? = null, alignment: TypographyAlignment = TypographyAlignment.Left, creation: TypographyElementEditing<TypographyElement>.() -> Unit) {
    element.children.add(TypographyElement(element.allocatedSchedulerService)
            .also {
                it.text = text
                it.typography = typography
                it.alignment = alignment
            }
            .also { TypographyElementEditing(it).apply(creation) })
}

@UIElementName("typograph", "enriched-ui")
open class TypographyElement(allocatedSchedulerService: SchedulerService)
    : ReactantUIDivElement(allocatedSchedulerService) {
    init {
        height = actual(1)
        width = fillParent
    }

    var text: String = ""
        set(value) = run { field = value }.also { view?.render() }

    var typography: Typography? = null
        set(value) = run { field = value }.also { view?.render() }

    var alignment = TypographyAlignment.Left
        set(value) = run { field = value }.also { view?.render() }

    var itemStackModifier: (Pair<Int, Int>, ItemStack) -> ItemStack? = { _, it -> it }
        set(value) = run { field = value }.also { view?.render() }

    val length get() = (computedStyle?.offsetWidth ?: 0) * (typography?.charsAmount ?: 0)

    val emptySpaceAmount get() = (length - text.length).coerceAtLeast(0)

    val leftPad
        get() = when (alignment) {
            TypographyAlignment.Left -> 0
            TypographyAlignment.Right -> emptySpaceAmount
            TypographyAlignment.CenterLeft -> floor((emptySpaceAmount).toDouble() / 2).toInt()
            TypographyAlignment.CenterRight -> ceil((emptySpaceAmount).toDouble() / 2).toInt()
        }

    val rightPad
        get() = when (alignment) {
            TypographyAlignment.Left -> emptySpaceAmount
            TypographyAlignment.Right -> 0
            TypographyAlignment.CenterLeft -> ceil((emptySpaceAmount).toDouble() / 2).toInt()
            TypographyAlignment.CenterRight -> floor((emptySpaceAmount).toDouble() / 2).toInt()
        }

    val overflowedCharsAmount get() = (text.length - length).coerceAtLeast(0)

    val choppedContent
        get() = when (alignment) {
            TypographyAlignment.Left -> text.take(length)
            TypographyAlignment.Right -> text.takeLast(length)
            TypographyAlignment.CenterLeft -> ceil((overflowedCharsAmount).toDouble() / 2).toInt().let { leftExcess ->
                text.drop(leftExcess).dropLast(overflowedCharsAmount - leftExcess)
            }
            TypographyAlignment.CenterRight -> floor((overflowedCharsAmount).toDouble() / 2).toInt().let { leftExcess ->
                text.drop(leftExcess).dropLast(overflowedCharsAmount - leftExcess)
            }
        }

    var padChar = ' '

    val computedChars get() = "${padChar.toString().repeat(leftPad)}${choppedContent}${padChar.toString().repeat(rightPad)}"

    val charsSlices get() = computedChars.chunked(typography?.charsAmount ?: 1000)

    override fun render(relativePosition: Pair<Int, Int>): ItemStack? {
        if (charsSlices.size <= relativePosition.first) return null

        return typography?.resources?.get(charsSlices[relativePosition.first])?.similarItemStack?.let { itemStackModifier(relativePosition, it) }
    }

    override fun edit() = TypographyElementEditing(this)
}
