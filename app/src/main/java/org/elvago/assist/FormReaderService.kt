package org.elvago.assist

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * P0 experiment: accessible overlay, NO clicks/gestures on other apps.
 * The accessibility button is system/device-dependent and is not guaranteed to exist.
 */
class FormReaderService : AccessibilityService() {
    companion object {
        @Volatile var latestSummary: String = "Sin lectura todavía."
            private set
    }
    private var overlay: LinearLayout? = null
    private var cancelled = false
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        val controller = accessibilityButtonController
        controller.registerAccessibilityButtonCallback(
            object : AccessibilityButtonController.AccessibilityButtonCallback() {
                override fun onClicked(controller: AccessibilityButtonController) {
                    showOverlay()
                }
            },
            mainHandler
        )
    }

    private fun showOverlay() {
        if (overlay != null) return
        cancelled = false
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(0xFFF7F9FC.toInt())
        }
        panel.addView(TextView(this).apply {
            text = "El Vago: prueba de accesibilidad"
            textSize = 20f
            contentDescription = "El Vago, panel experimental"
        })
        panel.addView(Button(this).apply {
            text = "1. Estado del panel"
            contentDescription = "Estado del panel"
            setOnClickListener {
                text = "1. Panel operativo, sin control de Chrome"
            }
        })
        panel.addView(Button(this).apply {
            text = "2. Detener"
            contentDescription = "Detener El Vago"
            setOnClickListener { cancelled = true; closeOverlay() }
        })
        panel.addView(Button(this).apply {
            text = "3. Cerrar panel"
            contentDescription = "Cerrar panel El Vago"
            setOnClickListener { closeOverlay() }
        })
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP }
        try {
            (getSystemService(WINDOW_SERVICE) as WindowManager).addView(panel, params)
            overlay = panel
        } catch (_: RuntimeException) {
            latestSummary = "No se pudo mostrar el panel; comprobar compatibilidad."
        }
    }

    private fun closeOverlay() {
        val panel = overlay ?: return
        overlay = null
        (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(panel)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // P0: inspect ONLY the app's fictional form. No Chrome content is read.
        if (cancelled || event?.packageName?.toString() != packageName) return
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
            latestSummary = "Controles detectados (tipos y cantidades):\\n" +
                counts.entries.sortedBy { it.key }.joinToString("\\n") { "${it.key}: ${it.value}" } +
                "\\nNodos inspeccionados: $nodes"
        } finally { root.recycle() }
    }

    override fun onInterrupt() { cancelled = true; closeOverlay() }
    override fun onDestroy() { closeOverlay(); super.onDestroy() }
}
