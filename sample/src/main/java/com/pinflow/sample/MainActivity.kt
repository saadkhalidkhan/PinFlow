package com.pinflow.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinflow.compose.PinFlow
import com.pinflow.compose.PinFlowAnimation
import com.pinflow.compose.PinFlowCellState
import com.pinflow.compose.PinFlowMode
import com.pinflow.compose.PinFlowThemes
import com.pinflow.compose.PinFlowValidator
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.pinflow.sample.ui.theme.PinFlowTheme

private object Routes {
    const val Demo = "demo"
    const val AnimationShowcase = "animation_showcase"
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PinFlowTheme {
                val navController = rememberNavController()
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                val onShowcase = currentRoute == Routes.AnimationShowcase

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
                                            if (onShowcase) "Animation Showcase" else "PinFlow",
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                            ),
                                        )
                                        Text(
                                            if (onShowcase) {
                                                "MVP 3 — motion & verification states"
                                            } else {
                                                "Animated OTP & PIN for Compose"
                                            },
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                        )
                                    }
                                },
                                navigationIcon = {
                                    if (onShowcase) {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    if (!onShowcase) {
                                        TextButton(
                                            onClick = {
                                                navController.navigate(Routes.AnimationShowcase)
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Animation,
                                                contentDescription = null,
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Animations")
                                        }
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
                        NavHost(
                            navController = navController,
                            startDestination = Routes.Demo,
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            composable(Routes.Demo) {
                                DemoScreen()
                            }
                            composable(Routes.AnimationShowcase) {
                                AnimationShowcaseScreen()
                            }
                        }
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
            title = "Neon + gradient borders",
            description = "PinFlowThemes.Neon() with animated gradient borders and a custom cursor.",
        ) {
            var pin by remember { mutableStateOf("") }
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                style = PinFlowThemes.Neon(),
                hapticEnabled = true,
                animations = setOf(PinFlowAnimation.Glow, PinFlowAnimation.Bounce),
            )
        }

        DemoSection(
            title = "Glass theme",
            description = "Translucent cells and soft gradient borders — adapts to light and dark.",
        ) {
            var pin by remember { mutableStateOf("") }
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                style = PinFlowThemes.Glass(),
            )
        }

        DemoSection(
            title = "Minimal theme",
            description = "Thin monochrome borders, no fill — PinFlowThemes.Minimal().",
        ) {
            var pin by remember { mutableStateOf("") }
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                mode = PinFlowMode.Underline,
                style = PinFlowThemes.Minimal(),
            )
        }

        DemoSection(
            title = "Custom cell content",
            description = "Render your own digit UI while keeping PinFlow keyboard and paste behavior.",
        ) {
            var pin by remember { mutableStateOf("") }
            val gradient = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary,
                ),
            )
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                borderBrush = gradient,
                cursorColor = Color.Red,
                cursorWidth = 3.dp,
                cellContent = { digit, state ->
                    if (digit == null) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    color = when (state) {
                                        PinFlowCellState.Focused -> MaterialTheme.colorScheme.primary
                                        PinFlowCellState.Error -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.outline
                                    },
                                    shape = CircleShape,
                                ),
                        )
                    } else {
                        Text(
                            text = digit.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = when (state) {
                                PinFlowCellState.Error -> MaterialTheme.colorScheme.error
                                PinFlowCellState.Success -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                },
            )
        }

        DemoSection(
            title = "Haptic feedback",
            description = "hapticEnabled = true — feel a tap on each digit (device dependent).",
        ) {
            var pin by remember { mutableStateOf("") }
            PinFlow(
                value = pin,
                onValueChange = { pin = it },
                hapticEnabled = true,
                mode = PinFlowMode.Circle,
            )
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
