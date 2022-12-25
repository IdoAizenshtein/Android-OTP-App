package otpnew.bizibox.com.biziboxotp;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.WorkManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseDataReceiver extends FirebaseMessagingService {
    public String indPushOpen = "0";

    private static final String TAG = "FirebaseDataReceiver";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (true) {
                scheduleJob();
            } else {

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


    }


    private void scheduleJob() {
        indPushOpen = "1";

        Data data = new Data.Builder()
                .putString("indPushOpen_data", indPushOpen)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        WorkRequest webhookWorkRequest =
                new OneTimeWorkRequest.Builder(IntentServiceAlarm.class)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS
                        )
                        .setInputData(data)
                        .build();

        WorkManager
                .getInstance(this)
                .enqueue(webhookWorkRequest);
    }


}