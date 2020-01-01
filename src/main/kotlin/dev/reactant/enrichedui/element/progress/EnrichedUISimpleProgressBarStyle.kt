package dev.reactant.enrichedui.element.progress

import dev.reactant.resourcestirrer.itemresource.ClassLoaderItemResource
import dev.reactant.resourcestirrer.itemresource.ItemModelModifiers
import dev.reactant.resourcestirrer.resourceloader.ClassLoaderResourceLoader
import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.uikit.element.progress.ProgressBarRenderInfo
import dev.reactant.uikit.element.progress.ProgressBarTheme
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt

typealias LayeredTextures = Map<String, String>

class EnrichedUISimpleProgressBarStyle(
        val resourceLoader: ClassLoaderResourceLoader,
        val identifier: String,
        val head: Map<Int, LayeredTextures>,
        val middle: Map<Int, LayeredTextures>,
        val tail: Map<Int, LayeredTextures>,
        val baseItem: Material = Material.FIREWORK_STAR
) : ProgressBarTheme, Iterable<ClassLoaderItemResource> {
    val maxValue = head.keys.union(middle.keys).union(tail.keys).max()!!
    val resources = listOf("head", "middle", "tail").zip(listOf(head.entries, middle.entries, tail.entries))
            .map { (position, valuePathMap) ->
                valuePathMap.groupBy { it.value }.map { it ->
                    it.key to ClassLoaderItemResource(resourceLoader, null,
                            it.key,
                            "$identifier-$position-${it.value.map { it.key }.min()}",
                            baseItem, null, mapOf())
                            .applyModifier(ItemModelModifiers.scaleAsGUI(1.0, 1.0))
                }
            }
            .flatten()
            .toMap()

    val middleNotOverlappedByHeadSteps = middle.keys.max()!! - head.keys.max()!!

    val tailNotOverLappedByMiddleSteps = tail.keys.max()!! - middle.keys.max()!!

    val headNotOverlappedByMiddleSteps = middle.keys.min()!! - head.keys.min()!!

    val middleNotOverlappedByTailSteps = tail.keys.min()!! - middle.keys.min()!!


    override fun iterator(): Iterator<ClassLoaderItemResource> = resources.values.iterator()

    fun getRepresentableSteps(width: Int): Int {
        if (width < 0) throw IllegalArgumentException()
        return when (width) {
            0 -> 0
            1 -> head.keys.max()!! - head.keys.min()!! + 1
            else -> (width - 2) * middleNotOverlappedByHeadSteps + head.keys.max()!! - head.keys.min()!!+1  + tailNotOverLappedByMiddleSteps
        }
    }

    fun getCurrentItemTexture(itemIndex: Int, width: Int, globalStep: Int): LayeredTextures {
        if (width < 0) throw IllegalArgumentException()
        val isHead = itemIndex == 0
        val isTail = itemIndex == width - 1
        val currentItemRepresentingRange = when (width) {
            0 -> 0..head.keys.max()!!
            1 -> when {
                isHead -> head.keys.min()!!..head.keys.max()!!
                else -> tail.keys.min()!!..head.keys.max()!!
            }
            else -> when {
                isHead -> head.keys.min()!!..head.keys.max()!!
                isTail -> {
                    ((headNotOverlappedByMiddleSteps + (width - 2) * middleNotOverlappedByTailSteps) + 1)..getRepresentableSteps(width)
                }
                else -> {
                    ((headNotOverlappedByMiddleSteps + (itemIndex - 1) * middleNotOverlappedByTailSteps) + 1)..((headNotOverlappedByMiddleSteps + (itemIndex) * middleNotOverlappedByTailSteps))
                }
            }
        }

        val stepFromMin = when {
            globalStep in currentItemRepresentingRange -> globalStep - currentItemRepresentingRange.min()!!
            currentItemRepresentingRange.min()!! > globalStep -> 0
            currentItemRepresentingRange.max()!! < globalStep -> currentItemRepresentingRange.max()!!
            else -> throw IllegalStateException()
        }
        val index = stepFromMin + when {
            isHead -> head.keys.min()!!
            isTail -> tail.keys.min()!!
            else -> middle.keys.min()!!
        }


        fun getClosest(wanted: Int, map: Map<Int, LayeredTextures>): LayeredTextures = map.entries.sortedBy { it.key }.run {
            (firstOrNull { it.key >= wanted } ?: last()).value
        }
        return when {
            isHead -> getClosest(index, head)
            isTail -> getClosest(index, tail)
            else -> getClosest(index, middle)
        }
    }

    override fun invoke(info: ProgressBarRenderInfo): ItemStack? {
        val totalDisplayableSteps = getRepresentableSteps(info.barItemLength)
        val convertedStep = (info.value * totalDisplayableSteps).roundToInt()

        val result = getCurrentItemTexture(info.barItemIndex, info.barItemLength, convertedStep)
        return resources[result]!!.similarItemStack
    }
}

/**
 * Single layer texture progress bar
 */
fun ItemResourcesTable.createSingleLayerTextureProgressBar(
        identifier: String,
        head: Map<Int, String>,
        middle: Map<Int, String>,
        tail: Map<Int, String>,
        baseItem: Material = Material.RED_STAINED_GLASS_PANE,
        layerIndex: Int = 0
) = EnrichedUISimpleProgressBarStyle(this.resourceLoader, "${this.identifierPrefix}-$identifier",
        head.mapValues { mapOf("layer$layerIndex" to it.value) },
        middle.mapValues { mapOf("layer$layerIndex" to it.value) },
        tail.mapValues { mapOf("layer$layerIndex" to it.value) }, baseItem)

/**
 * Multi layer texture progress bar
 */
fun ItemResourcesTable.createMultiLayerTextureProgressBar(
        identifier: String,
        head: Map<Int, LayeredTextures>,
        middle: Map<Int, LayeredTextures>,
        tail: Map<Int, LayeredTextures>,
        baseItem: Material
) = EnrichedUISimpleProgressBarStyle(this.resourceLoader, "${this.identifierPrefix}-$identifier", head, middle, tail, baseItem)

/**
 * Create colorable progress bar with single layer grayscale texture
 * Using firework start as base item
 */
fun ItemResourcesTable.createColorableProgressBar(
        identifier: String,
        head: Map<Int, String>,
        middle: Map<Int, String>,
        tail: Map<Int, String>
) = createSingleLayerTextureProgressBar(identifier, head, middle, tail, Material.LEATHER_BOOTS)
