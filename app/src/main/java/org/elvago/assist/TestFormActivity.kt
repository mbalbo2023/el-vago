package org.elvago.assist

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import java.util.Calendar

/** Offline fictional form. No submission, persistence, or network access. */
class TestFormActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = ScrollView(this)
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }
        root.addView(form)
        fun title(value: String) = form.addView(TextView(this).apply {
            text = value
            textSize = 18f
            setPadding(0, 20, 0, 8)
        })
        title("Formulario ficticio: no se envían datos")
        title("Nombre completo")
        form.addView(EditText(this).apply {
            hint = "Nombre ficticio"
            contentDescription = "Nombre completo"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        })
        title("Fecha de nacimiento")
        val date = Button(this).apply {
            text = "Seleccioná una fecha"
            contentDescription = "Fecha de nacimiento: seleccionar fecha"
        }
        date.setOnClickListener {
            val today = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                date.text = "%02d/%02d/%04d".format(day, month + 1, year)
                date.contentDescription = "Fecha de nacimiento: ${date.text}"
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show()
        }
        form.addView(date)
        title("País")
        val countries = arrayOf("Seleccioná un país", "Argentina", "España", "Uruguay")
        form.addView(Spinner(this).apply {
            adapter = ArrayAdapter(this@TestFormActivity, android.R.layout.simple_spinner_dropdown_item, countries)
            contentDescription = "País"
        })
        title("Motivo de consulta")
        val reasons = arrayOf("Seleccioná un motivo", "Accesibilidad digital", "Tecnología", "Otro")
        form.addView(Spinner(this).apply {
            adapter = ArrayAdapter(this@TestFormActivity, android.R.layout.simple_spinner_dropdown_item, reasons)
            contentDescription = "Motivo de consulta"
        })
        form.addView(CheckBox(this).apply {
            text = "Acepto las condiciones ficticias de prueba"
        })
        form.addView(Button(this).apply {
            text = "Enviar formulario (bloqueado)"
            isEnabled = false
            contentDescription = "Enviar formulario deshabilitado: prueba sin envío"
        })
        setContentView(root)
    }
}
