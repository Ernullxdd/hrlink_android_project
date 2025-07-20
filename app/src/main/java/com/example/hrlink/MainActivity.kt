package com.example.hrlink

import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hrlink.ui.theme.HRLinkTheme
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HRLinkTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JWTFetcherUI()
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun JWTFetcherUI() {
    var url by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("منتظر دریافت لینک...") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("لینک") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            status = "در حال بارگذاری..."
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val result = URL(url).readText()
                    val jwtJson = JSONObject(JSONObject(result).getString("JWT"))
                    val cm = CookieManager.getInstance()
                    cm.setCookie("https://m.snappfood.ir", "jwt-access_token=${jwtJson.getString("access_token")}")
                    cm.setCookie("https://m.snappfood.ir", "jwt-token_type=${jwtJson.getString("token_type")}")
                    cm.setCookie("https://m.snappfood.ir", "jwt-refresh_token=${jwtJson.getString("refresh_token")}")
                    cm.setCookie("https://m.snappfood.ir", "jwt-expires_in=${jwtJson.getInt("expires_in")}")
                    status = "انجام شد!"
                } catch (e: Exception) {
                    status = "خطا: ${e.message}"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("ارسال")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(status)
    }
}