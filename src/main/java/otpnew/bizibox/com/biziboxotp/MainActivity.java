package otpnew.bizibox.com.biziboxotp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Set;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import com.google.firebase.messaging.FirebaseMessaging;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.annotation.NonNull;

import android.os.Looper;

import java.net.HttpURLConnection;


import java.security.cert.Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;

import java.io.BufferedInputStream;

import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import 	android.content.pm.PackageInstaller;
public class MainActivity extends AppCompatActivity {
    public static MainActivity inst;
    public static boolean active = false;
    private static final int READ_SMS_AND_PHONE_STATE_PERMISSIONS_REQUEST = 42;
    private boolean mAuthTask = false;
    private EditText mEmailView;
    private TextView mTextError;
    private EditText mPasswordView;
    private EditText phoneView;
    private EditText phone2View;
    private View mProgressView;
    private View mLoginFormView;
    SharedPreferences sharedpreferences;
    Typeface face;
    Typeface faceBold;
    public static final String MyPREFERENCESBizibiox = "MyPrefsBizibox";
    public static final String UsernameBizibox = "UsernameBizibox";
    public static final String PassBizibox = "PassBizibox";
    public static final String getSimNumberBizibox = "getSimNumberBizibox";
    public static final String getLastUpdateDate = "getLastUpdateDate";
    public static final String alertUpdates = "alertUpdates";
    public static final String versionNameStore = "versionNameStore";
    public static final String arrayData = "arrayData";
    public static final String tokenApp = "tokenApp";

    private PendingIntent alarmIntent;
    private AlarmManager alarmMgr;

    public static MainActivity instance() {
        return inst;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build());

        //Log.d("-----", "onCreate");

//        // [START handle_data_extras]
//        if (getIntent().getExtras() != null) {
//            Toast.makeText(this, "1111 are conncted", Toast.LENGTH_SHORT).show();
//            for (String key : getIntent().getExtras().keySet()) {
//                Object value = getIntent().getExtras().get(key);
//                Log.d("fasfsaf", "Key: " + key + " Value: " + value);
//            }
//        }
//        // [END handle_data_extras]

        faceBold = Typeface.createFromAsset(getAssets(), "almoni700.ttf");
        face = Typeface.createFromAsset(getAssets(), "almoni400.ttf");

        askPermission();

        if (isConnected()) {
            //Log.d("-----", "isConnected");

            sharedpreferences = getSharedPreferences(MyPREFERENCESBizibiox, Context.MODE_PRIVATE);
            String getSimNumber = sharedpreferences.getString(getSimNumberBizibox, "null");
            String usernameBiziboxVal = sharedpreferences.getString(UsernameBizibox, "null");
            String passBiziboxVal = sharedpreferences.getString(PassBizibox, "null");

            String versionName = "";
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionName = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(versionNameStore, versionName);
            editor.commit();


            if (getSimNumber.equals("null") || usernameBiziboxVal.equals("null") || passBiziboxVal.equals("null")) {
                setContentView(R.layout.activity_main);

                mLoginFormView = findViewById(R.id.login_form);
                mProgressView = findViewById(R.id.login_progress);
                mEmailView = (EditText) findViewById(R.id.email);
                mPasswordView = (EditText) findViewById(R.id.password);
                phoneView = (EditText) findViewById(R.id.tel);

                mTextError = (TextView) findViewById(R.id.mTextError);
                mTextError.setTypeface(faceBold);
                mTextError.setVisibility(View.GONE);

                mEmailView.setTypeface(face);
                mPasswordView.setTypeface(face);
                phoneView.setTypeface(face);

                TextView logo = (TextView) findViewById(R.id.logo);
                TextView textUnderLogo = (TextView) findViewById(R.id.textUnderLogo);
                TextView mEmailViewText = (TextView) findViewById(R.id.mEmailViewText);
                TextView mPasswordViewText = (TextView) findViewById(R.id.mPasswordViewText);
                TextView phoneViewText = (TextView) findViewById(R.id.phoneViewText);

                logo.setTypeface(faceBold);
                textUnderLogo.setTypeface(face);

                mEmailViewText.setTypeface(faceBold);
                mPasswordViewText.setTypeface(faceBold);
                phoneViewText.setTypeface(faceBold);

                final ScrollView scrollView = (ScrollView) mLoginFormView;

                mPasswordView.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }
                });

                mEmailView.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
                            int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
                            int sy = scrollView.getScrollY();
                            int sh = scrollView.getHeight();
                            int delta = bottom - (sy + sh);
                            scrollView.smoothScrollBy(0, delta);
                        }
                    }
                });

                mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            attemptLogin(textView);
                            return true;
                        }
                        return false;
                    }
                });

                Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
                mEmailSignInButton.setTypeface(faceBold);

                mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin(view);
                    }
                });
            } else {
                startListen();
            }

            Toast.makeText(this, "You are conncted", Toast.LENGTH_SHORT).show();
        } else {
            //Log.d("-----", "isConnected");

            Toast.makeText(this, "You are NOT conncted", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void askPermission(){
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_INHERIT_EXISTING);
        params.setAppPackageName(getPackageName());
        params.setWhitelistedRestrictedPermissions(Collections.singleton("android.permission.RECEIVE_SMS"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                Toast.makeText(this, "אנא אפשר הרשאה לקבלת SMS פועלים עסקים", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 0);
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        inst = this;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private void attemptLogin(View view) {
        //Log.d("-----", "attemptLogin");

        if (mAuthTask) {
            //Log.d("-----", "attemptLogin");

            return;
        }
        showProgress(true);
        mTextError.setVisibility(View.GONE);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        phoneView.setError(null);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String phoneTel = phoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_invalid_user));
            focusView = mEmailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(phoneTel)) {
            phoneView.setError(getString(R.string.error_invalid_tel));
            focusView = phoneView;
            cancel = true;
        } else if (phoneTel.length() < 9) {
            phoneView.setError(getString(R.string.error_invalid_tel_short));
            focusView = phoneView;
            cancel = true;
        } else if (phoneTel.length() == 9 && (!phoneTel.substring(0, 1).equals("5"))) {
            phoneView.setError(getString(R.string.error_invalid_tel_short));
            focusView = phoneView;
            cancel = true;
        } else if (phoneTel.length() == 10 && (!phoneTel.substring(0, 2).equals("05"))) {
            phoneView.setError(getString(R.string.error_invalid_tel_short));
            focusView = phoneView;
            cancel = true;
        }

        if (cancel) {
            showProgress(false);
            focusView.requestFocus();
        } else {
            showProgress(true);
            if (phoneTel.length() == 10) {
                phoneTel = phoneTel.substring(1, 10);
            }
            phoneTel = "972" + phoneTel;
            open(view, email, password, phoneTel);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public boolean SendPostLogin(final String mEmail, final String mPass) {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mEmail, mPass.toCharArray());
                }
            });
            URL url = new URL("https://secure.bizibox.biz/ang/login");
            String param = "inuser_username=" + mEmail + "&inuser_password=" + mPass + "&inbrowser=app&inos=android";
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("POST");
            con.setFixedLengthStreamingMode(param.getBytes().length);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(con.getOutputStream());
            out.print(param);
            out.close();

//            con.connect();


//            try(OutputStream os = con.getOutputStream()) {
//                byte[] input = param.getBytes("utf-8");
//                os.write(input, 0, input.length);
//                os.flush();
//            }
            String response = "";
            if (con.getResponseCode() != 200) {
                response = MainActivity.readStream(con.getErrorStream());
            } else {
                response = MainActivity.readStream(con.getInputStream());
                String err = "\"00000000-0000-0000-0000-000000000000\"";
                if (!err.equals(response)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    public void startListen() {
        //this.startService(new Intent(this, QuickResponseService.class));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        } else {
            goiToInside(true);
        }
    }

    public void getPer(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        } else {
            goiToInside(true);
        }
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    public void getPermissionToReadNumberAndSms() {
//        List<String> permissions = new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
//                Toast.makeText(this, "אנא אפשר הרשאה לקריאת SMS פועלים עסקים", Toast.LENGTH_SHORT).show();
//            }
//            permissions.add(Manifest.permission.READ_SMS);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//            if (shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
//                Toast.makeText(this, "אנא אפשר הרשאה לקבלת SMS פועלים עסקים", Toast.LENGTH_SHORT).show();
//            }
//            permissions.add(Manifest.permission.RECEIVE_SMS);
//        }
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
////                Toast.makeText(this, "אנא אפשר כתיבת קובץ logs", Toast.LENGTH_SHORT).show();
////            }
////            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
////        }
//        String[] tempArray = new String[permissions.size()];
//        if (tempArray.length > 0) {
//            String[] myArray = permissions.toArray(tempArray);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 0);
//            requestPermissions(myArray, READ_SMS_AND_PHONE_STATE_PERMISSIONS_REQUEST);
//        } else {
//            goiToInside(true);
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPer;
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)) {
            isPer = false;
        } else {
            isPer = true;
        }
        goiToInside(isPer);
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "אנא אפשר הרשאה לקריאת SMS פועלים עסקים", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 0);
        } else {
            goiToInside(true);
        }
    }

    public void closeApp(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("האם לסגור או להתנתק?");

        alertDialogBuilder.setPositiveButton("סגור",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        moveTaskToBack(true);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("התנתק", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear().commit();

                ComponentName receiver = new ComponentName(MainActivity.inst, SmsReceiver.class);
                PackageManager pm = MainActivity.inst.getPackageManager();
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

                ComponentName receiverAlarm = new ComponentName(MainActivity.inst, AlarmReceiver.class);
                pm.setComponentEnabledSetting(receiverAlarm,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);


                Intent mainIntent = new Intent(MainActivity.this, SplashActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void saveEdit(View view) {
        boolean cancel = false;
        View focusView = null;
        phone2View.setError(null);
        final String phoneTel2 = phone2View.getText().toString();

        if (TextUtils.isEmpty(phoneTel2)) {
            phone2View.setError(getString(R.string.error_invalid_tel));
            focusView = phone2View;
            cancel = true;
        } else if (phoneTel2.length() < 9) {
            phone2View.setError(getString(R.string.error_invalid_tel_short));
            focusView = phone2View;
            cancel = true;
        } else if (phoneTel2.length() == 9 && (!phoneTel2.substring(0, 1).equals("5"))) {
            phone2View.setError(getString(R.string.error_invalid_tel_short));
            focusView = phone2View;
            cancel = true;
        } else if (phoneTel2.length() == 10 && (!phoneTel2.substring(0, 2).equals("05"))) {
            phone2View.setError(getString(R.string.error_invalid_tel_short));
            focusView = phone2View;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("האם זהו הסלולרי אליו נשלחות ההודעות שיש להעביר למערכת ביזיבוקס?");

            alertDialogBuilder.setPositiveButton("כן",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            String phoneTelFin = phoneTel2;
                            if (phoneTelFin.length() == 10) {
                                phoneTelFin = phoneTelFin.substring(1, 10);
                            }
                            String phoneTelVal = "972" + phoneTelFin;
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(getSimNumberBizibox, phoneTelVal);
                            editor.commit();

                            phone2View.setEnabled(false);

                            Button buttonSave = (Button) findViewById(R.id.buttonSave);
                            buttonSave.setEnabled(false);
                            buttonSave.setBackgroundResource(R.color.colorActiveOpacity);
                            buttonSave.setVisibility(View.GONE);

                            Button buttonEdit = (Button) findViewById(R.id.buttonEdit);
                            buttonEdit.setEnabled(true);
                            buttonEdit.setBackgroundResource(R.color.colorActive);
                            buttonEdit.setVisibility(View.VISIBLE);

                            Toast.makeText(inst, "המספר הוחלף בהצלחה", Toast.LENGTH_SHORT).show();
                        }
                    });

            alertDialogBuilder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String getSimNumber = sharedpreferences.getString(getSimNumberBizibox, "null");
                    if (!getSimNumber.equals("null")) {
                        String tel2 = getSimNumber.substring(3, 12);
                        phone2View.setText(tel2);
                    }
                    View focusView = phone2View;
                    focusView.requestFocus();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void editPhone(View view) {
        phone2View = (EditText) findViewById(R.id.tel2);
        phone2View.setEnabled(true);

        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setEnabled(true);
        buttonSave.setBackgroundResource(R.color.colorActive);
        buttonSave.setVisibility(View.VISIBLE);

        Button buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonEdit.setEnabled(false);
        buttonEdit.setBackgroundResource(R.color.colorActiveOpacity);
        buttonEdit.setVisibility(View.GONE);
    }

    public void goiToInside(final boolean havePer) {
        //Log.i("----TAGGGGG---", "goiToInside");
        ComponentName receiverAlarm = new ComponentName(this, AlarmReceiver.class);
        PackageManager pm1 = this.getPackageManager();
        pm1.setComponentEnabledSetting(receiverAlarm,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

//        Intent intentSMS = new Intent("android.provider.Telephony.SMS_RECEIVED");
//        List<ResolveInfo> infos = getPackageManager().queryBroadcastReceivers(intentSMS, 0);
//        for (ResolveInfo info : infos) {
//            //Logs.appendLog("List Apps SMS_RECEIVED: ", "Receiver name:" + info.activityInfo.name + "; priority=" + info.priority);
//        }

        final Message msg = new Message();

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                boolean isHavePer = (boolean) msg.obj;

                setContentView(R.layout.reminder);

                phone2View = (EditText) findViewById(R.id.tel2);
                phone2View.setEnabled(false);
                phone2View.setTypeface(face);

                TextView titlesFont = (TextView) findViewById(R.id.titles);
                titlesFont.setTypeface(faceBold);
                if (!isHavePer) {
                    titlesFont.setText("אפשר הרשאה לקריאת SMS");
                    titlesFont.setTextColor(Color.parseColor("#f21a1a"));
                    Button buttonPer = (Button) findViewById(R.id.buttonPer);
                    buttonPer.setTypeface(faceBold);
                    buttonPer.setBackgroundResource(R.color.colorActive);
                    buttonPer.setVisibility(View.VISIBLE);
                } else {
                    titlesFont.setText("הינך מחובר בהצלחה");
                    titlesFont.setTextColor(Color.parseColor("#1287aa"));
                    Button buttonPer = (Button) findViewById(R.id.buttonPer);
                    buttonPer.setVisibility(View.GONE);
                }

                TextView text1Font = (TextView) findViewById(R.id.reminder1);
                text1Font.setTypeface(face);
                TextView text2Font = (TextView) findViewById(R.id.reminder2);
                text2Font.setTypeface(face);
                TextView text3Font = (TextView) findViewById(R.id.reminder3);
                text3Font.setTypeface(face);

                TextView dateLastUpdateTextFont = (TextView) findViewById(R.id.dateLastUpdateText);
                dateLastUpdateTextFont.setTypeface(faceBold);

                TextView dateLastUpdate = (TextView) findViewById(R.id.dateLastUpdate);
                dateLastUpdate.setTypeface(face);

                TextView phoneViewText2Font = (TextView) findViewById(R.id.phoneViewText2);
                phoneViewText2Font.setTypeface(faceBold);

                String alertUpdatesExist = sharedpreferences.getString(alertUpdates, "null");
                String getDateLastUpdate = sharedpreferences.getString(getLastUpdateDate, "null");

                if (alertUpdatesExist.equals("null")) {
                    if (!getDateLastUpdate.equals("null")) {
                        dateLastUpdate.setText(getDateLastUpdate);
                    }
                } else {
                    dateLastUpdate.setText(alertUpdatesExist);
                }

                String getSimNumber = sharedpreferences.getString(getSimNumberBizibox, "null");
                if (!getSimNumber.equals("null")) {
                    String tel2 = getSimNumber.substring(3, 12);
                    phone2View.setText(tel2);

                    phone2View.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                ScrollView scrollView = (ScrollView) findViewById(R.id.wrapInside);
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        }
                    });
                }

                Button buttonSave = (Button) findViewById(R.id.buttonSave);
                buttonSave.setTypeface(faceBold);
                buttonSave.setEnabled(false);
                buttonSave.setBackgroundResource(R.color.colorActiveOpacity);
                buttonSave.setVisibility(View.GONE);

                Button buttonEdit = (Button) findViewById(R.id.buttonEdit);
                buttonEdit.setEnabled(true);
                buttonEdit.setTypeface(faceBold);
                buttonEdit.setBackgroundResource(R.color.colorActive);
                buttonEdit.setVisibility(View.VISIBLE);


                Intent intent = new Intent(MainActivity.inst, AlarmReceiver.class);
                sendBroadcast(intent);
            }
        };
        Thread threadRun = new Thread() {
            @Override
            public void run() {
                //Log.i("run", "threadRun");

                boolean runNext = true;
                try {
                    while (runNext) {
                        //Log.i("runNext", runNext + "");
                        if (sharedpreferences.getString(tokenApp, "null").equals("null")) {
                            //Log.i("token", "token is null");
                            Thread.sleep(300);
                        } else if (sharedpreferences.getString(arrayData, "null").equals("null")) {
                            //Log.i("token", "token is not null");
                            //Log.i("arrayData", "arrayData is null");

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(arrayData, "empty");
                            editor.commit();

                            WorkManager workManager = WorkManager.getInstance(MainActivity.inst);
                            if (workManager != null) {
                                workManager.cancelAllWorkByTag("AlarmReceiver");
                            }

                            Data data = new Data.Builder()
                                    .putString("indPushOpen_data", "0")
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
                                    .getInstance(MainActivity.inst)
                                    .enqueue(webhookWorkRequest);


                            Thread.sleep(500);
                        } else if (sharedpreferences.getString(arrayData, "null").equals("empty")) {
                            //Log.i("arrayData", "arrayData is empty");
                            Thread.sleep(2000);
                        } else {
                            //Log.i("arrayData", "arrayData is not empty");
                            runNext = false;
                            ComponentName receiver = new ComponentName(MainActivity.inst, SmsReceiver.class);
                            PackageManager pm = MainActivity.inst.getPackageManager();
                            if (havePer) {
                                pm.setComponentEnabledSetting(receiver,
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                        PackageManager.DONT_KILL_APP);
                            } else {
                                pm.setComponentEnabledSetting(receiver,
                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                        PackageManager.DONT_KILL_APP);
                            }
                            msg.obj = havePer;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (InterruptedException e) {
                    runNext = false;
                }
            }
        };


        if (sharedpreferences.getString(tokenApp, "null").equals("null")) {
            //Log.i("token", "Get token");

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                // Get token
                                // Get new FCM registration token
                                String token = task.getResult();

                                // Log and toast
                                String msgTkn = getString(R.string.msg_token_fmt, token);
                                //Log.d("msgTkn", msgTkn);
//            Toast.makeText(this, msgTkn, Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(tokenApp, token);
                                editor.commit();
                            }

                        }
                    });

            threadRun.start();
        } else {
            if (sharedpreferences.getString(arrayData, "null").equals("null") || sharedpreferences.getString(arrayData, "null").equals("empty")) {
                WorkManager workManager = WorkManager.getInstance(MainActivity.inst);
                if (workManager != null) {
                    workManager.cancelAllWorkByTag("AlarmReceiver");
                }

                Data data = new Data.Builder()
                        .putString("indPushOpen_data", "0")
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
                        .getInstance(MainActivity.inst)
                        .enqueue(webhookWorkRequest);
                threadRun.start();
            } else {
                ComponentName receiver = new ComponentName(this, SmsReceiver.class);
                PackageManager pm = this.getPackageManager();
                if (havePer) {
                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                } else {
                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                }

                msg.obj = havePer;
                handler.sendMessage(msg);
            }
        }


    }


    public void open(View view, final String email, final String password, final String phoneTel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("האם זהו הסלולרי אליו נשלחות ההודעות שיש להעביר למערכת ביזיבוקס?");
        showProgress(false);

        alertDialogBuilder.setPositiveButton("כן",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        showProgress(true);
                        mAuthTask = true;
                        //Log.d("-----", "onClick-true");

                        ExecutorService executorService = Executors.newSingleThreadExecutor();

                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                boolean isValid = SendPostLogin(email, password);
                                System.out.println("Asynchronous task");
                                mAuthTask = false;
                                //Log.d("-----", "run-true");
                                //Log.d("-----", isValid + "");
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (isValid) {
                                            sharedpreferences = getSharedPreferences(MyPREFERENCESBizibiox, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString(UsernameBizibox, email);
                                            editor.putString(PassBizibox, password);
                                            editor.putString(getSimNumberBizibox, phoneTel);

                                            editor.commit();
                                            //showProgress(false);
                                            startListen();
                                        } else {
                                            showProgress(false);
                                            mTextError.setVisibility(View.VISIBLE);
                                            mPasswordView.requestFocus();
                                        }
                                    }
                                });

                            }
                        });

                        executorService.shutdown();


//                        mAuthTask = new UserLoginTask(email, password, phoneTel);
//                        mAuthTask.execute((Void) null);
                    }
                });

        alertDialogBuilder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgress(false);
                View focusView = phoneView;
                focusView.requestFocus();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}



