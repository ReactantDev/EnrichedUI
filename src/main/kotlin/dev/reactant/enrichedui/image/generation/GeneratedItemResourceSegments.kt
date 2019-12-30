package dev.reactant.enrichedui.image.generation

import dev.reactant.enrichedui.image.generation.operation.TextureGeneration
import dev.reactant.enrichedui.image.segmentation.splitting.ItemResourceSegmentsLayout
import dev.reactant.enrichedui.image.segmentation.splitting.ItemResourceSegments
import dev.reactant.enrichedui.image.segmentation.splitting.SegmentsLayout
import org.bukkit.Material
import java.awt.image.BufferedImage
import java.io.File

open class GeneratedItemResourceSegments(override val identifier: String,
                                         val textureGeneration: TextureGeneration,
                                         override val rows: Int,
                                         override val cols: Int,
                                         override val baseItem: Material = Material.GLASS_PANE,
                                         override val globalTranslation: Array<Double> = arrayOf(0.0, 0.0, -10.0),
                                         override var layout: SegmentsLayout = ItemResourceSegmentsLayout.VERTICAL
) : ItemResourceSegments() {


    override val originalImage: BufferedImage
        get() = textureGeneration.generatedTexture
    override val animationMetaFile: File?
        get() = null

}

