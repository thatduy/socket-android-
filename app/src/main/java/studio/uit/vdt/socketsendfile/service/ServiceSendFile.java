package studio.uit.vdt.socketsendfile.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ASUS on 30-Mar-18.
 */

public class ServiceSendFile extends Service {
    private static final String TAG = "ServiceSendFile";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> a = intent.getStringArrayListExtra("FILENAME");
                ArrayList<String> b = intent.getStringArrayListExtra("FILEPATH");
                Log.d(TAG, a.toString());
                startServer(a,b);
            }
        }).start();

        return START_STICKY;

    }
    public void startServer(ArrayList<String> fileNames, ArrayList<String> filePaths) {
        for (int i = 0; i < fileNames.size(); i++) {
            final int finalI = i;
            ServerSocket socket = null;
            Socket client = null;
            try {

                socket = new ServerSocket(13267);
                client = socket.accept();
                Log.d(TAG, fileNames.size() + " items");
                DataOutputStream d = new DataOutputStream(client.getOutputStream());
                d.writeUTF(fileNames.get(finalI) + ";" + fileNames.size());
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(filePaths.get(finalI)));
                BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
                int len = 0;
                byte[] buffer = new byte[1024 * 50];
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);

                }
                in.close();
                out.flush();
                out.close();

            } catch (Exception e) {
                // TODO: handle exception
                Log.d(TAG, e.toString());
            } finally {
                Log.d(TAG, "CLOSE ALL");
                try {

                    if (socket != null) {
                        socket.close();
                    }

                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, e.toString());
                }

            }

        }
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        stopSelf();
    }
}
