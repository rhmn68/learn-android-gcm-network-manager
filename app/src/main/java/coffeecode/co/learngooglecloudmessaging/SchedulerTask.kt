package coffeecode.co.learngooglecloudmessaging

import android.content.Context
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.PeriodicTask
import com.google.android.gms.gcm.Task

class SchedulerTask(context: Context){

    private var mGcmNetworkManager: GcmNetworkManager? = null

    init {
        mGcmNetworkManager = GcmNetworkManager.getInstance(context)
    }

    fun createPeriodicTask() {
        val periodicTask: Task = PeriodicTask.Builder()
            .setService(SchedulerService::class.java)
            .setPeriod(60)
            .setFlex(10)
            .setTag(SchedulerService.TAG_TASK_WEATHER_LOG)
            .setPersisted(true)
            .build()
        mGcmNetworkManager?.schedule(periodicTask)
    }

    fun cancelPeriodicTask() {
        mGcmNetworkManager?.cancelTask(SchedulerService.TAG_TASK_WEATHER_LOG, SchedulerService::class.java)
    }
}