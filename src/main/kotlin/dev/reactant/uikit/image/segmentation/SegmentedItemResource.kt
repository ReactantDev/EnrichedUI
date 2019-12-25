package dev.reactant.uikit.image.segmentation

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
open class SegmentedItemResource(override val identifier: String, val segmentImageFile: File, val segmentCol: Int, val zIndex: Double, override val baseItem: Material) : ItemResource {
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
    }

    companion object {
        private val GSON = Gson();
    }

    private fun generateItemModel(): ItemModel {
        return ItemModel().apply {
            textures {
                layer0 = "stirred:\${dir}/${segmentImageFile.nameWithoutExtension}"
            }
            display {
                gui {
                    rotation(0.0, 0.0, 0.0)
                    when (segmentCol) {
                        0 -> translation(18.0, 0.0, zIndex)
                        1 -> translation(72.0, 18.0, zIndex)
                        2 -> translation(-18.0, 18.0, zIndex)
                        else -> throw IllegalStateException();
                    }
                    scale(3.4, 3.4, 1.0)
                }
            }
        }
    }

}

