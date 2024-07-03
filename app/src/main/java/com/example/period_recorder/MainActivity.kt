package com.example.period_recorder

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

import androidx.core.app.ActivityCompat

import com.example.period_recorder.audioRecorder.AndroidAudioRecorder
import com.example.period_recorder.storage.StorageChecker
import com.example.period_recorder.storage.GetFilename
import com.example.period_recorder.executeManager.SettingPR

private const val TAG = "PR_Main"

class MainActivity : AppCompatActivity() {

    private lateinit var textBar: TextView
    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button
    private lateinit var btnScheduleStart: Button
    private lateinit var btnScheduleEnd: Button
    private var storageAccessCheck = StorageChecker()
    private val recorder = AndroidAudioRecorder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()

        storageAccessCheck.checkRequestSize(applicationContext)
    }

    private fun findViews() {
        btnStart = findViewById(R.id.button)
        btnEnd = findViewById(R.id.button2)
        textBar = findViewById(R.id.testView1)
        btnScheduleStart = findViewById(R.id.button3)
        btnScheduleEnd = findViewById(R.id.button4)

        btnStart.isEnabled = true
        btnEnd.isEnabled = false

        btnStart.setOnClickListener {
            recordStart()
        }

        btnEnd.setOnClickListener {
            recordEnd()
        }

        btnScheduleStart.setOnClickListener {
            val pr = SettingPR(this)
            pr.setAlarm(5, Intent(this, this::class.java))
            pr.setStop(5, Intent(this, this::class.java))
        }

        btnScheduleEnd.setOnClickListener {
        }
    }

    private fun recordStart() {
        btnStart.isEnabled = false
        btnEnd.isEnabled = true

        Toast.makeText(this, "Recording start", Toast.LENGTH_SHORT).show()

        val path = GetFilename().getName()
        File(path).also {
            recorder.start(it, this)
            Log.v(TAG, it.toString());
        }
    }

    private fun recordEnd() {
        btnStart.isEnabled = true
        btnEnd.isEnabled = false

        Toast.makeText(this, "Recording stop", Toast.LENGTH_SHORT).show()
        recorder.stop()
    }
}

