package com.pinflow.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinflow.compose.PinFlow
import com.pinflow.compose.PinFlowAnimation
import com.pinflow.compose.PinFlowMode
import com.pinflow.compose.PinFlowValidator
import com.pinflow.sample.ui.theme.PinFlowTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PinFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            LargeTopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            "PinFlow",
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                            ),
                                        )
                                        Text(
                                            "Animated OTP & PIN for Compose",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.largeTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                                ),
                            )
                        },
                        contentWindowInsets = WindowInsets.systemBars,
                    ) { innerPadding ->
                        DemoScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun DemoScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paste a code, type digit-by-digit, or complete a field to see success and motion.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        DemoSection(
            title = "Boxes + Smart Paste",
            description = "Default Material 3 cells. Paste a 4-digit code from the clipboard.",
        ) {
            var pin by remember { mutableStateOf("") }
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                mode = PinFlowMode.Boxes,
            )
            Text(
                text = "Value: $pin",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        DemoSection(
            title = "Underline + Shake",
            description = "Enter 1234 to trigger the error shake animation.",
        ) {
            var pin by remember { mutableStateOf("") }
            val isError = pin == "1234"

            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                mode = PinFlowMode.Underline,
                isError = isError,
                animations = setOf(
                    PinFlowAnimation.ShakeOnError,
                    PinFlowAnimation.Bounce,
                ),
            )

            if (isError) {
                Text(
                    text = "Incorrect code — try again.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }

        DemoSection(
            title = "Secure PIN (Circle)",
            description = "Masked digits with reveal-last-digit. Ideal for app lock or payment PIN.",
        ) {
            var pin by remember { mutableStateOf("") }
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                mode = PinFlowMode.SecurePin,
                revealLastDigit = true,
            )
        }

        DemoSection(
            title = "Success + Slide",
            description = "Enter 5678 — cells slide in and turn success green when complete.",
        ) {
            var pin by remember { mutableStateOf("") }
            val isSuccess = PinFlowValidator.isComplete(pin, length = 4) && pin == "5678"

            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                mode = PinFlowMode.Boxes,
                isSuccess = isSuccess,
                animations = setOf(
                    PinFlowAnimation.Slide,
                    PinFlowAnimation.Bounce,
                    PinFlowAnimation.Glow,
                ),
                onComplete = { /* hook verification here */ },
            )

            if (isSuccess) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = "Verified!",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        DemoSection(
            title = "Single Field",
            description = "One continuous field with letter-spaced digits — compact layouts.",
        ) {
            var pin by remember { mutableStateOf("") }
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                mode = PinFlowMode.SingleField,
                length = 6,
            )
        }

        DemoSection(
            title = "Alphanumeric (6)",
            description = "Promo or recovery codes with letters and digits.",
        ) {
            var pin by remember { mutableStateOf("") }
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                length = 6,
                isAlphanumeric = true,
                mode = PinFlowMode.Boxes,
            )
        }

        DemoSection(
            title = "Theme-aware motion",
            description = "Bounce, glow, and M3 colors adapt to light/dark and dynamic color.",
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("PinFlowDefaults.colors()", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun DemoSection(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DemoScreenPreview() {
    PinFlowTheme {
        DemoScreen()
    }
}
