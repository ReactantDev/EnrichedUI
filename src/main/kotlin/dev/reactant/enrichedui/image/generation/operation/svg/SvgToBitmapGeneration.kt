package dev.reactant.enrichedui.image.generation.operation

import dev.reactant.enrichedui.itemresources.BufferedImageFactoryItemResource
import dev.reactant.resourcestirrer.table.ItemResourcesTable
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.dom.util.DOMUtilities
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.util.XMLResourceDescriptor
import org.bukkit.Material
import org.w3c.dom.svg.SVGDocument
import java.awt.image.BufferedImage
import java.io.*
import java.util.*


class SvgGeneration(
        private val inputStreamFactory: () -> InputStream,
        private val modification: (SVGDocument) -> Unit,
        private val debugSvgOutputPath: String? = null
) {
    var parser = XMLResourceDescriptor.getXMLParserClassName()
    val svgFactory = SAXSVGDocumentFactory(parser)
    val generatedSVG: SVGDocument
        get() = (svgFactory.createDocument(UUID.randomUUID().toString(), inputStreamFactory().bufferedReader()) as SVGDocument).apply {
            modification(this)

            if (debugSvgOutputPath != null) {
                val fileOutputStream: OutputStream = FileOutputStream(debugSvgOutputPath)
                val svgWriter: Writer = OutputStreamWriter(fileOutputStream, "UTF-8")
                DOMUtilities.writeDocument(this, svgWriter);
                svgWriter.flush()
                svgWriter.close()
            }
        }


    fun export(width: Int, height: Int): BufferedImage {
        var result: BufferedImage? = null
        object : ImageTranscoder() {
            override fun createImage(width: Int, height: Int): BufferedImage {
                return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }

            override fun writeImage(img: BufferedImage?, output: TranscoderOutput?) {
                result = img!!
            }
        }.apply {
            addTranscodingHint(ImageTranscoder.KEY_WIDTH, width.toFloat())
            addTranscodingHint(ImageTranscoder.KEY_HEIGHT, height.toFloat())
        }.transcode(TranscoderInput(generatedSVG), null)
        return result!!;
    }
}

/**
 * To avoid memory leak, the image should only be generated when needed
 */
class DummyTextureGeneration(val imageFactory: () -> BufferedImage) : TextureGeneration {
    override val generatedTextureLayers: Map<String, BufferedImage>
        get() = mapOf("layer0" to imageFactory())
}


fun ItemResourcesTable.svgByClassloader(
        path: String,
        identifier: String,
        modification: (SVGDocument).() -> Unit = {}
) = SvgGeneration({ this.resourceLoader.getResourceFile(path)!! }, modification)
        .let { DummyTextureGeneration { it.export(64, 64) } }
        .let { BufferedImageFactoryItemResource(identifier, mapOf("layer0" to it.imageFactory), Material.LEATHER_BOOTS) }

