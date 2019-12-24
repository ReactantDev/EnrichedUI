package dev.reactant.uikit.image.segmentation.container

import dev.reactant.uikit.image.segmentation.SegmentedItemResource

interface GridResourceSegments : ResourceSegments {
    val rows: Int
    val cols: Int
    val gridSegments: List<List<SegmentedItemResource>>
}
