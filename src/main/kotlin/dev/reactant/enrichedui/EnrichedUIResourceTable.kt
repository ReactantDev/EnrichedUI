package dev.reactant.enrichedui

import dev.reactant.enrichedui.image.segmentation.splitting.ItemResourceSegmentsLayout
import dev.reactant.enrichedui.image.segmentation.splitting.byClassLoaderSegments
import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.resourcestirrer.table.ResourcesTable

@ResourcesTable
object EnrichedUIResourceTable : ItemResourcesTable("dev.reactant.enriched.internal") {
    val VERSION_BOARD = byClassLoaderSegments("version_info", "version-info", 9, 6)
    val TYPOGRAPHY_TEST_FRAME = byClassLoaderSegments(
            "frame/typography_test_frame",
            "typorgraphy-test-frame", 9, 6)
}
