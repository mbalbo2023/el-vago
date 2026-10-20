package org.elvago.assist

import org.junit.Assert.assertEquals
import org.junit.Test

class ControlClassifierTest {
    @Test fun textField() = assertEquals("editable_text", ControlClassifier.classify("android.widget.EditText", true, false, true))
    @Test fun dropdown() = assertEquals("dropdown", ControlClassifier.classify("android.widget.Spinner", false, false, true))
    @Test fun datePicker() = assertEquals("date_picker", ControlClassifier.classify("android.widget.DatePicker", false, false, true))
    @Test fun checkbox() = assertEquals("checkbox_or_toggle", ControlClassifier.classify("android.widget.CheckBox", false, true, true))
    @Test fun button() = assertEquals("button_or_clickable", ControlClassifier.classify("android.widget.Button", false, false, true))
    @Test fun labelIgnored() = assertEquals(null, ControlClassifier.classify("android.widget.TextView", false, false, false))
}
