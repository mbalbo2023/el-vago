package org.elvago.assist

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class FormReaderService : AccessibilityService() {
    companion object {
        @Volatile var latestSummary: String = "Sin lectura todavía."
            private set
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // MVP test scope: only inspect this app's fictional form, not other apps.
        if (event?.packageName?.toString() != packageName) return
        val root = rootInActiveWindow ?: return
        try {
            val counts = mutableMapOf<String, Int>()
            var nodes = 0
            val queue = ArrayDeque<AccessibilityNodeInfo>()
            queue.add(root)
            while (queue.isNotEmpty() && nodes < 300) {
                val node = queue.removeFirst()
                nodes++
                val kind = ControlClassifier.classify(
                    node.className?.toString() ?: "",
                    node.isEditable,
                    node.isCheckable,
                    node.isClickable
                )
                if (kind != null) counts[kind] = (counts[kind] ?: 0) + 1
                for (i in 0 until node.childCount) node.getChild(i)?.let(queue::add)
                if (node !== root) node.recycle()
            }
            latestSummary = "Controles detectados (tipos y cantidades):\n" +
                counts.entries.sortedBy { it.key }.joinToString("\n") { "${it.key}: ${it.value}" } +
                "\nNodos inspeccionados: $nodes"
        } finally { root.recycle() }
    }
    override fun onInterrupt() {}
}
