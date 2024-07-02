package com.example.period_recorder.executeManager

import com.example.period_recorder.audioRecorder.AndroidAudioRecorder
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId

private const val TAG = "PeriodRecorder_ExecutorTest"

class DoTest(private val context: Context)  {

    private var recorder = AndroidAudioRecorder(context)

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm(triggerTime: Long, intent: Intent) {
        val intentExample = Intent(context, PeriodRecord(recorder)::class.java)
//        val intentExample = Intent(context, PeriodRecord()::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intentExample,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        when {

            // If permission is granted, proceed with scheduling exact alarms.
            alarmManager.canScheduleExactAlarms() -> {
                Log.v(TAG, "setAlarm");

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000 + 1000,
                    pendingIntent)
            }
            else -> {
                // Ask users to go to exact alarm page in system settings.
                context.startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }
    }

    fun setStop(triggerTime: Long, intent: Intent) {
        Log.v(TAG, "setStop");
        val intentExample = Intent(context, EndRecord(recorder)::class.java)

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
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000 + 30000,
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

class PeriodRecord(private var recorder: AndroidAudioRecorder ): BroadcastReceiver(){
//class PeriodRecord(): BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v(TAG, "[PeriodRecord] start");
//
//        if(context != null)
//        {
//            recorder = AndroidAudioRecorder(context)
//            val path = GetFilename().getName()
//            File(path).also {
//                recorder.start(it)
//                Log.v(TAG, it.toString());
//            }
//            Toast.makeText(context, "Period record!", Toast.LENGTH_SHORT).show()
//        }
//        else
//        {
//            Log.v(TAG, "[PeriodRecord] Error handle, context is null");
//        }
    }
}

class EndRecord(private var recorder: AndroidAudioRecorder ): BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v(TAG, "[PeriodRecord] end");
//        if(context != null)
//        {
//            recorder.stop()
//        }
//        else
//        {
//            Log.v(TAG, "[EndRecord] Error handle, context is null");
//        }
    }
}