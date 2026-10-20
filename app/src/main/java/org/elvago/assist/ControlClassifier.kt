package org.elvago.assist

/** Pure, testable classification. No screen text or personal values are retained. */
object ControlClassifier {
    fun classify(className: String, editable: Boolean, checkable: Boolean, clickable: Boolean): String? {
        val name = className.substringAfterLast('.')
        return when {
            checkable -> "checkbox_or_toggle"
            name.contains("Spinner", ignoreCase = true) -> "dropdown"
            name.contains("DatePicker", ignoreCase = true) -> "date_picker"
            editable -> "editable_text"
            clickable -> "button_or_clickable"
            else -> null
        }
    }
}
