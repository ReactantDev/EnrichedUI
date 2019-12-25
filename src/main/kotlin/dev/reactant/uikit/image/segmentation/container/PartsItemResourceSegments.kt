package dev.reactant.uikit.image.segmentation.container

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.resourcestirrer.ResourceStirrer
import dev.reactant.resourcestirrer.model.ItemModel
import dev.reactant.resourcestirrer.resourceloader.ClassLoaderResourceLoader
import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.uikit.UIKit
import dev.reactant.uikit.image.segmentation.SegmentedItemResource
import org.bukkit.Material
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.min


/**
 * The image will be divided into equal parts using grid
 * @param identifier The identifier prefix of divided segments resource
 * @param searchAt The path of image in jar resources
 * @param rows Total number of rows to be divided
 * @param cols Total number of cols to be divided
 */
class PartsItemResourceSegments constructor(
        private val resourceLoader: ClassLoaderResourceLoader,
        private val searchAt: String,
        override val identifier: String,
        override val rows: Int,
        override val cols: Int,
        val baseItem: Material = Material.GLASS_PANE,
        val globalTranslation: Array<Double> = arrayOf(0.0, 0.0, -10.0),
        var layout: SegmentsLayout = DefaultSegmentsLayout.VERTICAL
) : GridResourceSegments {

    val totalSegmentCols = when (rows) {
        in 1..9 -> ceil(cols.toDouble() / 3).toInt()
        else -> throw IllegalArgumentException("Segmenting rows need larger than 0 and smaller or equal to 9")
    }

    val totalSegmentRows = when (cols) {
        in 1..6 -> ceil(rows.toDouble() / 3).toInt()
        else -> throw IllegalArgumentException("Segmenting cols need larger than 0 and smaller or equal to 6")
    }

//    val rowsScaleAllocation:List<Int>=

    override val originalImage: BufferedImage
        get() = (resourceLoader.getResourceFile("$searchAt.png")
                ?: throw IllegalStateException("Frame background image cannot be loaded: ${this.javaClass.canonicalName} : $searchAt.png"))
                .use { ImageIO.read(it) }

    override var segments: Map<Pair<Int, Int>, SegmentedItemResource> = breakIntoSegments()

    private fun breakIntoSegments(): Map<Pair<Int, Int>, SegmentedItemResource> {
        val cacheFolder = File("${UIKit.configFolder}/.cache");
        if (!cacheFolder.exists()) cacheFolder.mkdirs()

        val imageWidth = originalImage.width
        val imageHeight = originalImage.height
        val imageWidthPerSlot = imageWidth / rows
        val imageHeightPerSlot = imageHeight / cols
        val segmentWidth = imageWidthPerSlot * 3
        val segmentHeight = imageHeightPerSlot * 3


        // make a list of (0,0) (0,1) (0,2) (1,0) (1,1) (1,2), and map to image of segment
        return (0 until totalSegmentRows)
                .flatMap { row ->
                    (0 until totalSegmentCols).map { col ->
                        ResourceStirrer.logger.warn("$row $col")
                        val x = segmentWidth * row;
                        val y = segmentHeight * col;
                        val identifier = "ui-frame-${identifier}-${row}-${col}";
                        val segmentImageFile = File("${cacheFolder.absolutePath}/${identifier}.png");

                        val takingWidth = min(segmentWidth, imageWidth - x)
                        val takingHeight = min(segmentHeight, imageHeight - y)
                        ResourceStirrer.logger.warn("$imageWidth $imageHeight $takingWidth $takingHeight $x $y")
                        val takingImage = originalImage.getSubimage(x, y, takingWidth, takingHeight)
                        val outputImage = BufferedImage(segmentWidth, segmentHeight, BufferedImage.TYPE_INT_ARGB)
                        outputImage.createGraphics().run {
                            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                            setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                            drawImage(takingImage, 0, 0, null)
                            dispose()
                        }

                        ImageIO.write(outputImage, "png", segmentImageFile);

                        layout.getSegmentsTranslation(this, SegmentInfo(row, col)).let {
                            val segmentedItemResource = SegmentedItemResource(
                                    identifier, segmentImageFile, col, baseItem,
                                    it.displayPosition.also {
                                        ReactantCore.logger.warn(it.translation == null)
                                        ReactantCore.logger.warn(globalTranslation)
                                        it.translation = it.translation!!
                                                .mapIndexed { index, value -> value + globalTranslation[index] }
                                                .toTypedArray()
                                    })
                            (it.layoutPosition) to segmentedItemResource
                        }
                    }
                }.toMap().also {
                    it.values.size.let { ResourceStirrer.logger.warn(it) }
                }
    }

}

fun ItemResourcesTable.partsSegments(searchAt: String, identifier: String?,
                                     rows: Int, cols: Int,
                                     baseItem: Material = Material.GLASS_PANE,
                                     globalTranslation: Array<Double> = arrayOf(0.0, 0.0, -10.0)) =
        PartsItemResourceSegments(this.resourceLoader, searchAt, "${this.identifierPrefix}-$identifier", rows, cols, baseItem, globalTranslation)

data class SegmentInfo(
        val x: Int,
        val y: Int
)

data class SegmentTranslation(
        val layoutPosition: Pair<Int, Int>,
        val textureTranslation: List<Double>
) {
    val displayPosition
        get() = ItemModel.DisplayPosition().apply {
            rotation(0.0, 0.0, 0.0)
            translation = textureTranslation.toTypedArray()
            scale(3.39, 3.39, 1.0)
        }
}

interface SegmentsLayout {
    fun getSegmentsTranslation(partsItemResourceSegments: PartsItemResourceSegments, segmentInfo: SegmentInfo): SegmentTranslation
}

object DefaultSegmentsLayout {
    object VERTICAL : SegmentsLayout {
        override fun getSegmentsTranslation(partsItemResourceSegments: PartsItemResourceSegments, segmentInfo: SegmentInfo): SegmentTranslation {
            val isFirstSegmentAtRow = segmentInfo.x == 0
            val isLastSegmentAtRow = segmentInfo.x == partsItemResourceSegments.totalSegmentRows - 1
            val isFirstSegmentAtCol = segmentInfo.y == 0
            val isLastSegmentAtCol = segmentInfo.y == partsItemResourceSegments.totalSegmentCols - 1

            var offsetFromSegmentLeft = when {
                isFirstSegmentAtRow -> 0
                isLastSegmentAtRow -> ((partsItemResourceSegments.rows - 1) % 3)
                else -> -3
            }

            val yNeedShiftUp = partsItemResourceSegments.totalSegmentRows > 2 && partsItemResourceSegments.cols % 3 == 1
                    && isLastSegmentAtCol
            var offsetFromSegmentTop = when {
                isFirstSegmentAtRow || isLastSegmentAtRow -> 0
                else -> 1
            }.let { if (yNeedShiftUp) it - 1 else it }

            val atLayoutX = segmentInfo.x * 3 + offsetFromSegmentLeft
            val atLayoutY = segmentInfo.y * 3 + offsetFromSegmentTop
            ReactantCore.logger.info("$atLayoutX $atLayoutY")
            return SegmentTranslation(
                    atLayoutX to atLayoutY,
                    listOf(18.0 + -(offsetFromSegmentLeft * 18), -18.0 + (offsetFromSegmentTop * 18), 0.0)
            )
        }

    }
}
