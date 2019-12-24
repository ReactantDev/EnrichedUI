package dev.reactant.uikit.image.combination.operation

import java.awt.Color
import java.awt.FlowLayout
import java.awt.Font
import java.awt.RenderingHints
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel


class TextImageGeneration(var text: String) : TextureGeneration {
    override fun writeTextureFiles(path: String) {
        val width = 64*3;
        val height = 64;
        val outputImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        outputImage.createGraphics().apply graphics@{

            color = Color.WHITE
            fillRect(0, 0, width, height);
            color = Color.BLACK


            setRenderingHints(mapOf<RenderingHints.Key, Any>(
                    RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                    RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            ));

            TextLayout(text, Font("Noto Sans Mono CJK HK", Font.BOLD, 26), fontRenderContext).run {
                color = Color.BLACK
                draw(this@graphics, 0f, fontMetrics.ascent.toFloat() + 28)
            }

            dispose()
        }

        val icon = ImageIcon(outputImage)
        val frame = JFrame()
        frame.layout = FlowLayout()
        frame.setSize(200, 300)
        val lbl = JLabel()
        lbl.icon = icon
        frame.add(lbl)
        frame.isVisible = true

        ImageIO.write(outputImage, "png", File(path))
    }
}
