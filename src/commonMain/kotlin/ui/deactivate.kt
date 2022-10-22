package ui

import com.soywiz.korge.ui.*

fun <T>UIComboBox<T>.activate() { children.forEach { it.mouseEnabled = true } }
fun <T>UIComboBox<T>.deactivate() { children.forEach { it.mouseEnabled = false } }
