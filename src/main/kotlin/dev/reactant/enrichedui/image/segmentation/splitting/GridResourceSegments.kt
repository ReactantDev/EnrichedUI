package dev.reactant.enrichedui.image.segmentation.splitting

import dev.reactant.enrichedui.image.segmentation.SegmentedItemResource

interface GridResourceSegments : ResourceSegments {
    val rows: Int
    val cols: Int
    val segments: Map<Pair<Int, Int>, SegmentedItemResource>

    override fun iterator(): Iterator<SegmentedItemResource> = segments.values.iterator()
}
