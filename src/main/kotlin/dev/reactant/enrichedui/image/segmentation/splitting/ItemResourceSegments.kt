package dev.reactant.enrichedui.image.segmentation.splitting

import dev.reactant.enrichedui.EnrichedUI
import dev.reactant.enrichedui.image.segmentation.SegmentedItemResource
import dev.reactant.resourcestirrer.model.ItemModel
import org.bukkit.Material
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil

abstract class ItemResourceSegments : GridResourceSegments {

    abstract val globalTranslation: Array<Double>
    abstract val baseItem: Material
    abstract val layout: SegmentsLayout

    val totalSegmentCols
        get() = when (cols) {
            in 1..9 -> ceil(cols.toDouble() / 3).toInt()
            else -> throw IllegalArgumentException("Segmenting cols need larger than 0 and smaller or equal to 9")
        }

    val totalSegmentRows
        get() = when (rows) {
            in 1..6 -> ceil(rows.toDouble() / 3).toInt()
            else -> throw IllegalArgumentException("Segmenting rows need larger than 0 and smaller or equal to 6")
        }

    var _segmentsResult: Map<Pair<Int, Int>, SegmentedItemResource>? = null

    override val segments: Map<Pair<Int, Int>, SegmentedItemResource>
        get() {
            if (_segmentsResult == null) _segmentsResult = breakIntoSegments()
            return _segmentsResult!!
        }

    private fun breakIntoSegments(): Map<Pair<Int, Int>, SegmentedItemResource> {
        val cacheFolder = File("${EnrichedUI.configFolder}/.cache");
        if (!cacheFolder.exists()) cacheFolder.mkdirs()

        layersImage.values.groupBy { "${it.width}x${it.height}" }.let {
            assert(it.size > 1) {
                "Segmenting texture layers must have same width and height, " +
                        "but $identifier is not"
            }
            assert(it.isNotEmpty()) { "Segmenting texture layers must have at least 1 layer, but $identifier is not" }
        }

        val imageWidth = layersImage.values.first().width
        val imageWidthPerSlot = imageWidth / cols
        val segmentWidth = imageWidthPerSlot * 3


        val imageHeight = layersImage.values.first().height
        val segmentHeight = segmentWidth

        // a texture width height scale should be equal
        val msgFormat = "        %15s: %s \n"
        fun imageInformation(): String = String.format(msgFormat, "identifier", identifier) +
                String.format(msgFormat, "image size", "${imageWidth} x ${imageHeight}") +
                String.format(msgFormat, "expected size", "${imageWidth} (fixed, base on width) x N(${imageWidthPerSlot * rows}) , while N is frame amount ") +
                String.format(msgFormat, "rows and cols", "$rows x $cols") +
                String.format(msgFormat, "needed segments", "rows: $totalSegmentRows, cols: $totalSegmentCols") +
                String.format(msgFormat, "segment size", "$segmentHeight x $segmentWidth")

        if (imageHeight % (imageWidthPerSlot * rows) != 0) {
            val msg = "Image size are incorrect! Image height are not divisible by the segment width: \n" + imageInformation()
            throw IllegalStateException(msg)
        }

        val frameAtImageYShift = imageWidthPerSlot * rows
        val animationFrames = imageHeight / frameAtImageYShift

        if (imageHeight % frameAtImageYShift != 0) {
            val msg = "Image size are incorrect! It look like an animated texture, but image height are not divisible by the frame height: \n" +
                    imageInformation() +
                    String.format(msgFormat, "frame height cut at", "$frameAtImageYShift px")
            throw IllegalStateException(msg)
        }


        // make a list of (0,0) (0,1) (0,2) (1,0) (1,1) (1,2), and map to image of segment
        return (0 until totalSegmentRows).flatMap { row -> (0 until totalSegmentCols).map { col -> row to col } }
                .map { (row, col) ->
                    val segmentImageX = segmentWidth * col
                    val segmentImageY = segmentHeight * row

                    val isLastRowSegment = row == totalSegmentRows - 1
                    val isLastColSegment = col == totalSegmentCols - 1

                    // The actual height of this segment (slot as unit)
                    val segmentSlotsHeight = if (isLastColSegment) (rows % 3).let { if (it == 0) 3 else it } else 3
                    // The actual width of this segment (slot as unit)
                    val segmentSlotsWidth = if (isLastRowSegment) (cols % 3).let { if (it == 0) 3 else it } else 3

                    val segmentImageWidth = segmentSlotsWidth * imageWidthPerSlot
                    val segmentImageHeight = segmentSlotsHeight * imageWidthPerSlot

                    val outputImageFiles = hashMapOf<String, File>()

                    val segmentIdentifier = "${identifier}-${row}-${col}";
                    layersImage.forEach { (layerKey, texture) ->
                        fun getFrameSubImage(animatedFrameIndex: Int): BufferedImage {
                            return texture.getSubimage(segmentImageX,
                                    segmentImageY + frameAtImageYShift * animatedFrameIndex,
                                    segmentImageWidth, segmentImageHeight)
                        }


                        val outputImage = BufferedImage(segmentWidth, segmentHeight * animationFrames, BufferedImage.TYPE_INT_ARGB)

                        outputImage.createGraphics().run {
                            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                            setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                            (0 until animationFrames).forEach { frameIndex ->
                                val takingImage = getFrameSubImage(frameIndex)
                                // although take a smaller image, but to force to match 3x3 size image
                                // so each frame start at segmentHeight * frameIndex, instead of segmentImageHeight
                                drawImage(takingImage, 0, segmentHeight * frameIndex, null)
                            }
                            dispose()
                        }

                        val outputImageFile = File("${cacheFolder.absolutePath}/$segmentIdentifier-$layerKey.png");
                        ImageIO.write(outputImage, "png", outputImageFile);
                        outputImageFiles[layerKey] = outputImageFile
                    }

                    layout.getSegmentsTranslation(this, SegmentInfo(col, row,
                            segmentSlotsWidth, segmentSlotsHeight)).let {
                        val segmentedItemResource = SegmentedItemResource(
                                segmentIdentifier, outputImageFiles, animationMetaFile, baseItem,
                                it.displayPosition.also {
                                    it.translation = it.translation!!
                                            .mapIndexed { index, value -> value + globalTranslation[index] }
                                            .toTypedArray()
                                })
                        (it.layoutPosition) to segmentedItemResource
                    }

                }.toMap()


    }


}

data class SegmentInfo(
        val x: Int,
        val y: Int,
        val segmentSlotWidth: Int,
        val segmentSlotHeight: Int
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
    fun getSegmentsTranslation(itemResourceSegments: ItemResourceSegments, segmentInfo: SegmentInfo): SegmentTranslation
}

