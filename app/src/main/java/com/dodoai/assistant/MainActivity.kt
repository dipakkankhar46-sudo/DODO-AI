
package com.dodoai.assistant
import android.net.Uri
import android.content.ActivityNotFoundException
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.*
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : Activity() {

    private var tts: TextToSpeech? = null
    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("hi", "IN")
            }
        }

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 50, 30, 30)

        layout.setBackgroundColor(0xFF101827.toInt())

        val title = TextView(this)
        title.text = "🤖 DODO AI"
        title.textSize = 32f
        title.setTextColor(0xFF00D9FF.toInt())

        val button = Button(this)
        button.text = "🎤 DODO से बात करो"

        output = TextView(this)
        output.text = "नमस्ते! मैं DODO हूँ।"
        output.textSize = 20f
        output.setTextColor(0xFFFFFFFF.toInt())

        button.setOnClickListener {
            listen()
        }

        layout.addView(title)
        layout.addView(output)
        layout.addView(button)

        setContentView(layout)
    }

    private fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "hi-IN"
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "बोलिए, मैं सुन रहा हूँ"
        )

        try {
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
            speak("माफ कीजिए, Voice Recognition उपलब्ध नहीं है।")
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            val command = results?.firstOrNull() ?: return

            output.text = "आपने कहा: $command"
            processCommand(command.lowercase(Locale.ROOT))
        }
    }

    private fun processCommand(command: String) {

        when {
            command.contains("समय") || command.contains("time") -> {
                val time = SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
                ).format(Date())
                speak("अभी समय है $time")
            }

            command.contains("नमस्ते") ||
            command.contains("hello") -> {
                speak("नमस्ते! मैं DODO हूँ। बताइए क्या करना है?")
            }

            command.contains("यूट्यूब") ||
            command.contains("youtube") -> {
                openApp("com.google.android.youtube", "YouTube")
            }

            command.contains("व्हाट्सएप") ||
            command.contains("whatsapp") -> {
                openApp("com.whatsapp", "WhatsApp")
            }

            command.contains("बंद") -> {
                speak("ठीक है।")
            }

            else -> {
                speak("मैंने आपका आदेश सुना है। यह कमांड अभी मेरे अंदर जोड़ी नहीं गई है।")
            }
        }
    }

    private fun openApp(packageName: String, appName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            speak("$appName खोल रहा हूँ।")
            startActivity(launchIntent)
        } else {
            speak("$appName आपके फोन में इंस्टॉल नहीं है।")
        }
    }

    private fun speak(message: String) {
        output.text = message
        tts?.speak(
            message,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "DODO_REPLY"
        )
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

