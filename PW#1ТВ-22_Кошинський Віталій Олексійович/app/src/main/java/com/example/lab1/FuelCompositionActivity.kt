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

class FuelCompositionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab1Theme {
                FuelCompositionScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCompositionScreen(onBackPressed: () -> Unit) {
    var carbonContent by remember { mutableStateOf("") }
    var hydrogenContent by remember { mutableStateOf("") }
    var oxygenContent by remember { mutableStateOf("") }
    var sulfurContent by remember { mutableStateOf("") }
    var nitrogenContent by remember { mutableStateOf("") }
    var moistureContent by remember { mutableStateOf("") }
    var ashContent by remember { mutableStateOf("") }
    var analysisResult by remember { mutableStateOf("") }

    fun performFuelAnalysis() {
        val hydrogen = hydrogenContent.toDoubleOrNull() ?: 0.0
        val carbon = carbonContent.toDoubleOrNull() ?: 0.0
        val sulfur = sulfurContent.toDoubleOrNull() ?: 0.0
        val nitrogen = nitrogenContent.toDoubleOrNull() ?: 0.0
        val oxygen = oxygenContent.toDoubleOrNull() ?: 0.0
        val moisture = moistureContent.toDoubleOrNull() ?: 0.0
        val ash = ashContent.toDoubleOrNull() ?: 0.0

        // Коефіцієнти перерахунку
        val dryBasisFactor = 100 / (100 - moisture)
        val combustibleBasisFactor = 100 / (100 - moisture - ash)

        // Склад сухої маси
        val hydrogenDry = hydrogen * dryBasisFactor
        val carbonDry = carbon * dryBasisFactor
        val sulfurDry = sulfur * dryBasisFactor
        val nitrogenDry = nitrogen * dryBasisFactor
        val oxygenDry = oxygen * dryBasisFactor

        // Склад горючої маси
        val hydrogenCombustible = hydrogen * combustibleBasisFactor
        val carbonCombustible = carbon * combustibleBasisFactor
        val sulfurCombustible = sulfur * combustibleBasisFactor
        val nitrogenCombustible = nitrogen * combustibleBasisFactor
        val oxygenCombustible = oxygen * combustibleBasisFactor

        // Нижча теплота згоряння
        val lowerHeatingValue = 339 * carbon + 1030 * hydrogen - 108.8 * (oxygen - sulfur) - 25 * moisture

        analysisResult = """
            📊 РЕЗУЛЬТАТИ АНАЛІЗУ ПАЛИВА
            
            🔹 СУХА МАСА:
            • Вуглець: ${"%.2f".format(carbonDry)}%
            • Водень: ${"%.2f".format(hydrogenDry)}%
            • Сірка: ${"%.2f".format(sulfurDry)}%
            • Азот: ${"%.2f".format(nitrogenDry)}%
            • Кисень: ${"%.2f".format(oxygenDry)}%
            
            🔹 ГОРЮЧА МАСА:
            • Вуглець: ${"%.2f".format(carbonCombustible)}%
            • Водень: ${"%.2f".format(hydrogenCombustible)}%
            • Сірка: ${"%.2f".format(sulfurCombustible)}%
            • Азот: ${"%.2f".format(nitrogenCombustible)}%
            • Кисень: ${"%.2f".format(oxygenCombustible)}%
            
            🔥 ТЕПЛОТА ЗГОРЯННЯ:
            • Нижча теплота: ${"%.2f".format(lowerHeatingValue)} кМДж/кг
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Склад Палива",
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
                text = "Введіть елементарний склад палива (%):",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            CompositionTextField(
                value = carbonContent,
                onValueChange = { carbonContent = it },
                label = "Вуглець (C)",
                modifier = Modifier.fillMaxWidth()
            )

            CompositionTextField(
                value = hydrogenContent,
                onValueChange = { hydrogenContent = it },
                label = "Водень (H)",
                modifier = Modifier.fillMaxWidth()
            )

            CompositionTextField(
                value = oxygenContent,
                onValueChange = { oxygenContent = it },
                label = "Кисень (O)",
                modifier = Modifier.fillMaxWidth()
            )

            CompositionTextField(
                value = sulfurContent,
                onValueChange = { sulfurContent = it },
                label = "Сірка (S)",
                modifier = Modifier.fillMaxWidth()
            )

            CompositionTextField(
                value = nitrogenContent,
                onValueChange = { nitrogenContent = it },
                label = "Азот (N)",
                modifier = Modifier.fillMaxWidth()
            )

            CompositionTextField(
                value = moistureContent,
                onValueChange = { moistureContent = it },
                label = "Вологість (W)",
                modifier = Modifier.fillMaxWidth()
            )

            CompositionTextField(
                value = ashContent,
                onValueChange = { ashContent = it },
                label = "Зольність (A)",
                modifier = Modifier.fillMaxWidth()
            )

            // Кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { performFuelAnalysis() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Аналізувати")
                }

                OutlinedButton(
                    onClick = onBackPressed,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Назад")
                }
            }

            // Результат
            if (analysisResult.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = analysisResult,
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
fun FuelCompositionPreview() {
    Lab1Theme {
        FuelCompositionScreen(onBackPressed = {})
    }
}