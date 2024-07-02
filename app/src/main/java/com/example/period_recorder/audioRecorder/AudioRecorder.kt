package com.example.period_recorder.audioRecorder

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}