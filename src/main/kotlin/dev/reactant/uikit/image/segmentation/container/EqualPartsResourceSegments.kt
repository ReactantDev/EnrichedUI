package dev.reactant.uikit.image.segmentation.container

import dev.reactant.resourcestirrer.resourceloader.ClassLoaderResourceLoader
import dev.reactant.uikit.UIKit
import dev.reactant.uikit.image.segmentation.SegmentedItemResource
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


/**
 * The image will be divided into equal parts using grid
 * @param identifier The identifier prefix of divided segments resource
 * @param imagePath The path of image in jar resources
 * @param rows Total number of rows to be divided
 * @param cols Total number of cols to be divided
 */
abstract class EqualPartsResourceSegments protected constructor(
        override val identifier: String,
        private val imagePath: String,
        override val rows: Int,
        override val cols: Int
) : GridResourceSegments {
    private val resourceLoader = ClassLoaderResourceLoader(this.javaClass.classLoader)

    override val gridSegments: List<List<SegmentedItemResource>> = breakIntoSegments();

    override val originalImage: BufferedImage
        get() = (resourceLoader.getResourceFile("$imagePath.png")
                ?: throw IllegalStateException("Frame background image cannot be loaded: ${this.javaClass.canonicalName} : $imagePath.png"))
                .use { ImageIO.read(it) }

    override val size: Int
        get() = gridSegments.size

    override fun iterator(): Iterator<SegmentedItemResource> = gridSegments.flatten().iterator()

    private fun breakIntoSegments(): List<List<SegmentedItemResource>> {
        val cacheFolder = File("${UIKit.configFolder}/.cache");
        if (!cacheFolder.exists()) cacheFolder.mkdirs()

        val imageWidth = originalImage.width
        val imageHeight = originalImage.height
        check(imageWidth % cols == 0) { "Image width (${imageWidth}px) cannot be divided by ${cols}: ${this.javaClass.canonicalName} : $imagePath" }
        check(imageHeight % rows == 0) { "Image height (${imageHeight}px) cannot be divided by segment amount (${rows}): ${this.javaClass.canonicalName} : $imagePath" }
        val segmentWidth = imageWidth / cols
        val segmentHeight = imageHeight / rows


        // make a list of (0,0) (0,1) (0,2) (1,0) (1,1) (1,2), and map to image of segment
        return (0 until rows)
                .map { row ->
                    (0 until cols).map { col ->
                        val x = segmentWidth * col;
                        val y = segmentHeight * row;
                        val identifier = "ui-frame-${identifier}-${row}-${col}";
                        val segmentImageFile = File("${cacheFolder.absolutePath}/${identifier}.png");
                        ImageIO.write(originalImage.getSubimage(x, y, segmentWidth, segmentHeight), "png", segmentImageFile);

                        SegmentedItemResource(identifier, segmentImageFile, col);
                    }
                }
    }
}
