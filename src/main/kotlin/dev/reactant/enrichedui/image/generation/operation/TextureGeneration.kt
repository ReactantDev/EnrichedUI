package dev.reactant.enrichedui.image.generation.operation

import java.awt.image.BufferedImage

interface TextureGeneration {
    val generatedTextureLayers: Map<String, BufferedImage>
}
