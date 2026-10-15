package pharmlane.com.PharmLaneStore.utills

import android.annotation.SuppressLint
import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var mBroadcastManager: LocalBroadcastManager

    lateinit var myApplication: AppController

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.e("TAGnoti", "=notificationStore==>" + remoteMessage.data)

        val resultIntent: Intent
        var notificationType: String = remoteMessage.data["title"].toString()
        resultIntent = Intent(this, MainActivity::class.java)

        mBroadcastManager = LocalBroadcastManager.getInstance(this)

        Log.e("TAGnoti", "=notificationStore==>" + remoteMessage.data["title"])
        Log.e("TAGnoti", "=notificationStore==>" + remoteMessage.data["message"])


//        if (notificationType.equals("Live User")) {
//
//           resultIntent = Intent(this, ActivityVideoLive::class.java)
//                   .putExtra(ConstantCodes.INTENT_CHANNEL_ID, remoteMessage.data["channel_id"])
//                   .putExtra(ConstantCodes.INTENT_CHAT_ID, remoteMessage.data["chat_id"])
//                   .putExtra(ConstantCodes.INTENT_CHAT_USERPROFILE, remoteMessage.data["profile_image"])
//                   .putExtra(ConstantCodes.INTENT_CHAT_USERNAME, remoteMessage.data["user_name"])
//                   .putExtra(ConstantCodes.INTENT_ROLE, Constants.CLIENT_ROLE_AUDIENCE)
//                   .putExtra("IsFromDeepLinking", true)
//
//        } else {
//            resultIntent = Intent(this, ActivityMain::class.java)
//        }

        /*api side*/
        showNotificationMethod1(
                remoteMessage.data["title"].toString(),
                remoteMessage.data["message"].toString(),
                resultIntent
        )


//        mBroadcastManager.sendBroadcast(
//            Intent(application.resources.getString(R.string.broadcastIsSubsv))
//        )


        /*general side*/
        // val resultIntent1 = Intent(this, ActivityHome::class.java)
        //showNotificationMethod1(remoteMessage.data["title"].toString(), remoteMessage.data["message"].toString(), resultIntent1)

        Log.e("TAGnoti", "=notification==>" + remoteMessage.data["title"])
        Log.e("TAGnoti", "=notification==>" + remoteMessage.data["massage"])

    }


    @SuppressLint("WrongConstant")
    private fun showNotificationMethod1(title: String, message: String, resultIntent: Intent) {
        val NOTIFICATION_ID = 254
        val CHANNEL_ID = "FAMILYFIRSTPATIENT"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "FAMILYFIRSTPATIENT"
            val Description = "FAMILYFIRSTPATIENT Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(true)
            mChannel.canShowBadge()
            notificationManager.createNotificationChannel(mChannel)
        }
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
        mBuilder.setContentTitle(title)
        mBuilder.setContentText(message)
        mBuilder.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                Notification.DEFAULT_SOUND
        )
        val vibrate = longArrayOf(0, 100, 200, 300)
        mBuilder.setVibrate(vibrate)
        mBuilder.setAutoCancel(true)
        mBuilder.setChannelId(CHANNEL_ID)
        resultIntent.action = Intent.ACTION_MAIN
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val stackBuilder = TaskStackBuilder.create(this)
//        stackBuilder.addParentStack(ActivityStartScreen::class.java)
        stackBuilder.addNextIntent(resultIntent)
        // Android 12+ throws if a PendingIntent does not declare its mutability, which crashed
        // the app every time an order notification arrived.
        val resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

//    private fun showNotificationMethod2(messageBody: String) {
//        val intent = Intent(this, ActivityStartScreen::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//                this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT
//        )
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(getString(R.string.fcm_message))
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//
//        val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                    channelId,
//                    "FamilyFirstAppPatient",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
//    }

    fun isBackgroundRunning(context: Context): Boolean {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        //If your app is the process in foreground, then it's not in running in background
                        return false
                    }
                }
            }
        }
        return true
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }

}