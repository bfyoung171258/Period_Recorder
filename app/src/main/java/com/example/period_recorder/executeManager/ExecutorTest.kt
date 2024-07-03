package com.example.period_recorder.executeManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import android.widget.Toast
import com.example.period_recorder.audioRecorder.AndroidAudioRecorder
import com.example.period_recorder.storage.GetFilename
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId

private const val TAG = "PR_ExecutorTest"

class SettingPR(private val context: Context) {

    private var recorder = AndroidAudioRecorder()

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm(triggerTime: Long, intent: Intent) {
        val intentExample = Intent(context, PeriodRecord()::class.java)
        intentExample.putExtra("object", recorder)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intentExample,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        when {
            // If permission is granted, proceed with scheduling exact alarms.
            alarmManager.canScheduleExactAlarms() -> {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000 + 10000,
                    pendingIntent)

                Log.v(TAG, "setAlarm");
                Toast.makeText(context, "[PR_ExecutorTest] setAlarm", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Ask users to go to exact alarm page in system settings.
                context.startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }
    }

    fun setStop(triggerTime: Long, intent: Intent) {
        val intentExample = Intent(context, EndRecord()::class.java)
        intentExample.putExtra("object", recorder)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intentExample,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        when {
            // If permission is granted, proceed with scheduling exact alarms.
            alarmManager.canScheduleExactAlarms() -> {
                Log.v(TAG, "setStop");

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000 + 20000,
                    pendingIntent)
            }
            else -> {
                // Ask users to go to exact alarm page in system settings.
                context.startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }
    }

//    fun cancelAlarm(intent: Intent) {
//
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//        alarmManager.cancel(pendingIntent)
//    }
//
//    fun setRepeatingAlarm(interval: Long, triggerTime: Long, intent: Intent) {
//
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = System.currentTimeMillis()
//        calendar[Calendar.HOUR_OF_DAY] = 8
//        calendar[Calendar.MINUTE] = 0
//
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent)
//    }
//
//    fun setAlarmWithWindow(triggerTime: Long, windowLength: Long, intent: Intent) {
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerTime, windowLength, pendingIntent)
//    }

    fun getAlarmManager(): AlarmManager {
        return alarmManager
    }
}

class PeriodRecord: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null)
        {
            val derivedObject = intent?.getSerializableExtra("object") as AndroidAudioRecorder

            val path = GetFilename().getName()
            File(path).also {
                derivedObject.start(it, context)
                Log.v(TAG, it.toString());
            }
            Log.v(TAG, "start");
            Toast.makeText(context, "[PR_ExecutorTest] start", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Log.v(TAG, "[PR_ExecutorTest] Error handle, context is null");
        }
    }
}

class EndRecord: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val derivedObject = intent?.getSerializableExtra("object") as AndroidAudioRecorder
        if(context != null)
        {
            derivedObject.stop()
            Log.v(TAG, "end");
            Toast.makeText(context, "[PR_ExecutorTest] end", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Log.v(TAG, "[PR_ExecutorTest] Error handle, context is null");
        }
    }
}