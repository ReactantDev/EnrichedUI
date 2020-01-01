package dev.reactant.enrichedui.image.segmentation.splitting

import dev.reactant.enrichedui.EnrichedUI
import dev.reactant.resourcestirrer.resourceloader.ClassLoaderResourceLoader
import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.resourcestirrer.utils.outputTo
import org.bukkit.Material
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


/**
 * The image will be divided into equal parts using grid
 * @param identifier The identifier prefix of divided segments resource
 * @param textureLayers The path of image in jar resources
 * @param rows Total number of rows to be divided
 * @param cols Total number of cols to be divided
 */
class ClassLoaderItemResourceSegments constructor(
        private val resourceLoader: ClassLoaderResourceLoader,
        private val textureLayers: Map<String, String>,
        override val identifier: String,
        override val rows: Int,
        override val cols: Int,
        override val baseItem: Material = Material.GLASS_PANE,
        override val globalTranslation: Array<Double> = arrayOf(0.0, 0.0, -10.0),
        override var layout: SegmentsLayout = ItemResourceSegmentsLayout.VERTICAL
) : ItemResourceSegments() {

    override val layersImage: Map<String, BufferedImage>
        get() = textureLayers.mapValues { (_, texturePath) ->
            (resourceLoader.getResourceFile("$texturePath.png")
                    ?: throw IllegalStateException("Frame background texture cannot be loaded: ${this.javaClass.canonicalName} : $texturePath.png"))
                    .use { ImageIO.read(it) }
        }

    override val animationMetaFile: File?
        get() = resourceLoader.getResourceFile("$textureLayers.png.mcmeta")
                ?.use { input -> File("${EnrichedUI.configFolder}/.cache/${identifier}.png.mcmeta").also { input.outputTo(it) } }

}

fun ItemResourcesTable.byClassLoaderSegments(
        searchAt: String, identifier: String,
        cols: Int, rows: Int,
        baseItem: Material = Material.GLASS_PANE,
        globalTranslation: Array<Double> = arrayOf(0.0, 0.0, -10.0),
        layout: SegmentsLayout = ItemResourceSegmentsLayout.VERTICAL
) = ClassLoaderItemResourceSegments(this.resourceLoader, mapOf("layer0" to searchAt), getIdentifier(identifier), rows, cols, baseItem, globalTranslation, layout)

fun ItemResourcesTable.byClassLoaderSegments(
        textureLayers: Map<String, String>, identifier: String,
        cols: Int, rows: Int,
        baseItem: Material = Material.GLASS_PANE,
        globalTranslation: Array<Double> = arrayOf(0.0, 0.0, -10.0),
        layout: SegmentsLayout = ItemResourceSegmentsLayout.VERTICAL
) = ClassLoaderItemResourceSegments(this.resourceLoader, textureLayers, getIdentifier(identifier), rows, cols, baseItem, globalTranslation, layout)
