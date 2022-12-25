package otpnew.bizibox.com.biziboxotp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class IntentServiceAlarm extends Worker {
    public Context context;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCESBizibiox = "MyPrefsBizibox";
    public static final String UsernameBizibox = "UsernameBizibox";
    public static final String PassBizibox = "PassBizibox";
    public static final String getSimNumberBizibox = "getSimNumberBizibox";
    public static final String arrayData = "arrayData";
    public static final String versionNameStore = "versionNameStore";
    public static final String tokenApp = "tokenApp";
    public PendingIntent alarmIntent;
    public AlarmManager alarmMgr;
    public String indPushOpen = "0";
    public final static String indPushOpen_data = "0";

    public static final int MAX_ATTEMPT = 10;

    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_ERROR = "error";
    public static final String RESULT_RETRY = "error_retry";

    public IntentServiceAlarm(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build());
        //Log.i("-IntentServiceAlarm", "IntentServiceAlarm");
    }


    public String SendMonitorPostJson(final String mEmail, final String mPass, String versionName, String getSimNumber, String token) {
        String response;
        HttpsURLConnection cons = null;
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mEmail, mPass.toCharArray());
                }
            });
             //Log.i("-----", "SendMonitorPostJson");

            URL url = new URL("https://secure.bizibox.biz/ang/protected/set_otpapps");
            cons = (HttpsURLConnection) url.openConnection();
            cons.setDoOutput(true);
            cons.setDoInput(true);
            cons.setReadTimeout(10000);
            cons.setConnectTimeout(15000);
            cons.setInstanceFollowRedirects(false);
            cons.setRequestMethod("POST");
            cons.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            cons.setRequestProperty("Authorization", "Basic " + new String(Base64.encode((mEmail + ":" + mPass).getBytes(), Base64.NO_WRAP)));
            cons.setRequestProperty("HTML_LOGIN", "true");
            JSONObject cred = new JSONObject();
            cred.put("phoneNumber", getSimNumber);
            cred.put("verNum", versionName);
            cred.put("appToken", token);
            cred.put("indPushOpen", indPushOpen);
            String param = cred.toString();
            //Logs.appendLog("JSONObject", param);
            cons.setFixedLengthStreamingMode(param.getBytes().length);

            PrintWriter out = new PrintWriter(cons.getOutputStream());
            out.print(param);
            out.close();

            if (cons.getResponseCode() != 200) {
                response = RESULT_ERROR;
                //Logs.appendLog("getResponseCode", cons.getResponseCode() + "");
                // response = MainActivity.readStream(cons.getErrorStream());
            } else {
                //Logs.appendLog("getResponseCode", cons.getResponseCode() + "");
                response = MainActivity.readStream(cons.getInputStream());
            }
        } catch (MalformedURLException e) {
            response = RESULT_ERROR;
            //Log.e("SmsGateway", "MalformedURLException " + e);
        } catch (IOException e) {
            response = RESULT_RETRY;
            //Log.e("SmsGateway", "Exception " + e);
        } catch (Exception e) {
            response = RESULT_ERROR;
            //Log.e("SmsGateway", "Exception " + e);
        } finally {
            if (cons != null) {
                cons.disconnect();
            }
        }
        return response;
    }

    private void completeWakefulIntentAlarmReceiver() {
        //Logs.appendLog("completeWakefulIntentAlarmReceiver", "complete");
        // Log.i("-----", indPushOpen);
        if (!indPushOpen.equals("0")) {
            if (MainActivity.active) {
            } else {
                Intent mainIntent = new Intent(context, SplashActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainIntent);
            }
        }
    }


    @NonNull
    @Override
    public Result doWork() {
        //Log.i("-IntentServiceAlarm", "doWork");
        if (getRunAttemptCount() > MAX_ATTEMPT) {
            return Result.failure();
        }

        indPushOpen = getInputData().getString("indPushOpen_data");

//        context = getApplicationContext();

        sharedpreferences = context.getSharedPreferences(MyPREFERENCESBizibiox, Context.MODE_PRIVATE);
        final String getSimNumber = sharedpreferences.getString(getSimNumberBizibox, "null");
        final String mEmail = sharedpreferences.getString(UsernameBizibox, "null");
        final String mPass = sharedpreferences.getString(PassBizibox, "null");
        final String versionName = sharedpreferences.getString(versionNameStore, "null");
        final String token = sharedpreferences.getString(tokenApp, "null");


        if (!getSimNumber.equals("null") && !mEmail.equals("null") && !mPass.equals("null") && !versionName.equals("null") && !token.equals("null")) {
            //Log.i("-IntentServiceAlarm", "not");

            final long times = new Date().getTime();
            boolean runNext = true;
            try {
                //Log.i("-IntentServiceAlarm", "while");

                while (((new Date().getTime() - times) < 180000) && runNext) {
                    //Log.i("token", token);
                    String isSent = SendMonitorPostJson(mEmail, mPass, versionName, getSimNumber, token);
                    //Logs.appendLog("IntentServiceAlarm", "while");
                    //Log.i("-isSent", "isSent" + isSent);

                    if (isSent.equals(RESULT_RETRY) || isSent.equals(RESULT_ERROR)) {
                        //Logs.appendLog("IntentServiceAlarm", "while-false");

                        Thread.sleep(10000);
                    } else {
                        //Logs.appendLog("IntentAlarm_isSent", isSent);

                        runNext = false;
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(arrayData, isSent);
                        editor.apply();
                        completeWakefulIntentAlarmReceiver();
                    }
                }
            } catch (InterruptedException e) {
                runNext = false;
                //Logs.appendLog("IntentAlarm_InterruptedException", e + "");
                completeWakefulIntentAlarmReceiver();
            }
        } else {
            //Logs.appendLog("IntentServiceAlarm", "completeWakefulIntent");
            completeWakefulIntentAlarmReceiver();
        }

        return Result.success();
    }
}
