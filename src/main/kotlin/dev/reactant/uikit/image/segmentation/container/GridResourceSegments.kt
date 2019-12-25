package dev.reactant.uikit.image.segmentation.container

import dev.reactant.uikit.image.segmentation.SegmentedItemResource

interface GridResourceSegments : ResourceSegments {
    val rows: Int
    val cols: Int
    var segments: Map<Pair<Int, Int>, SegmentedItemResource>

    override fun iterator(): Iterator<SegmentedItemResource> = segments.values.iterator()
}
