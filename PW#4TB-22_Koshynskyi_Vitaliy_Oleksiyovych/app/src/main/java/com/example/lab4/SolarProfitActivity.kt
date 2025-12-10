package com.example.lab4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab4.ui.theme.lab4Theme
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt


class SolarProfitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            lab4Theme { SolarProfitScreen(onBack = { finish() }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarProfitScreen(onBack: () -> Unit) {
    // Вхідні дані
    var avgDailyPowerMw by remember { mutableStateOf("") }     // Середньодобова потужність (МВт)
    var firstDeviationMw by remember { mutableStateOf("") }    // Перше відхилення (МВт)
    var secondDeviationMw by remember { mutableStateOf("") }   // Друге відхилення (МВт)
    var pricePerKWhUah by remember { mutableStateOf("") }      // Вартість електроенергії (грн/кВт·год)

    // Вивід
    var resultText by remember { mutableStateOf("") }

    // ---- Математика
    fun normalPdf(x: Double, mean: Double, sigma: Double): Double {
        val coef = 1.0 / (sigma * sqrt(2 * Math.PI))
        val expo = -((x - mean).pow(2)) / (2 * sigma.pow(2))
        return coef * exp(expo)
    }

    fun integrateTrapezoid(a: Double, b: Double, n: Int, mean: Double, sigma: Double): Double {
        var sum = 0.0
        val h = (b - a) / n
        for (i in 0 until n) {
            val left = a + i * h
            val right = a + (i + 1) * h
            sum += (normalPdf(left, mean, sigma) + normalPdf(right, mean, sigma)) * 0.5 * h
        }
        return sum
    }

    fun compute() {
        val pAvgMw = avgDailyPowerMw.toDoubleOrNull() ?: 0.0
        val sigmaBefore = firstDeviationMw.toDoubleOrNull() ?: 0.0
        val sigmaAfter  = secondDeviationMw.toDoubleOrNull() ?: 0.0
        val priceUah    = pricePerKWhUah.toDoubleOrNull() ?: 0.0

        val a = pAvgMw - sigmaAfter
        val b = pAvgMw + sigmaAfter
        val steps = 1000

        // Частка потужності "без відхилень" (ефективність) до/після
        val efficiencyBefore = integrateTrapezoid(a, b, steps, pAvgMw, sigmaBefore)
        val efficiencyAfter  = integrateTrapezoid(a, b, steps, pAvgMw, sigmaAfter)

        // Енергія та гроші (кВт·год = МВт * 1000 * 24)
        val earningsBefore = pAvgMw * 24 * efficiencyBefore * priceUah * 1000
        val penaltiesBefore = pAvgMw * 24 * (1 - efficiencyBefore) * priceUah * 1000

        val earningsAfter = pAvgMw * 24 * efficiencyAfter * priceUah * 1000
        val penaltiesAfter = pAvgMw * 24 * (1 - efficiencyAfter) * priceUah * 1000

        resultText = """
            📊 РЕЗУЛЬТАТ

            Середньодобова потужність: ${"%.3f".format(pAvgMw)} МВт
            Перше відхилення: ${"%.3f".format(sigmaBefore)} МВт
            Друге відхилення: ${"%.3f".format(sigmaAfter)} МВт
            Вартість електроенергії: ${"%.3f".format(priceUah)} грн/кВт·год

            До вдосконалення:
            • Прибуток: ${"%.2f".format(earningsBefore / 1000)} тис. грн/добу
            • Виручка: ${"%.2f".format((earningsBefore - penaltiesBefore) / 1000)} тис. грн/добу
            • Штраф/втрати: ${"%.2f".format(penaltiesBefore / 1000)} тис. грн/добу

            Після вдосконалення:
            • Прибуток: ${"%.2f".format(earningsAfter / 1000)} тис. грн/добу
            • Виручка: ${"%.2f".format((earningsAfter - penaltiesAfter) / 1000)} тис. грн/добу
            • Штраф/втрати: ${"%.2f".format(penaltiesAfter / 1000)} тис. грн/добу
        """.trimIndent()
    }

    // ---- UI ----
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Розрахунок прибутку СЕС", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Введіть параметри:",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = avgDailyPowerMw,
                onValueChange = { avgDailyPowerMw = it },
                label = { Text("Середньодобова потужність (МВт)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = firstDeviationMw,
                onValueChange = { firstDeviationMw = it },
                label = { Text("Перше відхилення (МВт)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = secondDeviationMw,
                onValueChange = { secondDeviationMw = it },
                label = { Text("Друге відхилення (МВт)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = pricePerKWhUah,
                onValueChange = { pricePerKWhUah = it },
                label = { Text("Вартість електроенергії (грн/кВт·год)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Велика кнопка — займає весь вільний простір
                Button(
                    onClick = { compute() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Calculate, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Розрахувати",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Менша кнопка Назад
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.widthIn(min = 90.dp) // ← мінімальна ширина
                ) {
                    Text("Назад")
                }
            }


            if (resultText.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = resultText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SolarProfitPreview() {
    lab4Theme { SolarProfitScreen(onBack = {}) }
}
