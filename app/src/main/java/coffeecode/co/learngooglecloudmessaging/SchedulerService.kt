package coffeecode.co.learngooglecloudmessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.SyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.DecimalFormat


class SchedulerService : GcmTaskService(){

    companion object{
        val TAG = SchedulerService::class.java.simpleName
        const val APP_ID = "a6d3678de683ec9de8944b08ff50e349"
        const val CITY = "Bandung"
        var TAG_TASK_WEATHER_LOG = "WeatherTask"

        const val CHANNEL_ID = "Channel_1"
        const val CHANNEL_NAME = "Job service channel"
    }

    override fun onRunTask(taskParams: TaskParams?): Int {
        var result = 0
        if (taskParams?.tag.equals(TAG_TASK_WEATHER_LOG)) {
            getCurrentWeather()
            result = GcmNetworkManager.RESULT_SUCCESS
        }
        return result
    }

    override fun onInitializeTasks() {
        super.onInitializeTasks()
        val mSchedulerTask = SchedulerTask(this)
        mSchedulerTask.createPeriodicTask()
    }

    private fun getCurrentWeather() {
        Log.d("GetWeather", "Running")
        val client = SyncHttpClient()
        val url = "http://api.openweathermap.org/data/2.5/weather?q=$CITY&appid=$APP_ID"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }
                Log.d("GetWeather", "response body : $responseBody")
                try {
                    Log.d(TAG, result)
                    val responseObject = JSONObject(result)
                    val currentWeather = responseObject.getJSONArray("weather").getJSONObject(0).getString("main")
                    val description = responseObject.getJSONArray("weather").getJSONObject(0).getString("description")
                    val tempInKelvin = responseObject.getJSONObject("main").getDouble("temp")
                    val tempInCelsius = tempInKelvin - 273
                    val temperature = DecimalFormat("##.##").format(tempInCelsius)
                    val title = "Current Weather"
                    val message = "$currentWeather, $description with $temperature celcius"
                    val notifyId = 100
                    showNotification(applicationContext, title, message, notifyId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("GetWeather", "Failed")
            }

        })
    }

    private fun showNotification(context: Context, title: String, message: String, notifId: Int) {

        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_replay_black_24dp)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.black))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }
}