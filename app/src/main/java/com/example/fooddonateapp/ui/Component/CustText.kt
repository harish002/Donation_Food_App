package com.example.foodapp.ui.Component

import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun custTextInput(
    label: String,
    boardtype: KeyboardType = KeyboardType.Text,
    maxlenght: Int = 110,
    value: String,
    modifier: Modifier? = Modifier.fillMaxWidth(1f),
    onValueChange: (String) -> Unit,
    leadingicon: Int? = null,
    readonly: Boolean? =false
){
    Column(modifier = Modifier) {
        var textState by remember { mutableStateOf("") }

        val maxLength = maxlenght
        val white = Color.White
        val textcolor = Color(0xFF838383)
        val lableF = Color.Black

        if (modifier != null) {
            if (readonly != null) {
                Row(){
                    OutlinedTextField(
                        modifier = modifier,
                        value = value,
                        keyboardOptions = KeyboardOptions(keyboardType = boardtype),
                        onValueChange = onValueChange,
                        shape = RoundedCornerShape(20),
                        singleLine = true,
                        label = {
                            Text(
                                label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },

                        readOnly = readonly
                    )
                }
            }
        }

    }
}



