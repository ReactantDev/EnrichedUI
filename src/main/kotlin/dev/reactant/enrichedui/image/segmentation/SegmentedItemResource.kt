package dev.reactant.enrichedui.image.segmentation

import com.google.gson.Gson
import dev.reactant.resourcestirrer.itemresource.ItemResource
import dev.reactant.resourcestirrer.model.ItemModel
import dev.reactant.resourcestirrer.utils.outputTo
import org.bukkit.Material
import java.io.File
import java.io.FileWriter

/**
 * A item resource which is
 */
open class SegmentedItemResource(override val identifier: String, val segmentImageFile: File,
                                 val animationMetaFile: File?,
                                 override val baseItem: Material,
                                 val displayPosition: ItemModel.DisplayPosition) : ItemResource {
    override var allocatedCustomModelData: Int? = null;
    override val baseResource: ItemResource? = null
    override val predicate: Map<String, Any> = mapOf()
    override fun writeModelFile(path: String) {
        val modelObject = generateItemModel()
        FileWriter(File(path)).use { GSON.toJson(modelObject, ItemModel::class.java, it) }
    }

    override fun writeTextureFiles(path: String) {
        segmentImageFile.inputStream().use {
            it.outputTo(File("$path/${segmentImageFile.name}"))
        }
        animationMetaFile?.inputStream()?.use {
            it.outputTo(File("$path/${segmentImageFile.name}.mcmeta"))
        }
    }

    companion object {
        private val GSON = Gson();
    }

    private fun generateItemModel(): ItemModel {
        return ItemModel().apply {
            textures {
                "layer0"("stirred:{{prefix}}/${segmentImageFile.nameWithoutExtension}")
            }
            display {
                gui = displayPosition
            }
        }
    }

}
