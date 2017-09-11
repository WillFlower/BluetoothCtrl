package com.wgh.bluetoothctrl.tools;

import android.os.Environment;

import com.wgh.bluetoothctrl.global.Contants;
import com.wgh.bluetoothctrl.global.MyApplication;
import com.wgh.bluetoothctrl.manager.ThreadManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WGH on 2017/4/15.
 */

public class LogFileHelper {
    private static final int MAX_MB = 1;
    private static boolean mRunningFlag = true;
    private static LogFileHelper logFileHelper = null;
    private static String DIRECTORY_LOGCAT;
    private static File mLogFile1;
    private static File mLogFile2;
    private static LogCatter mLogCatter;

    public static LogFileHelper getInstance() {
        if (logFileHelper == null) {
            logFileHelper = new LogFileHelper();
        }
        return logFileHelper;
    }

    private LogFileHelper() {
        initFileDir();
    }

    private void initFileDir() {
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {// The SDCard directory.
            DIRECTORY_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "bluetoothCtrlLog";
        } else {// The application directory.
            DIRECTORY_LOGCAT = MyApplication.context().getFilesDir().getAbsolutePath()
                    + File.separator + "bluetoothCtrlLog";
        }
        File file = new File(DIRECTORY_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
        mLogFile1 = new File(DIRECTORY_LOGCAT, "bluetoothCtrlLog1.txt");
        mLogFile2 = new File(DIRECTORY_LOGCAT, "bluetoothCtrlLog2.txt");
        ULog.i("DIRECTORY_LOGCAT : " + DIRECTORY_LOGCAT);
    }

    public void startCat() {
        if (mLogCatter == null) {
            mRunningFlag = true;
            mLogCatter = new LogCatter();
            ThreadManager.getInstance().execute(mLogCatter);
            ULog.i("startCat()");
        }
    }

    public void stopCat() {
        if (mLogCatter != null) {
            mRunningFlag = false;
            ThreadManager.getInstance().stop(mLogCatter);
            ULog.i("stopCat()");
        }
    }

    private class LogCatter implements Runnable {

        private FileOutputStream mFileOutputStream;
        private final String mLogCommand;
        private Process logcatterProcess;
        private BufferedReader mBufferedReader;

        LogCatter() {
            if (1 == Contants.logFileIndex()) {
                try {
                    mFileOutputStream = new FileOutputStream(mLogFile1, false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mFileOutputStream = new FileOutputStream(mLogFile2, false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            mLogCommand = "logcat *:e *:i | grep \"(" +
                    String.valueOf(MyApplication.getmPId()) + ")\"";
        }

        @Override
        public void run() {
            try {
                logcatterProcess = Runtime.getRuntime().exec(mLogCommand);
                mBufferedReader = new BufferedReader(
                        new InputStreamReader(logcatterProcess.getInputStream()), 1024);
                String oneLine = null;
                while (mRunningFlag && (oneLine = mBufferedReader.readLine()) != null) {
                    if (0 == oneLine.length()) {
                        continue;
                    }
                    if (isFileOverMax(Contants.logFileIndex())) {
                        if (1 == Contants.logFileIndex()) {
                            mFileOutputStream = new FileOutputStream(mLogFile2, false);
                            Contants.logFileIndex(2);
                        } else {
                            mFileOutputStream = new FileOutputStream(mLogFile1, false);
                            Contants.logFileIndex(1);
                        }
                    }
                    if (mFileOutputStream != null &&
                            oneLine.contains(String.valueOf(MyApplication.getmPId()))) {
                        mFileOutputStream.write((getCurrentTime() + "  " + oneLine + "\n").getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mBufferedReader != null ) {
                    try {
                        mBufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mFileOutputStream != null) {
                    try {
                        mFileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (logcatterProcess != null) {
                    logcatterProcess.destroy();
                }
            }
        }
    }

    private boolean isFileOverMax(int index) {
        File file  = null;
        String size = "";
        long sizeKB = 0;
        long sizeMB = 0;
        long sizeGB = 0;

        if (1 == index) {
            file = mLogFile1;
        } else {
            file = mLogFile2;
        }

        long sizeBT = file.length();
        DecimalFormat df = new DecimalFormat("#.00");
        if (sizeBT < 1024) {
            size = df.format((double) sizeBT) + "BT";
        } else if (sizeBT < 1048576) {
            sizeKB = (long) (sizeBT / 1024);
            size = df.format((double) sizeBT / 1024) + "KB";
        } else if (sizeBT < 1073741824) {
            sizeMB = (long) (sizeBT / 1048576);
            size = df.format((double) sizeBT / 1048576) + "MB";
        } else {
            sizeGB = (long) (sizeBT / 1073741824);
            size = df.format((double) sizeBT / 1073741824) +"GB";
        }
//        ULog.i("LogFile :　" + file.getName() + "  Size : " + size);
        if (sizeMB >= MAX_MB) {
            ULog.w("LogFile : " + file.getName() + "  Size : " + size);
            return true;
        } else {
            return false;
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));// 2012-10-03 23:41:31
    }
}
