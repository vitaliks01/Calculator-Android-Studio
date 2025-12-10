package com.example.lab4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab4.ui.theme.lab4Theme

class ThreePhaseCalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Використовуємо вашу кастомну тему
            lab4Theme {
                ThreePhaseCalculatorScreen(onBackPressed = { finish() })
            }
        }
    }
}

@Composable
fun ThreePhaseCalculatorScreen(onBackPressed: () -> Unit) {
    var voltage by remember { mutableStateOf("") } // Напруга
    var impedance by remember { mutableStateOf("") } // Імпеданс
    var result by remember { mutableStateOf("") } // Результат

    // Функція для розрахунку трифазного короткого замикання
    fun calculateThreePhaseKZ() {
        val v = voltage.toDoubleOrNull()
        val z = impedance.toDoubleOrNull()

        result = if (v != null && z != null && z != 0.0) {
            "Струм трифазного КЗ: %.2f A".format(v / (z * kotlin.math.sqrt(3.0)))
        } else {
            "Помилка: перевірте введення даних."
        }
    }

    // Використовуємо Scaffold з фоном, який відповідає темі
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // Встановлюємо фон з теми
    ) { innerPadding ->
        // Створення UI для калькулятора трифазного КЗ
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = voltage,
                onValueChange = { voltage = it },
                label = { Text("Напруга (В)") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = impedance,
                onValueChange = { impedance = it },
                label = { Text("Імпеданс (Ом)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { calculateThreePhaseKZ() }, modifier = Modifier.fillMaxWidth()) {
                Text("Розрахувати")
            }
            Button(onClick = onBackPressed, modifier = Modifier.fillMaxWidth()) {
                Text("Повернутись")
            }
            Text(text = result, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
