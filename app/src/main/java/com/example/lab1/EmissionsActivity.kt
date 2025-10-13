package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab1.ui.theme.Lab1Theme

class EmissionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab1Theme {
                EmissionsScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmissionsScreen(onBackPressed: () -> Unit) {
    // input - горюча маса мазуту
    var carbon by remember { mutableStateOf("") }
    var hydrogen by remember { mutableStateOf("") }
    var oxygen by remember { mutableStateOf("") }
    var sulfur by remember { mutableStateOf("") }
    var qdaf by remember { mutableStateOf("") }
    var moisture by remember { mutableStateOf("") }
    var ash by remember { mutableStateOf("") }
    var vanadium by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    // Calculate - перерахунок на робочу масу
    fun calculateEmissions() {
        val C_G = carbon.toDoubleOrNull() ?: 0.0
        val H_G = hydrogen.toDoubleOrNull() ?: 0.0
        val O_G = oxygen.toDoubleOrNull() ?: 0.0
        val S_G = sulfur.toDoubleOrNull() ?: 0.0
        val Q_daf = qdaf.toDoubleOrNull() ?: 0.0
        val W_P = moisture.toDoubleOrNull() ?: 0.0
        val A_P = ash.toDoubleOrNull() ?: 0.0
        val V_G = vanadium.toDoubleOrNull() ?: 0.0

        // Коефіцієнт перерахунку з горючої маси на робочу
        val factor = (100 - W_P - A_P) / 100

        // Робоча маса
        val C_P = C_G * factor
        val H_P = H_G * factor
        val O_P = O_G * factor
        val S_P = S_G * factor
        val Q_P = Q_daf * factor

        // Ванадій у робочій масі
        val V_P = V_G * (100 - W_P) / 100

        // output
        result = """
            🔥 РОЗРАХУНОК СКЛАДУ МАЗУТУ
            
            📊 ГОРЮЧА МАСА (задана):
            • Вуглець: ${"%.2f".format(C_G)}%
            • Водень: ${"%.2f".format(H_G)}%
            • Кисень: ${"%.2f".format(O_G)}%
            • Сірка: ${"%.2f".format(S_G)}%
            • Теплота згоряння: ${"%.2f".format(Q_daf)} МДж/кг
            • Ванадій: ${"%.2f".format(V_G)} мг/кг
            
            📈 РОБОЧА МАСА (результат):
            • Вуглець: ${"%.2f".format(C_P)}%
            • Водень: ${"%.2f".format(H_P)}%
            • Кисень: ${"%.2f".format(O_P)}%
            • Сірка: ${"%.2f".format(S_P)}%
            • Теплота згоряння: ${"%.2f".format(Q_P)} МДж/кг
            • Ванадій: ${"%.2f".format(V_P)} мг/кг
            
            ⚙️ ПАРАМЕТРИ:
            • Вологість: ${"%.2f".format(W_P)}%
            • Зольність: ${"%.2f".format(A_P)}%
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Калькулятор Мазуту",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Введіть склад горючої маси мазуту:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = carbon,
                onValueChange = { carbon = it },
                label = { Text("Вуглець (Cg, %)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = hydrogen,
                onValueChange = { hydrogen = it },
                label = { Text("Водень (Hg, %)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = oxygen,
                onValueChange = { oxygen = it },
                label = { Text("Кисень (Og, %)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = sulfur,
                onValueChange = { sulfur = it },
                label = { Text("Сірка (Sg, %)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = qdaf,
                onValueChange = { qdaf = it },
                label = { Text("Теплота згоряння (Qdaf, МДж/кг)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Введіть параметри якості:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            OutlinedTextField(
                value = moisture,
                onValueChange = { moisture = it },
                label = { Text("Вологість (Wp, %)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = ash,
                onValueChange = { ash = it },
                label = { Text("Зольність (Ap, %)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = vanadium,
                onValueChange = { vanadium = it },
                label = { Text("Ванадій (Vg, мг/кг)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { calculateEmissions() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Розрахувати")
                }

                OutlinedButton(
                    onClick = onBackPressed,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Назад")
                }
            }

            // Результат
            if (result.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = result,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmissionsScreenPreview() {
    Lab1Theme {
        EmissionsScreen(onBackPressed = {})
    }
}