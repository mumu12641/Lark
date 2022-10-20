package io.github.mumu12641.lark.ui.theme.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.MainActivity
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.service.MediaPlaybackService
import kotlinx.coroutines.launch

object NotificationUtil {

    private const val channelId = "Lark"
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notification: Notification
    private val manager:NotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager


    fun init(mediaSession: MediaSessionCompat){
        this.mediaSession = mediaSession
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(channelId,"Lark",NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableVibration(false)
                vibrationPattern = LongArray(1){0}
                setSound(null,null)
                manager.createNotificationChannel(this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun createNotification(service: MediaPlaybackService,state: Int, song: Song) {
        val notificationBuilder: NotificationCompat.Builder
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata.description
        val clickPendingIntent = PendingIntent.getActivity(
            MainActivity.context, 0,
            Intent(MainActivity.context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        notificationBuilder = NotificationCompat.Builder(MainActivity.context, channelId).apply {

            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setContentIntent(clickPendingIntent)
            setSmallIcon(R.drawable.ic_stat_notification_icon)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSound(null)
            setVibrate(LongArray(1) { 0 })
            setSilent(true)

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_skip_previous_24,
                    "Next",
                    PendingIntent.getBroadcast(
                        MainActivity.context,
                        0,
                        Intent(ACTION_PREVIOUS),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )

            if (state == PlaybackStateCompat.STATE_PLAYING) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_pause_24,
                        "Pause",
                        PendingIntent.getBroadcast(
                            MainActivity.context,
                            0,
                            Intent(ACTION_PAUSE),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )

                )
            } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_play_arrow_24,
                        "Play",
                        PendingIntent.getBroadcast(
                            MainActivity.context,
                            0,
                            Intent(ACTION_PLAY),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                )
            }
            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_skip_next_24,
                    "Next",
                    PendingIntent.getBroadcast(
                        MainActivity.context,
                        0,
                        Intent(ACTION_NEXT),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )

        }
        applicationScope.launch {
            try {
                val bitmap: Bitmap = Glide
                    .with(MainActivity.context)
                    .asBitmap()
                    .load(song.songAlbumFileUri)
                    .centerCrop()
                    .submit()
                    .get()
                notificationBuilder.setLargeIcon(bitmap)
                notificationBuilder.setProgress(0, 0, false)
                notification = notificationBuilder.build()
                service.startForeground(MediaPlaybackService.NOTIFICATION_ID, notification)
            } catch (e: Exception) {
                val bitmap: Bitmap = Glide
                    .with(MainActivity.context)
                    .asBitmap()
                    .load(R.mipmap.new_icon)
                    .submit()
                    .get()
                notificationBuilder.setLargeIcon(bitmap)
                notificationBuilder.setProgress(0, 0, false)
                notification = notificationBuilder.build()
                service.startForeground(MediaPlaybackService.NOTIFICATION_ID, notification)
            }
        }
    }
}