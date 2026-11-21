package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.GameSettings
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppStrings
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonGridBackground
import com.example.ui.components.NeonTopBar
import com.example.ui.theme.NavyDeepest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: GameSettings,
    onUpdateSettings: ((GameSettings) -> GameSettings) -> Unit,
    onResetProgress: () -> Unit,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    val strings = LocalAppStrings.current
    var showResetDialog by remember { mutableStateOf(false) }

    NeonGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NeonTopBar(title = strings.settings, onBack = onBack)

            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Sound Effects Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan.copy(alpha = 0.5f),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingToggleRow(
                            title = strings.soundEffects,
                            icon = Icons.AutoMirrored.Rounded.VolumeUp,
                            accent = NeonCyan,
                            checked = settings.soundEnabled,
                            testTag = "settings_sfx_switch",
                            onCheckedChange = { enabled ->
                                onUpdateSettings { it.copy(soundEnabled = enabled) }
                            },
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(strings.sfxVolume, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(
                                "${(settings.sfxVolume * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonCyan,
                            )
                        }
                        Slider(
                            value = settings.sfxVolume,
                            onValueChange = { vol -> onUpdateSettings { it.copy(sfxVolume = vol) } },
                            enabled = settings.soundEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = NavySurfaceElevated,
                            ),
                        )
                    }
                }

                // Background Music Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonViolet.copy(alpha = 0.5f),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingToggleRow(
                            title = strings.backgroundMusic,
                            icon = Icons.Rounded.MusicNote,
                            accent = NeonViolet,
                            checked = settings.musicEnabled,
                            testTag = "settings_music_switch",
                            onCheckedChange = { enabled ->
                                onUpdateSettings { it.copy(musicEnabled = enabled) }
                            },
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(strings.musicVolume, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(
                                "${(settings.musicVolume * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonViolet,
                            )
                        }
                        Slider(
                            value = settings.musicVolume,
                            onValueChange = { vol -> onUpdateSettings { it.copy(musicVolume = vol) } },
                            enabled = settings.musicEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonViolet,
                                activeTrackColor = NeonViolet,
                                inactiveTrackColor = NavySurfaceElevated,
                            ),
                        )
                    }
                }

                // Vibration Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonEmerald.copy(alpha = 0.5f),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingToggleRow(
                            title = strings.vibration,
                            icon = Icons.Rounded.Vibration,
                            accent = NeonEmerald,
                            checked = settings.vibrationEnabled,
                            testTag = "settings_vibration_switch",
                            onCheckedChange = { enabled ->
                                onUpdateSettings { it.copy(vibrationEnabled = enabled) }
                            },
                        )
                    }
                }

                // Language Selection Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan.copy(alpha = 0.4f),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.language,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            AppLanguage.entries.forEach { lang ->
                                val selected = settings.languageCode == lang.code
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        onUpdateSettings { it.copy(languageCode = lang.code) }
                                    },
                                    label = { Text(lang.nativeName) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonCyan.copy(alpha = 0.22f),
                                        selectedLabelColor = NeonCyan,
                                        labelColor = TextSecondary,
                                    ),
                                )
                            }
                        }
                    }
                }

                // Reset Progress Button
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("settings_reset_button"),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, NeonCoral.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = NeonCoral.copy(alpha = 0.1f),
                        contentColor = NeonCoral,
                    ),
                ) {
                    Icon(Icons.Rounded.DeleteForever, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.resetProgress)
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = NavySurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text(strings.resetConfirmTitle) },
            text = { Text(strings.resetConfirmBody) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onResetProgress()
                    },
                ) {
                    Text(strings.confirmReset, color = NeonCoral)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(strings.cancel, color = NeonCyan)
                }
            },
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    icon: ImageVector,
    accent: Color,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = NavyDeepest,
                checkedTrackColor = accent,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = NavySurfaceElevated,
            ),
        )
    }
}
