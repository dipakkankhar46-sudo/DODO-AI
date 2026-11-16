package com.dodoai.assistant

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.RecognizerIntent
import android.content.Intent
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) {
            tts?.language = Locale("hi", "IN")
        }

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 50, 30, 30)

        val title = TextView(this)
        title.text = "DODO AI"
        title.textSize = 30f

        val input = EditText(this)
        input.hint = "DODO से कुछ पूछो..."

        val button = Button(this)
        button.text = "बोलो DODO"

        val output = TextView(this)
        output.textSize = 18f

        button.setOnClickListener {
            val question = input.text.toString()
            output.text = "आपने कहा: $question"
            tts?.speak(question, TextToSpeech.QUEUE_FLUSH, null, "dodo")
        }

        layout.addView(title)
        layout.addView(input)
        layout.addView(button)
        layout.addView(output)

        setContentView(layout)
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}
