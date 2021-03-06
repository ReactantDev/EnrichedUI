package dev.reactant.enrichedui.element.frame

import dev.reactant.enrichedui.image.segmentation.splitting.ItemResourceSegments
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.fillParent
import dev.reactant.reactant.ui.kits.ReactantUIDivElement
import dev.reactant.reactant.ui.kits.ReactantUIDivElementEditing
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@UIElementName("bgFrame", "enriched-ui")
open class FrameContainerElement(allocatedSchedulerService: SchedulerService, segments: ItemResourceSegments?)
    : ReactantUIDivElement(allocatedSchedulerService) {
    init {
        height = fillParent
        width = fillParent
        padding(0, 1)
    }

    var itemStackModifier: (Pair<Int, Int>, ItemStack) -> ItemStack? =
            { _, item -> item.apply { itemMeta<ItemMeta> { setDisplayName(" "); } } }
        set(value) = run { field = value }.also { view?.render() }

    var segments = segments
        set(value) = run { field = value }.also { view?.render() }

    override fun render(relativePosition: Pair<Int, Int>): ItemStack? {
        return segments?.run { segments[relativePosition]?.similarItemStack?.let { itemStackModifier(relativePosition, it) } }
    }


    override fun edit() = FrameContainerElementEditing(this)

}

open class FrameContainerElementEditing<out T : FrameContainerElement>(element: T)
    : ReactantUIDivElementEditing<T>(element) {
    var segments: ItemResourceSegments? by MutablePropertyDelegate(element::segments)
    var itemStackModifier: (Pair<Int, Int>, ItemStack) -> ItemStack? by MutablePropertyDelegate(element::itemStackModifier)
}

fun ReactantUIElementEditing<ReactantUIElement>.bgFrame(segments: ItemResourceSegments? = null, creation: FrameContainerElementEditing<FrameContainerElement>.() -> Unit) {
    element.children.add(FrameContainerElement(element.allocatedSchedulerService, segments)
            .also { FrameContainerElementEditing(it).apply(creation) })
}
