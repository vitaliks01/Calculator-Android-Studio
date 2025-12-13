package com.example.lab5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.lab5.ui.theme.lab5Theme

class ReliabilityCalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            lab5Theme {
                ReliabilityCalculatorScreen(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReliabilityCalculatorScreen(onBackPressed: () -> Unit) {
    var userInput by remember { mutableStateOf("") }
    var nValue by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // Відповідність номерів елементів елементам ЕПС
    val epsElements = mapOf(
        1 to "ПЛ-110 кВ",
        2 to "ПЛ-35 кВ",
        3 to "ПЛ-10 кВ",
        4 to "КЛ-10 кВ (траншея)",
        5 to "КЛ-10 кВ (кабельний канал)",
        6 to "Т-110 кВ",
        7 to "Т-35 кВ",
        8 to "Т-10 кВ (кабельна мережа 10 кВ)",
        9 to "Т-10 кВ (повітряна мережа 10 кВ)",
        10 to "В-110 кВ (елегазовий)",
        11 to "В-10 кВ (малооливний)",
        12 to "В-10 кВ (вакуумний)",
        13 to "АВ-0.38 кВ",
        14 to "ЕД 6, 10 кВ",
        15 to "ЕД 0,38 кВ"
    )

    // Довідкові дані для розрахунку (частина елементів використовується у формулах)
    val elementData = mapOf(
        "ПЛ-110 кВ" to mapOf("omega" to 0.07, "tv" to 10.0, "tp" to 35.0),
        "ПЛ-35 кВ" to mapOf("omega" to 0.02, "tv" to 8.0, "tp" to 35.0),
        "ПЛ-10 кВ" to mapOf("omega" to 0.02, "tv" to 10.0, "tp" to 35.0),
        "КЛ-10 кВ (траншея)" to mapOf("omega" to 0.03, "tv" to 44.0, "tp" to 9.0),
        "КЛ-10 кВ (кабельний канал)" to mapOf("omega" to 0.005, "tv" to 17.5, "tp" to 9.0),
        "Т-110 кВ" to mapOf("omega" to 0.015, "tv" to 100.0, "tp" to 43.0),
        "Т-35 кВ" to mapOf("omega" to 0.02, "tv" to 80.0, "tp" to 28.0),
        "Т-10 кВ (кабельна мережа 10 кВ)" to mapOf("omega" to 0.005, "tv" to 60.0, "tp" to 10.0),
        "Т-10 кВ (повітряна мережа 10 кВ)" to mapOf("omega" to 0.05, "tv" to 60.0, "tp" to 10.0)
    )

    fun calculateReliability() {
        val selectedIndexes = userInput
            .trim()
            .split(" ", ",", ";")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }

        if (selectedIndexes.isEmpty()) {
            result = "Помилка: введіть номери елементів (наприклад: 1 3 5)."
            return
        }

        val selectedElements = selectedIndexes.mapNotNull { epsElements[it] }

        if (selectedElements.isEmpty()) {
            result = "Помилка: за заданими номерами елементів не знайдено."
            return
        }

        val n = nValue.toDoubleOrNull()
        if (n == null) {
            result = "Помилка: введіть коректне числове значення N."
            return
        }

        var omegaSum = 0.0      // сумарна частота відмов одноколової системи
        var tRecovery = 0.0     // зважена тривалість відновлення
        var maxTp = 0.0         // максимальна тривалість планового простою

        for (element in selectedElements) {
            val data = elementData[element] ?: continue
            val omega = data["omega"] ?: 0.0
            val tv = data["tv"] ?: 0.0
            val tp = data["tp"] ?: 0.0

            omegaSum += omega
            tRecovery += omega * tv
            maxTp = maxOf(maxTp, tp)
        }

        // Урахування додаткових елементів (наприклад, відгалужень, вимикачів тощо)
        omegaSum += 0.03 * n
        tRecovery += 0.06 * n

        // Середня тривалість відновлення
        tRecovery /= omegaSum

        // Коефіцієнти простою
        val kAP = omegaSum * tRecovery / 8760.0         // аварійний простій
        val kPP = 1.2 * maxTp / 8760.0                  // плановий простій

        // Порівняння з двоколовою системою
        val omegaDK = 2 * omegaSum * (kAP + kPP)        // частота відмов двох кіл
        val omegaDKS = omegaDK + 0.02                   // з урахуванням секційного вимикача

        result = """
            Обрані елементи: ${selectedElements.joinToString(", ")}

            Частота відмов одноколової системи: ${"%.5f".format(omegaSum)} рік⁻¹
            Середня тривалість відновлення: ${"%.5f".format(tRecovery)} год
            Коефіцієнт аварійного простою k_AP: ${"%.5f".format(kAP)}
            Коефіцієнт планового простою k_PP: ${"%.5f".format(kPP)}

            Частота відмов одночасно двох кіл двоколової системи: ${"%.5f".format(omegaDK)} рік⁻¹
            Частота відмов двоколової системи (з урахуванням секційного вимикача): ${"%.5f".format(omegaDKS)} рік⁻¹
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Порівняння надійності",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Калькулятор порівняння надійності одноколової та двоколової систем електропередачі.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Введіть номери елементів (через пробіл):\n1 – ПЛ-110 кВ, 2 – ПЛ-35 кВ, 3 – ПЛ-10 кВ, 4 – КЛ-10 кВ (траншея), 5 – КЛ-10 кВ (канал), 6 – Т-110 кВ, 7 – Т-35 кВ, 8 – Т-10 кВ (кабель), 9 – Т-10 кВ (повітря).",
                style = MaterialTheme.typography.labelSmall
            )

            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Номери елементів (наприклад: 1 3 5)") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = nValue,
                onValueChange = { nValue = it },
                label = { Text("Введіть значення N") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { calculateReliability() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Розрахувати надійність")
            }

            Button(
                onClick = onBackPressed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад")
            }

            if (result.isNotBlank()) {
                Text(
                    text = result,
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
