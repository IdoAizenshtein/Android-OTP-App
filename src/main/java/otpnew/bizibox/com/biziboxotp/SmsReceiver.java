package otpnew.bizibox.com.biziboxotp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class SmsReceiver extends BroadcastReceiver {

    private Context context;
    public static final String SMS_BUNDLE = "pdus";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCESBizibiox = "MyPrefsBizibox";
    public static final String UsernameBizibox = "UsernameBizibox";
    public static final String PassBizibox = "PassBizibox";
    public static final String getSimNumberBizibox = "getSimNumberBizibox";
    public static final String getLastUpdateDate = "getLastUpdateDate";
    public static final String alertUpdates = "alertUpdates";
    public static final String arrayData = "arrayData";


    @Override
    public void onReceive(Context context, Intent intent) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build());

        this.context = context;
        //Log.i("onReceive", "---------onReceive-----");

        Bundle intentExtras = intent.getExtras();
        if (intentExtras == null) {
            return;
        }


        String action = intent.getAction();
        if ("android.provider.Telephony.SMS_RECEIVED".equals(action)) {
            //Logs.appendLog("SMS_RECEIVED", "Get sms");

            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String format = intentExtras.getString("format");
            if (sms != null && sms.length != 0) {
                sharedpreferences = context.getSharedPreferences(MyPREFERENCESBizibiox, Context.MODE_PRIVATE);
                String getArrayData = sharedpreferences.getString(arrayData, "null");
                if (!getArrayData.equals("null")) {
                    boolean existSms = false;
                    for (int i = 0; i < sms.length; ++i) {
                        SmsMessage smsMessage;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                        } else {
                            smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                        }
                        String smsBody = smsMessage.getMessageBody();
                        String address = smsMessage.getOriginatingAddress();
                        try {
                            JSONArray readerArray = new JSONArray(getArrayData);
                            for (int iArr = 0; iArr < readerArray.length(); iArr++) {
                                JSONObject ob = readerArray.getJSONObject(iArr);
                                String PhoneName = ob.getString("PHONE_NAME");
                                String text = ob.getString("TEXT");

                                if (address.equals(PhoneName) && smsBody.contains(text)) {
                                    //Log.i("address", address);
                                    //Log.i("PhoneName", PhoneName);
                                    //Log.i("smsBody", smsBody);
                                    //Log.i("text", text);

                                    //Logs.appendLog("address", address);
                                    //Logs.appendLog("PhoneName", PhoneName);
                                    //Logs.appendLog("smsBody", smsBody);
                                    //Logs.appendLog("text", text);

                                    smsBody = smsBody.replaceAll("[^\\d]", "");
                                    final String getSimNumber = sharedpreferences.getString(getSimNumberBizibox, "null");
                                    final String mEmail = sharedpreferences.getString(UsernameBizibox, "null");
                                    final String mPass = sharedpreferences.getString(PassBizibox, "null");
                                    final String smsBodyFinal = smsBody;
                                    callWebHook(mEmail, mPass, smsBodyFinal, getSimNumber);

                                }
                            }
                        } catch (final JSONException e) {
                            //Logs.appendLog("errorParse", "Json parsing error: " + e.getMessage());
                            //Log.e("errorParse", "Json parsing error: " + e.getMessage());
                        }
                    }
                }
            }
        }

    }

    protected void callWebHook(final String mEmail, final String mPass, String otpPass, String getSimNumber) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putString("mEmail", mEmail)
                .putString("mPass", mPass)
                .putString("otpPass", otpPass)
                .putString("getSimNumber", getSimNumber)
                .build();

        //Log.i("mEmail_data", mEmail);
        //Log.i("mPass_data", mPass);
        //Log.i("otpPass_data", otpPass);
        //Log.i("getSimNumber_data", getSimNumber);

        WorkRequest webhookWorkRequest =
                new OneTimeWorkRequest.Builder(WebHookWorkRequest.class)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS
                        )
                        .setInputData(data)
                        .build();

        WorkManager
                .getInstance(this.context)
                .enqueue(webhookWorkRequest);

    }
}
