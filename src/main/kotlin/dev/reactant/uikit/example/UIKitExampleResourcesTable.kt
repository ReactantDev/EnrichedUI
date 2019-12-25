package dev.reactant.uikit.example

import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.resourcestirrer.table.ResourcesTable
import dev.reactant.uikit.image.combination.GeneratedItemResource
import dev.reactant.uikit.image.combination.operation.TextImageGeneration
import dev.reactant.uikit.image.segmentation.container.partsSegments
import org.bukkit.Material

@ResourcesTable
object UIKitExampleResourcesTable : ItemResourcesTable("dev.reactant.uikit.example") {
    val TEST_FRAME = partsSegments("gui/test_frame/test_frame", "test_frame", 9, 6)

    object STR_1 : GeneratedItemResource("str1", Material.GLASS_PANE, TextImageGeneration("Testing!")) {}
}
