package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundEngine(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    private var sfxEnabled = true

    @Volatile
    private var sfxVolume = 0.85f

    @Volatile
    private var musicEnabled = true

    @Volatile
    private var musicVolume = 0.45f

    @Volatile
    private var vibrationEnabled = true

    private var musicJob: Job? = null
    private var isAppForeground = true

    private val sampleRate = 22050

    // Pentatonic frequencies for pleasant ascending edge connections
    private val connectScale = doubleArrayOf(
        523.25, // C5
        587.33, // D5
        659.25, // E5
        783.99, // G5
        880.00, // A5
        1046.50, // C6
        1174.66, // D6
        1318.51, // E6
    )

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Throwable) {
            null
        }
    }

    fun updateSettings(
        soundEnabled: Boolean,
        sfxVol: Float,
        musicOn: Boolean,
        musicVol: Float,
        vibrationOn: Boolean,
    ) {
        val wasMusicActive = musicEnabled && isAppForeground
        sfxEnabled = soundEnabled
        sfxVolume = sfxVol.coerceIn(0f, 1f)
        musicEnabled = musicOn
        musicVolume = musicVol.coerceIn(0f, 1f)
        vibrationEnabled = vibrationOn

        val shouldPlayMusic = musicEnabled && isAppForeground
        if (shouldPlayMusic && !wasMusicActive) {
            startMusicLoop()
        } else if (!shouldPlayMusic) {
            stopMusicLoop()
        }
    }

    fun onForegroundChanged(foreground: Boolean) {
        isAppForeground = foreground
        if (foreground && musicEnabled) {
            startMusicLoop()
        } else {
            stopMusicLoop()
        }
    }

    fun playClick() {
        if (!sfxEnabled || sfxVolume <= 0.01f) return
        playToneBuffer(
            createToneSamples(
                freqs = doubleArrayOf(680.0),
                durationSec = 0.045,
                decay = 45.0,
                amp = 0.35f * sfxVolume,
            ),
        )
    }

    fun playDrawStart() {
        if (!sfxEnabled || sfxVolume <= 0.01f) return
        playToneBuffer(
            createSweepSamples(
                startFreq = 392.0,
                endFreq = 523.25,
                durationSec = 0.09,
                amp = 0.30f * sfxVolume,
            ),
        )
        vibratePulse(12L, 60)
    }

    fun playEdgeConnect(stepIndex: Int) {
        vibratePulse(16L, 85)
        if (!sfxEnabled || sfxVolume <= 0.01f) return
        val freq = connectScale[stepIndex.coerceAtLeast(0) % connectScale.size]
        playToneBuffer(
            createToneSamples(
                freqs = doubleArrayOf(freq, freq * 1.5),
                durationSec = 0.13,
                decay = 18.0,
                amp = 0.38f * sfxVolume,
            ),
        )
    }

    fun playInvalidMove() {
        vibratePulse(45L, 160)
        if (!sfxEnabled || sfxVolume <= 0.01f) return
        playToneBuffer(
            createToneSamples(
                freqs = doubleArrayOf(175.0, 185.0),
                durationSec = 0.16,
                decay = 14.0,
                amp = 0.42f * sfxVolume,
            ),
        )
    }

    fun playLevelComplete() {
        vibratePulse(50L, 180)
        if (!sfxEnabled || sfxVolume <= 0.01f) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            val totalSamples = (sampleRate * 0.65).toInt()
            val pcm = ShortArray(totalSamples)
            notes.forEachIndexed { idx, freq ->
                val startSample = (idx * 0.085 * sampleRate).toInt()
                for (i in startSample until totalSamples) {
                    val t = (i - startSample).toDouble() / sampleRate
                    val env = exp(-6.5 * t) * (1.0 - exp(-120.0 * t))
                    val s = (sin(2.0 * PI * freq * t) * 0.7 + sin(2.0 * PI * freq * 2.0 * t) * 0.3) * env
                    val v = pcm[i] + (s * Short.MAX_VALUE * 0.28 * sfxVolume).toInt()
                    pcm[i] = v.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
            playPcmSync(pcm)
        }
    }

    fun playLevelFail() {
        vibratePulse(65L, 200)
        if (!sfxEnabled || sfxVolume <= 0.01f) return
        playToneBuffer(
            createSweepSamples(
                startFreq = 320.0,
                endFreq = 150.0,
                durationSec = 0.30,
                amp = 0.40f * sfxVolume,
            ),
        )
    }

    fun playCoinReward() {
        if (!sfxEnabled || sfxVolume <= 0.01f) return
        scope.launch {
            val part1 = createToneSamples(doubleArrayOf(987.77), 0.08, 22.0, 0.34f * sfxVolume)
            val part2 = createToneSamples(doubleArrayOf(1318.51), 0.18, 12.0, 0.38f * sfxVolume)
            playPcmSync(part1 + part2)
        }
    }

    private fun vibratePulse(durationMs: Long, amplitude: Int) {
        if (!vibrationEnabled) return
        try {
            val vib = vibrator ?: return
            if (!vib.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(durationMs)
            }
        } catch (_: Throwable) {
            // Safe no-op in tests or devices without vibrator
        }
    }

    private fun startMusicLoop() {
        if (musicJob?.isActive == true) return
        musicJob = scope.launch {
            var track: AudioTrack? = null
            try {
                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                ).coerceAtLeast(4096)

                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    )
                    .setBufferSizeInBytes(minBuf * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()

                // Ambient chord progression: Am9 -> Fmaj7 -> Cmaj9 -> G6
                val chords = arrayOf(
                    doubleArrayOf(220.0, 261.63, 329.63, 493.88),
                    doubleArrayOf(174.61, 220.0, 261.63, 329.63),
                    doubleArrayOf(196.0, 261.63, 329.63, 392.00),
                    doubleArrayOf(196.0, 246.94, 293.66, 329.63),
                )
                val chunkSamples = sampleRate / 5 // 200ms chunks for responsive volume/stop
                val buffer = ShortArray(chunkSamples)
                var globalSample = 0L
                val samplesPerChord = sampleRate * 3L // 3 seconds per chord

                while (isActive && musicEnabled && isAppForeground) {
                    val vol = musicVolume
                    for (i in 0 until chunkSamples) {
                        val sIdx = globalSample + i
                        val chordIdx = ((sIdx / samplesPerChord) % chords.size).toInt()
                        val localT = (sIdx % samplesPerChord).toDouble() / samplesPerChord
                        // Smooth crossfade envelope per chord
                        val fade = when {
                            localT < 0.15 -> localT / 0.15
                            localT > 0.85 -> (1.0 - localT) / 0.15
                            else -> 1.0
                        }
                        val tSec = sIdx.toDouble() / sampleRate
                        val chord = chords[chordIdx]
                        var sampleVal = 0.0
                        for (f in chord) {
                            sampleVal += sin(2.0 * PI * f * tSec)
                        }
                        // Soft gentle shimmer overtone
                        val shimmer = sin(2.0 * PI * chord.last() * 2.0 * tSec) * 0.15
                        val combined = ((sampleVal / chord.size) + shimmer) * fade * 0.18 * vol
                        buffer[i] = (combined * Short.MAX_VALUE)
                            .toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()
                    }
                    globalSample += chunkSamples
                    track.write(buffer, 0, buffer.size)
                }
            } catch (_: Throwable) {
                // Safe no-op if audio device unavailable (e.g. JVM Robolectric tests)
            } finally {
                try {
                    track?.pause()
                    track?.flush()
                    track?.release()
                } catch (_: Throwable) {
                }
            }
        }
    }

    private fun stopMusicLoop() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun playToneBuffer(pcm: ShortArray) {
        scope.launch {
            playPcmSync(pcm)
        }
    }

    private fun playPcmSync(pcm: ShortArray) {
        var track: AudioTrack? = null
        try {
            val byteLen = pcm.size * 2
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build(),
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                )
                .setBufferSizeInBytes(byteLen.coerceAtLeast(1024))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcm, 0, pcm.size)
            track.play()
            val durationMs = (pcm.size * 1000L / sampleRate) + 40L
            Thread.sleep(durationMs)
        } catch (_: Throwable) {
            // Safe no-op on Robolectric or audio interruption
        } finally {
            try {
                track?.release()
            } catch (_: Throwable) {
            }
        }
    }

    private fun createToneSamples(
        freqs: DoubleArray,
        durationSec: Double,
        decay: Double,
        amp: Float,
    ): ShortArray {
        val count = (sampleRate * durationSec).toInt().coerceAtLeast(1)
        val out = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-decay * t) * (1.0 - exp(-200.0 * t))
            var sum = 0.0
            for (f in freqs) {
                sum += sin(2.0 * PI * f * t)
            }
            val sample = (sum / freqs.size) * env * amp * Short.MAX_VALUE
            out[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return out
    }

    private fun createSweepSamples(
        startFreq: Double,
        endFreq: Double,
        durationSec: Double,
        amp: Float,
    ): ShortArray {
        val count = (sampleRate * durationSec).toInt().coerceAtLeast(1)
        val out = ShortArray(count)
        var phase = 0.0
        for (i in 0 until count) {
            val p = i.toDouble() / count
            val freq = startFreq + (endFreq - startFreq) * p
            phase += 2.0 * PI * freq / sampleRate
            val env = (1.0 - p) * (1.0 - exp(-150.0 * (i.toDouble() / sampleRate)))
            val sample = sin(phase) * env * amp * Short.MAX_VALUE
            out[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return out
    }

    fun release() {
        stopMusicLoop()
    }
}
