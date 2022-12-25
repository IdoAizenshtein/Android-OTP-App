package otpnew.bizibox.com.biziboxotp;

import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Logs {
    public static void appendLog(String title, String text) {
//        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        File logFile = new File("sdcard/logBizibox.txt");
//        if (!logFile.exists()) {
//            try {
//                logFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
//            buf.append(currentDateTimeString + ":" + title + " - " + text);
//            buf.newLine();
//            buf.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
