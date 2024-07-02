package com.example.period_recorder.storage

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat;
import java.util.Date
import java.util.Locale

class GetFilename {
    public fun getName(): String{
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "Period_Recorder")
        dir.mkdir()

        val formatter = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
        val now: Date = Date()
        val path = dir.absolutePath.toString() + "/rec_" + formatter.format(now) + ".mp3"

        return path
    }
}