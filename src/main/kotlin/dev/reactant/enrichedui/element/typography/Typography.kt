package dev.reactant.enrichedui.element.typography

import dev.reactant.enrichedui.itemresources.BufferedImageFactoryItemResource
import dev.reactant.resourcestirrer.table.ItemResourcesTable
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.util.XMLResourceDescriptor
import org.bukkit.Material
import org.w3c.dom.svg.SVGDocument
import java.awt.image.BufferedImage
import java.util.*

fun <T, U, O> Iterable<T>.cartesianProduct(iterable: Iterable<U>, mapper: (T, U) -> O) = this.flatMap { t -> iterable.map { u -> mapper(t, u) } }

open class Typography(
        val identifier: String,
        val svgDocumentFactory: () -> SVGDocument,
        val supportedChars: Set<Char>,
        val charsAmount: Int,
        val baseItem: Material = Material.LEATHER_BOOTS
) : Iterable<BufferedImageFactoryItemResource> {

    protected fun generateCombinations(): List<String> {
        var result = supportedChars.map { it.toString() }
        repeat(charsAmount - 1) {
            result = result.cartesianProduct(supportedChars) { a, b -> "$a$b" }
        }
        return result;
    }

    val resources: Map<String, BufferedImageFactoryItemResource> = generateCombinations().map { str ->
        str to BufferedImageFactoryItemResource(
                "$identifier-$str",
                mapOf("layer0" to {
                    val svg = svgDocumentFactory().apply {
                        getElementById("enrichedui:chars").textContent = str
                    }

                    var result: BufferedImage? = null
                    object : ImageTranscoder() {
                        override fun createImage(width: Int, height: Int): BufferedImage {
                            return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        }

                        override fun writeImage(img: BufferedImage?, output: TranscoderOutput?) {
                            result = img!!
                        }
                    }.apply {
                        addTranscodingHint(ImageTranscoder.KEY_WIDTH, 64F)
                        addTranscodingHint(ImageTranscoder.KEY_HEIGHT, 64F)
                    }.transcode(TranscoderInput(svg), null)
                    result!!;
                }), baseItem, animationMetaInputStreamFactory = null
        ).apply { displayPosition.scale(1.13, 1.13, 0.0) }
    }.toMap()

    override fun iterator(): Iterator<BufferedImageFactoryItemResource> = resources.values.iterator()
}

/**
 * Multi layer texture progress bar
 */
fun ItemResourcesTable.createTypography(
        identifier: String,
        svgDocumentFactory: () -> SVGDocument,
        supportedChars: Set<Char>,
        charsAmount: Int,
        baseItem: Material = Material.LEATHER_BOOTS
) = Typography(identifier, svgDocumentFactory, supportedChars, charsAmount, baseItem)


fun ItemResourcesTable.svgDocumentFactoryByClassLoader(svgFilePath: String): () -> SVGDocument =
        {
            val parser = XMLResourceDescriptor.getXMLParserClassName()
            val svgFactory = SAXSVGDocumentFactory(parser)
            (svgFactory.createDocument(UUID.randomUUID().toString(), this.resourceLoader.getResourceFile(svgFilePath)) as SVGDocument)
        }
