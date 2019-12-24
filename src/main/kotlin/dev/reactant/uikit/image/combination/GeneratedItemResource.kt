package dev.reactant.uikit.image.combination

import com.google.gson.Gson
import dev.reactant.resourcestirrer.itemresource.ItemResource
import dev.reactant.resourcestirrer.model.ItemModel
import dev.reactant.uikit.image.combination.operation.TextureGeneration
import org.bukkit.Material
import java.io.File
import java.io.FileWriter

open class GeneratedItemResource(override val identifier: String,
                                 override val baseItem: Material,
                                 val itemModel: ItemModel?,
                                 val textureGeneration: TextureGeneration) : ItemResource {

    constructor(identifier: String, baseItem: Material, imageCombination: TextureGeneration)
            : this(identifier, baseItem, null, imageCombination)

    override var allocatedCustomModelData: Int? = null
    override val baseResource: ItemResource? = null

    override val predicate: Map<String, Any> = mapOf()

    override fun writeModelFile(path: String) {
        val modelObject = itemModel ?: ItemModel().apply {
            textures {
                layer0 = "stirred:\${dir}"
            }
            display {
                gui {
                    rotation(0.0, 0.0, 0.0);
                    translation(0.0, 0.0, 0.0)
                    scale(3.4, 1.0, 1.0);
                }
            }
        }
        FileWriter(File(path)).use { GSON.toJson(modelObject, ItemModel::class.java, it) }
    }

    companion object {
        private val GSON = Gson();
    }

    override fun writeTextureFiles(path: String) = textureGeneration.writeTextureFiles("$path.png")

}
