package studio.uit.vdt.socketsendfile.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import studio.uit.vdt.socketsendfile.adapter.ReceiveAdapter;

/**
 * Created by VDT on 30-Mar-18.
 */

public class ReceiveFilePresenter extends BasePresenter {
    private final static String TAG = ReceiveFilePresenter.class.getSimpleName();
    private RecyclerView.LayoutManager layoutManager;
    private ReceiveAdapter receiveAdapter;
    private ArrayList<String> mData;

    private String myIP;

    public ReceiveFilePresenter(Context context, RecyclerView recyclerView) {
        super(context);
        layoutManager = new LinearLayoutManager(context);
        mData = new ArrayList<>();
        receiveAdapter = new ReceiveAdapter(mData, context);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(receiveAdapter);

    }

    public void getReceivedFiles() {
        File myDir = Environment.getExternalStorageDirectory();
        String FILE_TO_RECEIVED = myDir.getAbsolutePath() + "/";
        File file = new File(FILE_TO_RECEIVED + "SOCKET FILE");
        if (file.exists()) {
            for (File x : file.listFiles()) {

                Date date = new Date(x.lastModified());
                mData.add(x.getName() + ";" + date.toString());

            }
            receiveAdapter.notifyDataSetChanged();
        }

    }


    public void showToast() {
        Toast.makeText(context, "File downloaded", Toast.LENGTH_LONG).show();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(final Message msg) {
            mData.add((String) msg.obj);
            receiveAdapter.notifyDataSetChanged();
        }
    };

    private void getIP() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        myIP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        myIP = myIP.substring(0, myIP.lastIndexOf("."));
        Log.d(TAG, myIP);
    }

    public void codeProcess() {
        getIP();
        for (int i = 1; i <= 254; i++) {
            final int j = i;
            new Thread(new Runnable() { // new thread for
                public void run() {
                    try {
                        String output = myIP + "." + j;
                        processClient(output);
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.d(TAG, e.toString());
                    }
                }
            }).start();
            ;
        }
    }

    private void processClient(String ip) throws Exception {

        File myDir = Environment.getExternalStorageDirectory();
        String FILE_TO_RECEIVED = myDir.getAbsolutePath() + "/";
        File file = new File(FILE_TO_RECEIVED + "SOCKET FILE");
        if (!file.exists()) {
            file.mkdir();
        }
        int i = -1;
        while (i != 0) {
            Socket socket = new Socket(ip, 13267);
            InputStream is = socket.getInputStream();
            DataInputStream d = new DataInputStream(is);

            String data = d.readUTF().trim();
            if (i != -1) {
                i = Integer.parseInt(data.split(";")[1]);
            }

            String name = file.getAbsolutePath() + "/" + data.split(";")[0];
            BufferedInputStream in =
                    new BufferedInputStream(socket.getInputStream());

            BufferedOutputStream out =
                    new BufferedOutputStream(new FileOutputStream(name));

            int len = 0;
            byte[] buffer = new byte[1024 * 50];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.flush();
            out.close();
            socket.close();
            i--;
            Log.d(TAG, "DONE");

            Message message = new Message();
            message.obj = data.split(";")[0] + ";" + new Date().toString();
            handler.handleMessage(message);
        }


    }
}
