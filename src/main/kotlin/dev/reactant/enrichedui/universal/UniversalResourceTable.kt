package dev.reactant.enrichedui.universal

import dev.reactant.enrichedui.element.progress.createColorableProgressBar
import dev.reactant.enrichedui.image.segmentation.splitting.byClassLoaderSegments
import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.resourcestirrer.table.ResourcesTable

@ResourcesTable
object UniversalResourceTable : ItemResourcesTable("dev.reactant.enriched.universal") {
    val VERSION_BOARD = byClassLoaderSegments("version_info", "version-info", 9, 6)

    object PROGRESS_BAR : ItemResourcesGroup(this, "progress-bar") {
        val SIMPLE = createColorableProgressBar("simple",
                (1..19).map { it to "progress_bar/progress_bar_1/0/progress_bar_1_$it" }.toMap(),
                (14..35).map { it to "progress_bar/progress_bar_1/1/progress_bar_1_$it" }.toMap(),
                (30..43).map { it to "progress_bar/progress_bar_1/2/progress_bar_1_$it" }.toMap()
        )
    }
}
