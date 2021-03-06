package io.cloudwalk.loglibrary;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Locale.US;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class Sniffer {
    private static final String
            TAG = Sniffer.class.getSimpleName();

    private static final Semaphore
            sSnifferSemaphore = new Semaphore(1, true);

    private static String
            sPath = null;

    private static void _clear() {
        android.util.Log.d(TAG, "_clear");

        File dir = Application.getContext().getExternalFilesDir("Log");

        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                Calendar[] calendar = { Calendar.getInstance(), Calendar.getInstance() };

                Date lastModifiedDate = new Date(file.lastModified());

                calendar[0].setTime(lastModifiedDate);
                calendar[1].add    (Calendar.DAY_OF_YEAR, -7);

                if (calendar[0].getTimeInMillis() < calendar[1].getTimeInMillis()) {
                    file.delete();
                }
            }
        }
    }

    private Sniffer() {
        Log.d(TAG, "Sniffer");

        /* Nothing to do */
    }

    public static void write(String tag, String msg, Throwable tr) {
        if (msg == null) { msg = ""; }

        StringWriter sw = new StringWriter();
        PrintWriter  pw = new PrintWriter(sw);

        tr.printStackTrace(pw);

        String trace = sw.toString();

        if (!msg.isEmpty() && !trace.isEmpty()) { msg += "\r\n"; }

        write(tag, msg + trace);
    }

    public static void write(String tag, String msg) {
        Semaphore semaphore = new Semaphore(0, true);

        new Thread() {
            @Override
            public void run() {
                super.run();

                semaphore.release();

                try {
                    sSnifferSemaphore.acquireUninterruptibly();

                    StringBuilder   trace     = new StringBuilder();
                    String          timestamp = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", US).format(Calendar.getInstance().getTime());

                    for (String slice : msg.split("\n")) {
                        trace.append(timestamp).append(" ").append(tag).append(": ").append(slice).append("\r\n");
                    }

                    do {
                        if (sPath != null) {
                            File file = new File(sPath);

                            file.getParentFile().mkdirs();

                            if ((file.length() + trace.length()) < 128000) { break; }
                        }

                        String root = Application
                                .getContext()
                                .getExternalFilesDir("Log")
                                .getAbsolutePath();

                        String name = new SimpleDateFormat("yyMMddHHmmss", US).format(Calendar.getInstance().getTime());

                        sPath = root + "/" + name + ".log";

                        if (!(new File(sPath)).exists()) { _clear(); continue; }

                        sPath = null;
                    } while (true);

                    FileOutputStream   fos = new FileOutputStream  (sPath, true);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, ISO_8859_1);

                    osw.append(trace.toString().replaceAll("[^\n,\r, -~]", "_"));
                    osw.close ();
                } catch (Exception exception) {
                    android.util.Log.e(TAG, android.util.Log.getStackTraceString(exception));
                } finally {
                    sSnifferSemaphore.release();
                }
            }
        }.start();

        semaphore.acquireUninterruptibly();
    }

    public static File[] export() {
        File[] list;

        try {
            sSnifferSemaphore.acquireUninterruptibly();

            sPath = null;

            list = Application.getContext().getExternalFilesDir("Log").listFiles();
        } catch (Exception exception) {
            android.util.Log.e(TAG, android.util.Log.getStackTraceString(exception));

            list = null;
        } finally {
            sSnifferSemaphore.release();
        }

        return list;
    }
}
