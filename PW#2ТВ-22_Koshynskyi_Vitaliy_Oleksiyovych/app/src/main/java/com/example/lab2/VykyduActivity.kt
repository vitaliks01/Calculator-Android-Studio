package com.example.lab2

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
import com.example.lab2.ui.theme.Lab2Theme

class VykyduActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab2Theme {
                VykyduScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VykyduScreen(onBackPressed: () -> Unit) {
    var coal by remember { mutableStateOf("") }
    var fuelOil by remember { mutableStateOf("") }
    var naturalGas by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    fun calculateEmissions() {
        val coalAmount = coal.toDoubleOrNull() ?: 0.0
        val fuelOilAmount = fuelOil.toDoubleOrNull() ?: 0.0
        val gasAmount = naturalGas.toDoubleOrNull() ?: 0.0

        // Константи для показників емісії та теплоти згоряння
        val emissionFactorCoal = 150.0
        val emissionFactorFuelOil = 0.57
        val emissionFactorGas = 0.0

        val heatValueCoal = 20.47
        val heatValueFuelOil = 40.40
        val heatValueGas = 33.08

        // Ефективність електростатичного фільтра
        val filterEfficiency = 0.985

        // Розрахунок викидів для вугілля
        val totalCoalEmissions = (emissionFactorCoal * heatValueCoal * coalAmount) / 1_000_000
        val coalEmissionsWithFilter = totalCoalEmissions * (1 - filterEfficiency)

        // Розрахунок викидів для мазуту
        val totalFuelOilEmissions = (emissionFactorFuelOil * heatValueFuelOil * fuelOilAmount) / 1_000_000
        val fuelOilEmissionsWithFilter = totalFuelOilEmissions * (1 - filterEfficiency)

        // Розрахунок викидів для природного газу
        val totalGasEmissions = (emissionFactorGas * gasAmount * heatValueGas) / 1_000_000 * (1 - filterEfficiency)

        // Сума викидів
        val totalEmissions = coalEmissionsWithFilter + fuelOilEmissionsWithFilter + totalGasEmissions

        result = """
            📊 РЕЗУЛЬТАТИ РОЗРАХУНКУ ВИКИДІВ
            
            🔹 ВУГІЛЛЯ:
            • Викиди після фільтра: ${"%.6f".format(coalEmissionsWithFilter)} т
            
            🔹 МАЗУТ:
            • Викиди після фільтра: ${"%.6f".format(fuelOilEmissionsWithFilter)} т
            
            🔹 ПРИРОДНИЙ ГАЗ:
            • Викиди після фільтра: ${"%.6f".format(totalGasEmissions)} т
            
            🔸 ЗАГАЛЬНІ ВИКИДИ:
            • Загальна кількість: ${"%.6f".format(totalEmissions)} т
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Калькулятор Викидів",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                text = "Введіть кількість палива:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            CompositionTextField(
                value = coal,
                onValueChange = { coal = it },
                label = "Вугілля (тонни)",
                modifier = Modifier.fillMaxWidth()
            )

            CompositionTextField(
                value = fuelOil,
                onValueChange = { fuelOil = it },
                label = "Мазут (тонни)",
                modifier = Modifier.fillMaxWidth()
            )

            CompositionTextField(
                value = naturalGas,
                onValueChange = { naturalGas = it },
                label = "Природний газ (м³)",
                modifier = Modifier.fillMaxWidth()
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
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(7.dp))
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

@Composable
fun CompositionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun VykyduPreview() {
    Lab2Theme {
        VykyduScreen(onBackPressed = {})
    }
}