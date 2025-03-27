package com.example.androidcoroutines

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoroutineExampleScreen() {
    // CoroutineScope Compose ortamında lifecycle'a bağlanır ve otomatik iptal edilir.
    val coroutineScope = rememberCoroutineScope()

    // Kullanıcıya gösterilecek durumlar
    var resultText by remember { mutableStateOf("Başlamak için butona bas!") }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Coroutine Eğitim Projesi") })
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (loading) CircularProgressIndicator()

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = resultText)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            loading = true
                            resultText = "Coroutine başlatıldı!"

                            // Paralel iki işlemi başlatmak için async coroutine'i kullanalım.
                            val result1Deferred = async { getDataFromApi1() }
                            val result2Deferred = async { getDataFromApi2() }

                            // İki coroutine'in tamamlanmasını bekliyoruz (paralel çalışma)
                            val result1 = result1Deferred.await()
                            val result2 = result2Deferred.await()

                            // IO thread'ten Main thread'e geçip sonucu UI'da gösterelim
                            withContext(Dispatchers.Main) {
                                resultText = "Sonuçlar:\n$result1\n$result2"
                                loading = false
                            }
                        }
                    }
                ) {
                    Text("Coroutine'i Başlat")
                }
            }
        }
    )
}

// Coroutine suspend fonksiyonları simüle edelim (Örneğin API çağrıları)
private suspend fun getDataFromApi1(): String {
    delay(2000) // 2 saniyelik gecikme
    return "API 1'den veri geldi!"
}

private suspend fun getDataFromApi2(): String {
    delay(3000) // 3 saniyelik gecikme
    return "API 2'den veri geldi!"
}