import java.io.File

val path = System.getProperty("user.dir") + "/fontawesome/svgs"

val lines = arrayListOf(
        "package dev.reactant.enrichedui.universal",
        "import dev.reactant.resourcestirrer.table.ItemResourcesTable",
        "import dev.reactant.enrichedui.image.generation.operation.svgByClassloader",
        "import dev.reactant.resourcestirrer.table.ResourcesTable",
        "@ResourcesTable", "object FaIconsTable : ItemResourcesTable(\"dev.reactant.enriched.universal.faicon\") {")
File(path).listFiles().forEach { folder ->
    val type = folder.name
    folder.listFiles().map {
        val valName = it.nameWithoutExtension.replace("-", "_").toUpperCase()
        "    val FA_${valName}_${type.toUpperCase()} = svgByClassloader(\"icon/fontawesome/svgs/${folder.name}/${it.name}\",\"${it.name}-${type}\");"
    }.let { lines.addAll(it) }
}

lines.add("}")

File("../../kotlin/dev/reactant/enrichedui/universal/FontAwesomeIconsTable.kt")
        .writeText(lines.joinToString("\n"))
