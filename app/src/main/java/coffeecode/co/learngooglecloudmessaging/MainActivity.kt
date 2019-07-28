package coffeecode.co.learngooglecloudmessaging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mSchedulerTask: SchedulerTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSchedulerTask = SchedulerTask(this)
        onClick()
    }

    private fun onClick() {
        btnSetScheduler.setOnClickListener {
            mSchedulerTask?.createPeriodicTask()
            Toast.makeText(this, "Periodic Task Created", Toast.LENGTH_SHORT).show()
        }

        btnCancelScheduler.setOnClickListener {
            mSchedulerTask?.cancelPeriodicTask()
            Toast.makeText(this, "Periodic Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
