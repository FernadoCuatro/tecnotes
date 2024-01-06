package com.fmcuatro.agenda_online;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.fmcuatro.agenda_online.AgregarNota.AgregarNota;

public class AlarmNotification extends Worker {

    public AlarmNotification(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String texto = getInputData().getString("texto");
        String descripcion = getInputData().getString("descripcion");
        int notificationId = getInputData().getInt("notificationId", 0);

        createSimpleNotification(getApplicationContext(), texto, descripcion, notificationId);

        return Result.success();
    }

    private void createSimpleNotification(Context context, String texto, String descripcion, int notificationId) {
        Intent intent = new Intent(context, MenuPrincipal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AgregarNota.MY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.icono_agenda)
                .setContentTitle("¡Un evento está cerca!")
                .setContentText("¡" + texto.substring(0, 1).toUpperCase() + texto.substring(1) + " está cerca!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(descripcion))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }
}
