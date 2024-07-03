package com.example.period_recorder.audioRecorder

import android.content.Context
import java.io.File

interface AudioRecorder {
    fun start(outputFile: File, context: Context)
    fun stop()
}