package org.elvago.assist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var output: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 24)
        }
        layout.addView(TextView(this).apply {
            text = "El Vago 0.1\nDiagnóstico local, solo lectura.\nNo se ejecutan acciones sobre formularios."
            textSize = 20f
        })
        layout.addView(Button(this).apply {
            text = "Abrir formulario ficticio"
            setOnClickListener { startActivity(Intent(this@MainActivity, TestFormActivity::class.java)) }
        })
        layout.addView(Button(this).apply {
            text = "Abrir ajustes de accesibilidad"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        })
        layout.addView(Button(this).apply {
            text = "Mostrar último diagnóstico"
            setOnClickListener { output.text = FormReaderService.latestSummary }
        })
        output = TextView(this).apply { text = "Sin diagnóstico. Activá el servicio voluntariamente y abrí el formulario ficticio."; textSize = 16f }
        layout.addView(output)
        setContentView(layout)
    }
}
