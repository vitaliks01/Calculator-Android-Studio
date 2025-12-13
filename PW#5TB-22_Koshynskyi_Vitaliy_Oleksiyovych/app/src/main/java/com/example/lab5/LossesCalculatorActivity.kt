package com.example.lab5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lab5.ui.theme.lab5Theme

class LossesCalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            lab5Theme {
                LossesCalculatorScreen(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LossesCalculatorScreen(onBackPressed: () -> Unit) {
    var omega by remember { mutableStateOf("") }
    var tb by remember { mutableStateOf("") }
    var Pm by remember { mutableStateOf("") }
    var Tm by remember { mutableStateOf("") }
    var kp by remember { mutableStateOf("") }
    var zPerA by remember { mutableStateOf("") }
    var zPerP by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    fun calculateLosses() {
        val omegaValue = omega.toDoubleOrNull() ?: 0.0    // ω – частота відмов
        val tbValue = tb.toDoubleOrNull() ?: 0.0          // t_b – середній час відновлення
        val PmValue = Pm.toDoubleOrNull() ?: 0.0          // Pm – середнє споживання потужності
        val TmValue = Tm.toDoubleOrNull() ?: 0.0          // Tm – тривалість періоду
        val kpValue = kp.toDoubleOrNull() ?: 0.0          // k_p – коефіцієнт планових відключень
        val zPerAValue = zPerA.toDoubleOrNull() ?: 0.0    // Zпер.а – вартість 1 кВт·год недовідпущеної енергії при аваріях
        val zPerPValue = zPerP.toDoubleOrNull() ?: 0.0    // Zпер.п – вартість 1 кВт·год при планових відключеннях

        // Розрахунок математичних сподівань
        val MWA = omegaValue * tbValue * PmValue * TmValue   // аварійне недовідпущення
        val MWP = kpValue * PmValue * TmValue                // планове недовідпущення
        val M = zPerAValue * MWA + zPerPValue * MWP          // збитки в грошах

        fun round(value: Double): String = "%.5f".format(value)

        result = """
            Математичне сподівання аварійного недовідпущення: ${round(MWA)} кВт·год
            Математичне сподівання планового недовідпущення: ${round(MWP)} кВт·год
            Математичне сподівання збитків від перервання електропостачання: ${round(M)} грн
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Збитки від перерв електропостачання",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Розрахунок збитків від перерв електропостачання " +
                        "при застосуванні однотрансформаторної ГТП.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Заповніть вихідні дані для розрахунку " +
                        "аварійного та планового недовідпущення електроенергії.",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )

            TextField(
                value = omega,
                onValueChange = { omega = it },
                label = { Text("Частота відмов ω (рік⁻¹)") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = tb,
                onValueChange = { tb = it },
                label = { Text("Середній час відновлення t_b (роки або частка року)") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = Pm,
                onValueChange = { Pm = it },
                label = { Text("Середнє споживання потужності Pm (кВт)") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = Tm,
                onValueChange = { Tm = it },
                label = { Text("Тривалість періоду Tm (год)") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = kp,
                onValueChange = { kp = it },
                label = { Text("Коефіцієнт планових відключень k_p") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = zPerA,
                onValueChange = { zPerA = it },
                label = { Text("Zпер.а (грн/кВт·год)") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = zPerP,
                onValueChange = { zPerP = it },
                label = { Text("Zпер.п (грн/кВт·год)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { calculateLosses() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Розрахувати збитки")
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
