package com.santansarah.barcodescanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santansarah.barcodescanner.data.UriHelper
import com.santansarah.barcodescanner.ui.theme.BarcodeScannerTheme
import com.santansarah.barcodescanner.ui.theme.BlueUri
import com.santansarah.barcodescanner.ui.theme.brightYellow
import com.santansarah.barcodescanner.ui.theme.lightestGray
import com.santansarah.barcodescanner.ui.theme.primary
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var barcodeScanner: BarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            BarcodeScannerTheme {
                // A surface container using the 'background' color from the theme
                val scope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = brightYellow
                ) {

                    val barcodeResults =
                        barcodeScanner.barCodeResults.collectAsStateWithLifecycle()
                    LaunchedEffect(true) {
                        barcodeScanner.startScan()
                    }
                    ScanBarcode(
                        barcodeScanner::startScan,
                        barcodeResults.value
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanBarcode(
    onScanBarcode: suspend () -> Unit,
    barcodeValue: String?
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var text by remember { mutableStateOf("Barcode value will appear here") }
    if (barcodeValue != null) {
        text = barcodeValue
    }
    if (UriHelper().isUrl(text)) {
        UriHelper().openLinkInBrowser(context, text)
    }



    SelectionContainer {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DisableSelection {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(.85f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    onClick = {
                        scope.launch {
                            onScanBarcode()
                        }
                    }) {
                    Text(
                        text = "Scan Barcode",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayMedium,
                        color = lightestGray,
                        //style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            if (UriHelper().isUrl(text)) {
                ClickableText(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .selectable(true) {},
                    text = AnnotatedString(text),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = BlueUri,
                        textDecoration = TextDecoration.Underline
                    ),
                    onClick = { UriHelper().openLinkInBrowser(context, text) }
                )

            } else {

                Text(
                    modifier = Modifier
                        .padding(horizontal = 10.dp),
                    text = text,
                    style = MaterialTheme.typography.headlineMedium
                )
            }


            Spacer(modifier = Modifier.height(20.dp))
            if (barcodeValue != null) {
                DisableSelection {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString((text)))
                            Toast.makeText(context, "copied: \"$text\"", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Copy")
                    }
                }

            }
        }
    }
}



@Preview
@Composable
fun PreviewScanBarcode() {
    BarcodeScannerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = primary
        ) {

            ScanBarcode({}, null)
        }
    }
}
