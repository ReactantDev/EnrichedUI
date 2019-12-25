package dev.reactant.uikit.image.segmentation.container

import dev.reactant.uikit.image.segmentation.SegmentedItemResource
import java.awt.image.BufferedImage


interface ResourceSegments : Iterable<SegmentedItemResource> {
    val identifier: String
    val originalImage: BufferedImage
}
