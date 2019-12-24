package dev.reactant.uikit.element.frame

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.PositioningStylePropertyValue
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.ui.element.style.fillParent
import dev.reactant.reactant.ui.kits.ReactantUIDivElement
import dev.reactant.reactant.ui.kits.ReactantUIDivElementEditing
import dev.reactant.uikit.image.segmentation.container.EqualPartsResourceSegments
import org.bukkit.inventory.ItemStack

@UIElementName("frame", "uikit")
open class FrameContainerElement(allocatedSchedulerService: SchedulerService, var segments: EqualPartsResourceSegments) : ReactantUIDivElement(allocatedSchedulerService) {

    override val elementIdentifier = "frame"

    override var height: PositioningStylePropertyValue = fillParent
    override var width: PositioningStylePropertyValue = fillParent

    override var padding: List<PositioningStylePropertyValue> = arrayListOf(actual(1));

    override fun render(relativePosition: Pair<Int, Int>): ItemStack? {
        val (x, y) = relativePosition
        val row = y / 3
        val col = when {
            x == 0 -> when {
                y % 3 == 1 -> 0
                y % 3 == 2 -> 1
                else -> null
            }
            x == 8 -> if (y % 3 == 2) 2 else null
            else -> null
        }
        return if (col != null && row < segments.size) segments.gridSegments[row][col].similarItemStack else null
    }


    override fun edit() = FrameContainerElementEditing(this)

}

open class FrameContainerElementEditing<out T : FrameContainerElement>(element: T)
    : ReactantUIDivElementEditing<T>(element) {
    var segments: EqualPartsResourceSegments
        get() = this.element.segments
        set(value) {
            this.element.segments = value
        }
}

fun ReactantUIElementEditing<ReactantUIElement>.bgFrame(segments: EqualPartsResourceSegments, creation: ReactantUIDivElementEditing<FrameContainerElement>.() -> Unit) {
    element.children.add(FrameContainerElement(element.allocatedSchedulerService, segments)
            .also { FrameContainerElementEditing(it).apply(creation) })
}
