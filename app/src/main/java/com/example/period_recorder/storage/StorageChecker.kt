package com.example.period_recorder.storage

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import androidx.core.content.getSystemService
import java.util.UUID

private const val TAG = "PR_StorageChecker"
private const val NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 20L

class StorageChecker() {

    var externalStorageRead: Boolean? = null
    var externalStorageWrite: Boolean? = null
    var availableBytes: Long = 0

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

    public fun checkRequestSize(context: Context){
        val storageManager = context.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(context.filesDir)
        availableBytes = storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
            storageManager.allocateBytes(
                appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP
            )

            Log.v(
                TAG, "Access of external storage(read, write): " + "(${externalStorageRead}), "
                        + "(${externalStorageWrite})")
            Log.v(TAG, "available GBs: " + (availableBytes / 1024 / 1024 / 1024).toString())
        } else {
            val storageIntent = Intent().apply {
                // To request that the user remove all app cache files instead, set
                // "action" to ACTION_CLEAR_APP_CACHE.
                action = StorageManager.ACTION_MANAGE_STORAGE
            }
        }
    }
}