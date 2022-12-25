package otpnew.bizibox.com.biziboxotp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
//import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.legacy.content.WakefulBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;

import javax.net.ssl.HttpsURLConnection;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.WorkManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;
import androidx.work.ExistingPeriodicWorkPolicy;

public class AlarmReceiver extends BroadcastReceiver {
    public String indPushOpen = "0";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.i("AlarmReceiver", "onReceive");

        WorkManager workManagerAct = WorkManager.getInstance(MainActivity.inst);
        if (workManagerAct != null) {
            workManagerAct.cancelAllWorkByTag("AlarmReceiver");
        }

        Data data = new Data.Builder()
                .putString("indPushOpen_data", indPushOpen)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workReq =
                new PeriodicWorkRequest.Builder(IntentServiceAlarm.class,
                        6,
                        TimeUnit.HOURS,
                        15,
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag("AlarmReceiver")
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS
                        )
                        .setInputData(data)
                        .build();

        WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                        "AlarmReceiver",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        workReq);
    }
}