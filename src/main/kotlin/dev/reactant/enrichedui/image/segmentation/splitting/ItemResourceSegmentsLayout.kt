package dev.reactant.enrichedui.image.segmentation.splitting

object ItemResourceSegmentsLayout {

    private fun segmentTranslationBySlotOffset(segmentInfo: SegmentInfo, offsetX: Int, offsetY: Int)
            : SegmentTranslation {
        val atLayoutX = segmentInfo.x * 3 + offsetX
        val atLayoutY = segmentInfo.y * 3 + offsetY
        return SegmentTranslation(
                atLayoutX to atLayoutY,
                listOf(18.0 + -(offsetX * 18), -18.0 + (offsetY * 18), 0.0)
        )
    }

    object VERTICAL : SegmentsLayout {
        override fun getSegmentsTranslation(itemResourceSegments: ItemResourceSegments, segmentInfo: SegmentInfo): SegmentTranslation {
            val isFirstSegmentAtRow = segmentInfo.x == 0
            val isLastSegmentAtRow = segmentInfo.x == itemResourceSegments.totalSegmentCols - 1
            val isFirstSegmentAtCol = segmentInfo.y == 0
            val isLastSegmentAtCol = segmentInfo.y == itemResourceSegments.totalSegmentRows - 1

            val offsetFromSegmentLeft = when {
                isFirstSegmentAtRow -> 0
                isLastSegmentAtRow -> segmentInfo.segmentSlotWidth - 1
                else -> -3
            }

            val yNeedShiftUp = itemResourceSegments.totalSegmentCols > 2 && itemResourceSegments.cols % 3 == 1
                    && isLastSegmentAtCol

            val offsetFromSegmentTop = when {
                isFirstSegmentAtRow || isLastSegmentAtRow -> 0
                else -> 1
            }.let { if (yNeedShiftUp) it - 1 else it }

            return segmentTranslationBySlotOffset(segmentInfo, offsetFromSegmentLeft, offsetFromSegmentTop)
        }

    }

    fun sameOffset(offsetFromLeft:Int,offsetFromTop: Int): SegmentsLayout = object : SegmentsLayout {
        override fun getSegmentsTranslation(itemResourceSegments: ItemResourceSegments, segmentInfo: SegmentInfo): SegmentTranslation {
            return segmentTranslationBySlotOffset(segmentInfo, offsetFromLeft, offsetFromTop)
        }
    }

}

