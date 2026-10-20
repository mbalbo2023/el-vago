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
        // Strictly read-only. No screenshots, clicks, gestures, network, or persistence.
        if (event?.packageName == packageName) return
        val root = rootInActiveWindow ?: return
        try {
            val counts = mutableMapOf<String, Int>()
            var nodes = 0
            val queue = ArrayDeque<AccessibilityNodeInfo>()
            queue.add(root)
            while (queue.isNotEmpty() && nodes < 300) {
                val node = queue.removeFirst()
                nodes++
                val cls = node.className?.toString()?.substringAfterLast('.') ?: "Unknown"
                if (node.isEditable || node.isClickable || node.isCheckable) {
                    counts[cls] = (counts[cls] ?: 0) + 1
                }
                for (i in 0 until node.childCount) node.getChild(i)?.let(queue::add)
                if (node !== root) node.recycle()
            }
            latestSummary = "Controles detectados (solo tipos y cantidades):\n" +
                counts.entries.sortedBy { it.key }.joinToString("\n") { "${it.key}: ${it.value}" } +
                "\nNodos inspeccionados: $nodes"
        } finally { root.recycle() }
    }
    override fun onInterrupt() {}
}
