package com.example.period_recorder.audioRecorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.Serializable

class AndroidAudioRecorder: Serializable, AudioRecorder {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(context: Context): MediaRecorder{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        }else{
            MediaRecorder()
        }
    }

    override fun start(outputFile: File, context: Context){
        createRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)

            prepare()
            start()

            recorder = this
        }
    }

    override fun stop(){
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}