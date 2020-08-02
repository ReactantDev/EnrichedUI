package dev.reactant.enrichedui.itemresources

import com.google.gson.Gson
import dev.reactant.resourcestirrer.model.ItemModel
import dev.reactant.resourcestirrer.resourcetype.item.ItemResource
import dev.reactant.resourcestirrer.utils.outputTo
import org.bukkit.Material
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileWriter
import java.io.InputStream
import javax.imageio.ImageIO

open class BufferedImageFactoryItemResource(
        override val identifier: String,
        val bufferedImageLayersFactory: Map<String, () -> BufferedImage>,
        override val baseItem: Material,
        val displayPosition: ItemModel.DisplayPosition = ItemModel.DisplayPosition(),
        val animationMetaInputStreamFactory: (() -> InputStream?)?=null) : ItemResource {
    override var allocatedCustomModelData: Int? = null;
    override val baseResource: ItemResource? = null
    override val predicate: Map<String, Any> = mapOf()
    override fun writeModelFile(path: String) {
        val modelObject = generateItemModel()
        FileWriter(File(path)).use { GSON.toJson(modelObject, ItemModel::class.java, it) }
    }

    override fun writeTextureFiles(path: String) {
        bufferedImageLayersFactory.forEach { (layerKey, bufferedImageFactory) ->
            val file = File("$path-$layerKey.png")
            if (!file.parentFile.exists()) file.parentFile.mkdirs()
            ImageIO.write(bufferedImageFactory(), "png", File("$path-$layerKey.png"))
        }
        animationMetaInputStreamFactory?.invoke()?.use {
            it.outputTo(File("$path-layer0.png.mcmeta"))
        }
    }

    companion object {
        private val GSON = Gson();
    }

    private fun generateItemModel(): ItemModel {
        return ItemModel().apply {
            parent = "item/generated"
            textures {
                bufferedImageLayersFactory.keys.forEach {
                    it("{{prefix}}-$it")
                }
            }
            display {
                gui = displayPosition
            }
        }
    }
}

