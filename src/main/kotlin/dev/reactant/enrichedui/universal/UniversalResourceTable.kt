package dev.reactant.enrichedui.universal

import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.resourcestirrer.table.ResourcesTable
import dev.reactant.enrichedui.element.progress.createColorableProgressBar
import dev.reactant.enrichedui.element.progress.createSingleLayerTextureProgressBar

@ResourcesTable
object UniversalResourceTable : ItemResourcesTable("dev.reactant.enriched.universal") {
    object PROGRESS_BAR : ItemResourcesGroup(tableHeader, "progress-bar") {
        val SIMPLE = createColorableProgressBar("simple",
                (1..19).map { it to "progress_bar/progress_bar_1/0/progress_bar_1_$it" }.toMap(),
                (14..35).map { it to "progress_bar/progress_bar_1/1/progress_bar_1_$it" }.toMap(),
                (30..43).map { it to "progress_bar/progress_bar_1/2/progress_bar_1_$it" }.toMap()
        )
    }
}
