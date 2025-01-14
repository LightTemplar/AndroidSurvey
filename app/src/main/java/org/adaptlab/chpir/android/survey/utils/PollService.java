package org.adaptlab.chpir.android.survey.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import org.adaptlab.chpir.android.survey.tasks.EntityUploadTask;

public class PollService extends IntentService {
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";
    private static final String TAG = "PollService";

    public PollService() {
        super(TAG);
    }

    // Control polling of api, set isOn to true to enable polling
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            int DEFAULT_POLL_INTERVAL = 1000 * 24 * 60 * 60;
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), DEFAULT_POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PollService.PREF_IS_ALARM_ON, isOn).apply();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (NotificationUtils.checkForNetworkErrors(getApplicationContext())) {
            new EntityUploadTask().execute();
        }
    }

}