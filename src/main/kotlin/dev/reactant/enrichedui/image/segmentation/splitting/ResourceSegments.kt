package dev.reactant.enrichedui.image.segmentation.splitting

import dev.reactant.enrichedui.image.segmentation.SegmentedItemResource
import java.awt.image.BufferedImage
import java.io.File


interface ResourceSegments : Iterable<SegmentedItemResource> {
    val identifier: String
    val originalImage: BufferedImage
    val animationMetaFile: File?
}
