package com.example.lab6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab6.ui.theme.Lab6Theme
import kotlin.math.ceil
import kotlin.math.sqrt

class TransmissionCalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab6Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransmissionCalculatorScreen()
                }
            }
        }
    }
}

@Stable
data class ElectricParams(
    var name: String = "",
    var efficiency: String = "",
    var powerFactor: String = "",
    var voltage: String = "",
    var quantity: String = "",
    var ratedPower: String = "",
    var usageCoefficient: String = "",
    var reactivePowerCoefficient: String = "",
    var powerProduct: String = "",
    var current: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectricParamsForm(params: ElectricParams) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = params.name,
        onValueChange = { params.name = it },
        label = { Text("Назва обладнання") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.quantity,
        onValueChange = { params.quantity = it },
        label = { Text("Кількість (n, шт)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.ratedPower,
        onValueChange = { params.ratedPower = it },
        label = { Text("Потужність (Pн, кВт)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.usageCoefficient,
        onValueChange = { params.usageCoefficient = it },
        label = { Text("Kv") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.reactivePowerCoefficient,
        onValueChange = { params.reactivePowerCoefficient = it },
        label = { Text("tgφ") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransmissionCalculatorScreen() {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var equipmentList by remember {
        mutableStateOf(
            listOf(
                ElectricParams(
                    name = "Шліфувальний верстат",
                    efficiency = "0.92",
                    powerFactor = "0.9",
                    voltage = "0.38",
                    quantity = "4",
                    ratedPower = "22",
                    usageCoefficient = "0.33",
                    reactivePowerCoefficient = "1.33"
                ),
                ElectricParams(
                    name = "Свердлильний верстат",
                    efficiency = "0.92",
                    powerFactor = "0.9",
                    voltage = "0.38",
                    quantity = "2",
                    ratedPower = "14",
                    usageCoefficient = "0.12",
                    reactivePowerCoefficient = "1"
                ),
                ElectricParams(
                    name = "Фугувальний верстат",
                    efficiency = "0.92",
                    powerFactor = "0.9",
                    voltage = "0.38",
                    quantity = "4",
                    ratedPower = "42",
                    usageCoefficient = "0.15",
                    reactivePowerCoefficient = "1.33"
                ),
                ElectricParams(
                    name = "Циркулярна пила",
                    efficiency = "0.92",
                    powerFactor = "0.9",
                    voltage = "0.38",
                    quantity = "1",
                    ratedPower = "36",
                    usageCoefficient = "0.3",
                    reactivePowerCoefficient = "1.57"
                ),
                ElectricParams(
                    name = "Прес",
                    efficiency = "0.92",
                    powerFactor = "0.9",
                    voltage = "0.38",
                    quantity = "1",
                    ratedPower = "20",
                    usageCoefficient = "0.5",
                    reactivePowerCoefficient = "0.75"
                ),
                ElectricParams(
                    name = "Полірувальний верстат",
                    efficiency = "0.92",
                    powerFactor = "0.9",
                    voltage = "0.38",
                    quantity = "1",
                    ratedPower = "40",
                    usageCoefficient = "0.23",
                    reactivePowerCoefficient = "1"
                ),
                ElectricParams(
                    name = "Фрезерний верстат",
                    efficiency = "0.92",
                    powerFactor = "0.9",
                    voltage = "0.38",
                    quantity = "2",
                    ratedPower = "32",
                    usageCoefficient = "0.2",
                    reactivePowerCoefficient = "1"
                ),
                ElectricParams(
                    name = "Вентилятор",
                    efficiency = "0.92",
                    powerFactor = "0.9",
                    voltage = "0.38",
                    quantity = "1",
                    ratedPower = "20",
                    usageCoefficient = "0.65",
                    reactivePowerCoefficient = "0.75"
                ),
            )
        )
    }

    var Kr by remember { mutableStateOf("1.25") }
    var Kr2 by remember { mutableStateOf("0.7") }

    var sumOfPowerProduct by remember { mutableStateOf(0.0) }

    var groupUsageCoefficient by remember { mutableStateOf("") }   // Kv
    var effectiveEquipmentAmount by remember { mutableStateOf("") } // ne

    var activeLoadCalculation by remember { mutableStateOf("") }      // Pp
    var reactiveLoadCalculation by remember { mutableStateOf("") }    // Qp
    var fullPower by remember { mutableStateOf("") }                  // Sp
    var groupCurrentCalculationShr1 by remember { mutableStateOf("") } // Ip

    var activeLoadCalculationBus by remember { mutableStateOf("") }
    var reactiveLoadCalculationBus by remember { mutableStateOf("") }
    var fullPowerBus by remember { mutableStateOf("") }
    var groupCurrentCalculationBus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {

        Text(
            text = "Electrical Load Calculator",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { equipmentList = equipmentList + ElectricParams() },
            modifier = Modifier.padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Додати обладнання")
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(equipmentList) { params ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp)
                ) {
                    ElectricParamsForm(params = params)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                var sumOfProduct = 0.0
                var sumOfPower = 0.0
                var sumOfPowerSquare = 0.0

                equipmentList.forEach { params ->
                    val n = params.quantity.toDoubleOrNull() ?: 0.0
                    val Pn = params.ratedPower.toDoubleOrNull() ?: 0.0
                    val U = params.voltage.toDoubleOrNull() ?: 0.38
                    val cosPhi = params.powerFactor.toDoubleOrNull() ?: 0.9
                    val eta = params.efficiency.toDoubleOrNull() ?: 0.9
                    val Kv = params.usageCoefficient.toDoubleOrNull() ?: 0.0

                    params.powerProduct = (n * Pn).toString()

                    val Ip = if (U > 0 && cosPhi > 0 && eta > 0) {
                        (n * Pn) / (sqrt(3.0) * U * cosPhi * eta)
                    } else 0.0
                    params.current = Ip.toString()

                    sumOfProduct += (n * Pn) * Kv
                    sumOfPower += (n * Pn)
                    sumOfPowerSquare += n * Pn * Pn
                }

                sumOfPowerProduct = sumOfProduct

                val KvGroup =
                    if (sumOfPower > 0) sumOfProduct / sumOfPower else 0.0
                val ne =
                    if (sumOfPowerSquare > 0) {
                        ceil((sumOfPower * sumOfPower) / sumOfPowerSquare)
                    } else 0.0

                groupUsageCoefficient = "%.4f".format(KvGroup)
                effectiveEquipmentAmount = ne.toInt().toString()

                val KrValue = Kr.toDoubleOrNull() ?: 1.25
                val PH = 22.0
                val tan_phi = 1.57
                val Un = 0.38

                val Pp = KrValue * sumOfPowerProduct
                val Qp = KvGroup * PH * tan_phi
                val Sp = sqrt(Pp * Pp + Qp * Qp)
                val Ip = if (Un > 0) Pp / Un else 0.0

                activeLoadCalculation = "%.2f".format(Pp)
                reactiveLoadCalculation = "%.2f".format(Qp)
                fullPower = "%.2f".format(Sp)
                groupCurrentCalculationShr1 = "%.2f".format(Ip)

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Розрахувати")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Коефіцієнт використання Kv: $groupUsageCoefficient")
        Text("Ефективна кількість ЕП ne: $effectiveEquipmentAmount")
        Text("Коефіцієнт резерву Kr: $Kr")
        Text("Розрахункове активне навантаження Pp: $activeLoadCalculation кВт")
        Text("Розрахункове реактивне навантаження Qp: $reactiveLoadCalculation квар")
        Text("Повна потужність Sp: $fullPower кВА")
        Text("Розрахунковий струм Ip: $groupCurrentCalculationShr1 А")
    }
}

@Preview(showBackground = true)
@Composable
fun TransmissionCalculatorPreview() {
    Lab6Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TransmissionCalculatorScreen()
        }
    }
}
