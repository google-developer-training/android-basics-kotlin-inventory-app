package com.example.inventory.workers

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.inventory.MainActivity
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.data.ItemRoomDatabase
import com.example.inventory.data.getDaysToExpiry
import kotlinx.coroutines.flow.first

class NotificationWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        Log.d("worker", "running task")
        val items: List<Item> = ItemRoomDatabase.getDatabase(applicationContext).itemDao().getItems().first()
        items.forEach { item:Item ->
            if (item.getDaysToExpiry() <= 1) {
                Log.d("worker", "${item.name} expires in ${item.getDaysToExpiry()} days")
                val builder = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(applicationContext.getString(R.string.expiring_soon))
                    .setContentText(applicationContext.getString(R.string.expiration_message, item.name, item.getDaysToExpiry()))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(applicationContext)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(item.id, builder.build())
                }
            }
        }

        Log.d("worker", "finished checking")
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}