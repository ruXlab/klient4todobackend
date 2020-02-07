package vc.rux.klinent4todobackend.ui.components

import android.view.LayoutInflater
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


@BindingAdapter(value = ["items", "layoutId"], requireAll = true)
@JvmName("tagsChips")
fun setTagsChips(chipGroup: ChipGroup, items: List<String>, layoutId: Int) {
    val inflater = LayoutInflater.from(chipGroup.context)

    val numberOfViewsToRemove = chipGroup.childCount - items.size
    when {
        numberOfViewsToRemove < 0 -> repeat(-numberOfViewsToRemove) {
            inflater.inflate(layoutId, chipGroup, true)
        }
        numberOfViewsToRemove > 0 -> repeat(numberOfViewsToRemove) {
            chipGroup.removeViewAt(0)
        }
    }

    repeat(chipGroup.childCount) { n ->
        val node = chipGroup.getChildAt(n) as? Chip
            ?: throw RuntimeException("Could not find Chip in the provided layout. Make sure layout Chip at the root")
        node.text = items[n]
    }
}
