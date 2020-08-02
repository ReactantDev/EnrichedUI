package dev.reactant.enrichedui.universal

import dev.reactant.enrichedui.element.progress.createColorableProgressBar
import dev.reactant.enrichedui.element.typography.createTypography
import dev.reactant.enrichedui.element.typography.svgDocumentFactoryByClassLoader
import dev.reactant.enrichedui.image.generation.operation.svgByClassloader
import dev.reactant.enrichedui.image.segmentation.splitting.byClassLoaderSegments
import dev.reactant.resourcestirrer.table.ItemResourcesTable
import dev.reactant.resourcestirrer.table.ResourcesTable

@ResourcesTable
object UniversalResourceTable : ItemResourcesTable("dev.reactant.enriched.universal") {
    object PROGRESS_BAR : ItemResourcesGroup(this, "progress-bar") {
        val SIMPLE = createColorableProgressBar("simple",
                (1..19).map { it to "progress_bar/progress_bar_1/0/progress_bar_1_$it" }.toMap(),
                (14..35).map { it to "progress_bar/progress_bar_1/1/progress_bar_1_$it" }.toMap(),
                (30..43).map { it to "progress_bar/progress_bar_1/2/progress_bar_1_$it" }.toMap()
        )
    }

    private val asciiChars = (32..127).map { it.toChar() }.toSet()
    private val numbers = (48..57).map { it.toChar() }.toSet().union(setOf(' ', ',', '.', '$'))

    object TYPOGRAPHY : ItemResourcesGroup(this, "typography") {
        object NUMBER : ItemResourcesGroup(this, "number") {
            val NOTO_SANS_MONO_3CHAR = createTypography(
                    "noto-sans-mono-3char",
                    svgDocumentFactoryByClassLoader("typography/noto_sans_mono_3char.svg"),
                    numbers, 3)
        }
        object ASCII : ItemResourcesGroup(this, "ascii") {
            val NOTO_SANS_MONO_2CHAR = createTypography(
                    "noto-sans-mono-2char",
                    svgDocumentFactoryByClassLoader("typography/noto_sans_mono_2char.svg"),
                    asciiChars, 2)
        }
    }
}
