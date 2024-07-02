package com.example.period_recorder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.util.UUID

import androidx.core.app.ActivityCompat

import com.example.period_recorder.audioRecorder.AndroidAudioRecorder
import com.example.period_recorder.storage.StorageChecker
import com.example.period_recorder.storage.GetFilename
import com.example.period_recorder.executeManager.DoTest

private const val TAG = "PeriodRecorder_Main"

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val CREATE_FILE_REQUEST_CODE = 1

private const val NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 20L

class MainActivity : AppCompatActivity() {

    private lateinit var textBar: TextView
    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button
    private lateinit var btnScheduleStart: Button
    private lateinit var btnScheduleEnd: Button

    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }
    private var storageAccessCheck = StorageChecker()

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

        Log.v(TAG, "Access of external storage: " +
            "read(${storageAccessCheck.externalStorageRead}), " +
                    "write(${storageAccessCheck.externalStorageWrite})");
        checkRequestSize()
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
            val test = DoTest(this)
            test.setAlarm(5, Intent(this, this::class.java))
//            test.setStop(5, Intent(this, this::class.java))
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
            recorder.start(it)
            Log.v(TAG, it.toString());
        }
    }

    private fun recordEnd() {
        btnStart.isEnabled = true
        btnEnd.isEnabled = false

        Toast.makeText(this, "Recording stop", Toast.LENGTH_SHORT).show()

        createInternalFile()
        createExternalFile()

        recorder.stop()
    }

    private fun createInternalFile() {
        val filename = "internalFile.txt"
        val fileContents = "Hello world!"
        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }

        this.openFileInput(filename).bufferedReader().useLines { lines ->
            lines.fold("") { some, text ->
                "$some\n$text"
            }
        }

        val dirName = File("abcD")
        this.getDir(dirName.path, Context.MODE_PRIVATE)

        val path = filesDir.absolutePath
        Log.v(TAG, path.toString());
    }

    private fun createExternalFile() {
        // select volume
        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(applicationContext, null)
        val primaryExternalStorage = externalStorageVolumes[0]

        val a = Environment.DIRECTORY_MUSIC
        Log.v(TAG, a.toString());

        val b = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        Log.v(TAG, b.toString());

        try {
            val appSpecificExternalDir = File(this.getExternalFilesDir(null), "externalFile.txt")
            appSpecificExternalDir.appendText("hello") // write
            Toast.makeText(applicationContext, "Success gen", Toast.LENGTH_SHORT)
                .show()

            Log.v(TAG, "external path:" + appSpecificExternalDir.absolutePath.toString());
        } catch (e: Exception) {
            e.printStackTrace()
            // on below line we are displaying a toast message as fail to generate PDF
            Toast.makeText(applicationContext, "Fail to gen file", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun checkRequestSize() {
        val storageManager = applicationContext.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
        val availableBytes: Long =
            storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
            val str = "available GBs: "
            Log.v(TAG, str + (availableBytes / 1024 / 1024 / 1024).toString());
            storageManager.allocateBytes(
                appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP)
        } else {
            val storageIntent = Intent().apply {
                // To request that the user remove all app cache files instead, set
                // "action" to ACTION_CLEAR_APP_CACHE.
                action = ACTION_MANAGE_STORAGE
            }
        }
    }
}

