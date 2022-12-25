package otpnew.bizibox.com.biziboxotp;


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

public class WebHookWorkRequest extends Worker {
    public final static String mEmail = "";
    public final static String mPass = "";
    public final static String otpPass = "";
    public final static String getSimNumber = "";
    public static final String getLastUpdateDate = "getLastUpdateDate";
    public static final String alertUpdates = "alertUpdates";
    public static final String MyPREFERENCESBizibiox = "MyPrefsBizibox";

    SharedPreferences sharedpreferences;

    public static final int MAX_ATTEMPT = 10;

    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_ERROR = "error";
    public static final String RESULT_RETRY = "error_retry";
    private Context mContext;

    public WebHookWorkRequest(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        //Log.i("WebHookWorkRequest", "doWork");
        if (getRunAttemptCount() > MAX_ATTEMPT) {
            return Result.failure();
        }
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCESBizibiox, Context.MODE_PRIVATE);

        String mEmail_data = getInputData().getString("mEmail");
        String mPass_data = getInputData().getString("mPass");
        String otpPass_data = getInputData().getString("otpPass");
        String getSimNumber_data = getInputData().getString("getSimNumber");
        //Log.i("mEmail_data", mEmail_data);
        //Log.i("mPass_data", mPass_data);
        //Log.i("otpPass_data", otpPass_data);
        //Log.i("getSimNumber_data", getSimNumber_data);


//        existSms = true;
        final long times = new Date().getTime();
        final Message msg = new Message();
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String message = (String) msg.obj;
                if (MainActivity.active) {
                    TextView dateLastUpdate = (TextView) MainActivity.inst.findViewById(R.id.dateLastUpdate);
                    dateLastUpdate.setText(message);
                }
                if (message.contains("לא נמצא")) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                }
            }
        };

        boolean runNext = true;
        try {
            while (((new Date().getTime() - times) < 180000) && runNext) {
                //Logs.appendLog("MyIntentService", "while");

                String result = SendPostJson(mEmail_data, mPass_data, otpPass_data, getSimNumber_data);

                if (result.equals(RESULT_RETRY) || result.equals(RESULT_ERROR)) {
                    //Logs.appendLog("MyIntentService", "while-false");

                    Thread.sleep(10000);
                    if (result.equals(RESULT_RETRY)) {
                        return Result.retry();
                    }

                    if (result.equals(RESULT_ERROR)) {
                        return Result.failure();
                    }
                } else {
                    runNext = false;
                    Date aDate = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
                    String date = formatter.format(aDate);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(getLastUpdateDate, date);

                    //Logs.appendLog("MyIntentService-isSent", result);

                    if (result.equals("1")) {
                        String alertUpd = "מספר הטלפון לא נמצא תואם לזה שרשום בביזיבוקס, במידה והמספר שרשום כאן תקין, עליך להיכנס לממשק ביזיבוקס ולעדכן את המספר";
                        editor.putString(alertUpdates, alertUpd);
                        editor.apply();
                        msg.obj = alertUpd;
                    } else {
                        editor.putString(alertUpdates, "null");
                        editor.apply();
                        msg.obj = date;
                    }
                    handler.sendMessage(msg);


                }
            }
        } catch (InterruptedException e) {
            runNext = false;
            //Logs.appendLog("MyIntentService-InterruptedException", e + "");
        }


        return Result.success();
    }

    public String SendPostJson(final String mEmail, final String mPass, String otpPass, String getSimNumber) {
        String result;
        HttpsURLConnection cons = null;
        try {
            //Log.i("WebHookWorkRequest", "SendPostJson");

            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mEmail, mPass.toCharArray());
                }
            });

            URL url = new URL("https://secure.bizibox.biz/ang/protected/send_otp");
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
            cred.put("sender", getSimNumber);
            cred.put("otpPass", otpPass);
            String param = cred.toString();
            cons.setFixedLengthStreamingMode(param.getBytes().length);

            PrintWriter out = new PrintWriter(cons.getOutputStream());
            out.print(param);
            out.close();
            //Log.i("cons.getResponseCode()", cons.getResponseCode() + "");

            // String response = "";
            if (cons.getResponseCode() != 200) {
                result = RESULT_ERROR;
                //Log.i("RESULT_ERROR", RESULT_ERROR);

                //Logs.appendLog("getResponseCode", cons.getResponseCode() + "");
                // response = MainActivity.readStream(cons.getErrorStream());
            } else {
                //Logs.appendLog("getResponseCode", cons.getResponseCode() + "");
                result = MainActivity.readStream(cons.getInputStream());
                //Log.i("result", result);
            }
        } catch (MalformedURLException e) {
            result = RESULT_ERROR;
            //Log.e("SmsGateway", "MalformedURLException " + e);
            //Log.i("RESULT_ERROR", "MalformedURLException " + e);

        } catch (IOException e) {
            result = RESULT_RETRY;
            //Log.e("SmsGateway", "IOException " + e);
            //Log.i("RESULT_ERROR", "IOException " + e);

        } catch (Exception e) {
            result = RESULT_ERROR;
            //Log.e("SmsGateway", "Exception " + e);
            //Log.i("RESULT_ERROR", "Exception " + e);

        } finally {
            if (cons != null) {
                cons.disconnect();
            }
        }
//        return "false";
        return result;
    }

}
