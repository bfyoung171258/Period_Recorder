package com.example.period_recorder.storage

import android.os.Environment

class StorageChecker {

    var externalStorageRead: Boolean? = null
    var externalStorageWrite: Boolean? = null

    init{
        externalStorageRead = isExternalStorageWritable()
        externalStorageWrite = isExternalStorageReadable()
    }

    // Checks if a volume containing external storage is available for read and write.
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Checks if a volume containing external storage is available to at least read.
    private fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }
}