package dev.reactant.enrichedui.image.generation.operation

import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.enrichedui.image.generation.GeneratedItemResourceSegments
import dev.reactant.enrichedui.image.segmentation.splitting.ItemResourceSegmentsLayout
import dev.reactant.enrichedui.image.segmentation.splitting.SegmentsLayout
import org.bukkit.Material
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.font.TextLayout
import java.awt.image.BufferedImage


class TextImageGeneration(
        var text: String,
        var font: Font = Font("SansSerif", Font.BOLD, 26),
        val width: Int,
        val height: Int
) : TextureGeneration {

    override val generatedTexture: BufferedImage
        get() = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).also {
            it.createGraphics().apply graphics@{
                //                color = Color.WHITE
//                fillRect(0, 0, width, height);
                color = Color.BLACK


                setRenderingHints(mapOf<RenderingHints.Key, Any>(
                        RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                        RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                ));

                TextLayout(text, font, fontRenderContext).run {
                    color = Color.BLACK
                    draw(this@graphics, 0f, fontMetrics.ascent.toFloat() + 28)
                }

                dispose()
            }
        }
}

fun ItemResourcesTable.generateText(
        identifier: String,
        text: String,
        rows: Int,
        cols: Int,
        font: Font = Font("SansSerif", Font.BOLD, 26),
        baseItem: Material = Material.GLASS_PANE,
        globalTranslation: Array<Double> = arrayOf(0.0, 0.0, -10.0),
        layout: SegmentsLayout = ItemResourceSegmentsLayout.VERTICAL,
        width: Int = rows * 64,
        height: Int = cols * 64
) = GeneratedItemResourceSegments("${this.identifierPrefix}-$identifier",
        TextImageGeneration(text, font, width, height), rows, cols,
        baseItem, globalTranslation, layout
)
