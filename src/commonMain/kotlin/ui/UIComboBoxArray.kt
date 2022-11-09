package ui

import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korio.async.*


inline fun <T> Container.uiComboBoxArray1(
    boxWidth: Double = UI_DEFAULT_WIDTH,
    boxHeight: Double = UI_DEFAULT_HEIGHT,
    boxPadding: Double = UI_DEFAULT_PADDING,
    deactivationSymbol: T,
    items: List<T>,
    numberOfComboBoxes: Int,
    block: @ViewDslMarker UIComboBoxArray1<T>.() -> Unit = {}
) = UIComboBoxArray1(
    boxWidth, boxHeight, boxPadding,
    deactivationSymbol, items, numberOfComboBoxes
).addTo(this).apply(block)

class UIComboBoxArray1<T>(
    boxWidth: Double = UI_DEFAULT_WIDTH,
    boxHeight: Double = UI_DEFAULT_HEIGHT,
    poxPadding: Double = UI_DEFAULT_PADDING,
    private val deactivationSymbol: T,
    items: List<T>,
    val numberOfComboBoxes: Int
): UIView(boxWidth, boxHeight) {
    private val _comboBoxes = Array(numberOfComboBoxes) {
        uiComboBox(
            boxWidth, boxHeight,
            items = if (it == 0) items else listOf(deactivationSymbol) + items
        ) {
            y = it * (boxHeight + poxPadding)
            deactivate()
        }
    }

    init {
        _comboBoxes.take(2).forEach { it.activate() }

        onSelectionUpdate {
            if (_comboBoxes[it].selectedItem == deactivationSymbol) {
                for (i in (it + 1) until numberOfComboBoxes) {
                    deactivate(i)
                }
            } else if (it < numberOfComboBoxes - 1) activate(it + 1)
        }
    }

    val selectedItems get() = _comboBoxes.map {
        if (it.selectedItem == deactivationSymbol) null
        else it.selectedItem
    }

    fun onSelectionUpdate(callback: UIComboBoxArray1<T>.(Int) -> Unit) {
        for ((idx, comboBox) in _comboBoxes.withIndex()) {
            comboBox.onSelectionUpdate{ callback(idx) }
        }
    }

    fun activate(idx: Int) {
        _comboBoxes[idx].activate()
    }

    fun deactivate(idx: Int) {
        _comboBoxes[idx].selectedIndex = 0
        _comboBoxes[idx].deactivate()
    }
}


inline fun <T> Container.uiComboBoxArray2(
    boxWidth: Double = UI_DEFAULT_WIDTH,
    boxHeight: Double = UI_DEFAULT_HEIGHT,
    boxPadding: Double = UI_DEFAULT_PADDING,
    items: List<T>,
    numberOfComboBoxes: Int = items.size,
    block: @ViewDslMarker UIComboBoxArray2<T>.() -> Unit = {}
) = UIComboBoxArray2(
    boxWidth, boxHeight, boxPadding, items, numberOfComboBoxes
).addTo(this).apply(block)

class UIComboBoxArray2<T>(
    val boxWidth: Double = UI_DEFAULT_WIDTH,
    val boxHeight: Double = UI_DEFAULT_HEIGHT,
    val boxPadding: Double = UI_DEFAULT_PADDING,
    val items: List<T>,
    val numberOfComboBoxes: Int = items.size
 ): UIView(boxWidth, boxHeight) {
    init { require(numberOfComboBoxes <= items.size) }

    val onSelectionUpdate = Signal<Int>()

    private val _comboBoxes = Array(numberOfComboBoxes) {
        comboBoxOf(it, listOf(items[it]))
    }
    init {
        for (i in 0 until numberOfComboBoxes)
            updateComboBox(i)
    }

    val selectedItems get() = _comboBoxes.map { it.selectedItem }

    fun activateComboBox(idx: Int) {
        if (_comboBoxes[idx].selectedItem == null) {
            _comboBoxes[idx].removeFromParent()
            _comboBoxes[idx] = comboBoxOf(idx, unselectedItems(idx))
            updateAllOthers(idx)
        }
    }

    fun deactivateComboBox(idx: Int) {
        if (_comboBoxes[idx].selectedItem != null) {
            _comboBoxes[idx].removeFromParent()
            _comboBoxes[idx] = comboBoxOf(idx, emptyList())
            updateAllOthers(idx)
        }
    }

    private fun comboBoxOf(idx: Int, items: List<T>, selectedIndex: Int = 0) =
        uiComboBox(boxWidth, boxHeight, items = items, selectedIndex = selectedIndex) {
            y = idx * (boxHeight + boxPadding)
            if (items.isEmpty()) deactivate()
            onSelectionUpdate {
                updateAllOthers(idx)
                this@UIComboBoxArray2.onSelectionUpdate(idx)
            }
        }

    private fun updateAllOthers(idxNotToUpdate: Int) {
        for (i in 0 until numberOfComboBoxes) {
            if (i != idxNotToUpdate) updateComboBox(i)
        }
    }

    private fun unselectedItems(idx: Int): List<T> {
        val items = items.toMutableList()
        for ((i, box) in _comboBoxes.withIndex()) {
            if (i != idx) {
                items.remove(box.selectedItem)
            }
        }
        return items
    }

    private fun updateComboBox(idx: Int) {
        val selectedItem = _comboBoxes[idx].selectedItem
        if (selectedItem != null) {
            val freeItems = unselectedItems(idx)
            val selectIndex = freeItems.indexOf(selectedItem)

            _comboBoxes[idx].removeFromParent()
            _comboBoxes[idx] = comboBoxOf(idx, freeItems, selectIndex)
        }
    }
}
