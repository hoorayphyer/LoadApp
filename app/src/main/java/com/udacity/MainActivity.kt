package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedRadioID: Int = 0

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent // for the following
    private lateinit var action: NotificationCompat.Action // this is the action to open DetailActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val radioGroup = binding.includedContent.radioGroup
        binding.includedContent.customButton.setOnClickListener {
            selectedRadioID = radioGroup.checkedRadioButtonId
            if (selectedRadioID == -1) {
                Toast.makeText(
                    applicationContext,
                    "Please select a file to download",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val url = when (selectedRadioID) {
                R.id.glide_button -> GlideURL
                R.id.loadapp_button -> LoadAppURL
                R.id.retrofit_button -> RetrofitURL
                else -> ""
            }
            download(url)
        }

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()



    }

    // this receiver receives broadcast sent by DownloadManager.
    // In onCreate(), the IntentFilter is set at ACTION_DOWNLOAD_COMPLETE, so the broadcast is sent when download is finished
    // the download() method initiates the download.
    // What this receiver's onReceive does is send notification with a different intent that opens the DetailActivity
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val status = getStatus(id)
            notificationManager.sendNotification("Download completed", applicationContext, status)
        }

        private fun getStatus( id : Long) : Int {
            val query = DownloadManager.Query().setFilterById(id)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            // cursor points to the data set returned by this database query
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) { // move cursor to the first row of the data set
                return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }
            return DownloadManager.STATUS_FAILED
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GlideURL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LoadAppURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RetrofitURL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "LoadAppChannelId"
    }

    private fun NotificationManager.sendNotification(
        messageBody: String,
        applicationContext: Context,
        status: Int
    ) {
        val notificationId = 0
        val intent = Intent(applicationContext, DetailActivity::class.java).apply {
            action=ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("com.udacity.EXTRA_STATUS", status)
            putExtra("com.udacity.EXTRA_RADIO_ID", selectedRadioID)
        }
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
       // action = NotificationCompat.Action.Builder(R.drawable.ic_assistant_black_24dp, "check for status", pendingIntent).build()

        val builder = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle("LoadAppTitle")
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
         //   .addAction(action)

        // notificationId is a unique int for each notification that you must define
        notify(notificationId, builder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }
}