package studio.uit.vdt.socketsendfile.Presenter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import studio.uit.vdt.socketsendfile.R;
import studio.uit.vdt.socketsendfile.adapter.ReceiveAdapter;
import studio.uit.vdt.socketsendfile.model.SendModel;

/*
 * Created by VDT on 30-Mar-18.
 */

public class ReceiveFilePresenter extends BasePresenter {
    private final static String TAG = ReceiveFilePresenter.class.getSimpleName();
    private ReceiveAdapter receiveAdapter;
    private ArrayList<SendModel> mData;
    private final static String FOLDER_NAME = "SOCKETFILEs";
    private int count = -1;

    private String myIP;

    public ReceiveFilePresenter(Context context, RecyclerView recyclerView) {
        super(context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        mData = new ArrayList<>();
        receiveAdapter = new ReceiveAdapter(mData, context, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(receiveAdapter);

    }

    public void getReceivedFiles() {

        File myDir = Environment.getExternalStorageDirectory();
        String FILE_TO_RECEIVED = myDir.getAbsolutePath() + "/";
        File file = new File(FILE_TO_RECEIVED + FOLDER_NAME);
        if (file.exists()) {
            for (File x : file.listFiles()) {
                Date date = new Date(x.lastModified());
                SendModel model = new SendModel(0,"", x.getName(), date.toString(), x.getAbsolutePath());
                mData.add(model);

            }
            receiveAdapter.notifyDataSetChanged();
        }

    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(final Message msg) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SendModel receive = (SendModel) msg.obj;
                    progressDialog.setTitle("From " + receive.getIp());
                    progressDialog.setMessage("Received " + receive.getName());
                    mData.add(receive);
                    receiveAdapter.notifyDataSetChanged();
                    Log.d(TAG, "COUNT " + receive.getCount());
                    if (receive.getCount() == 0) {
                        hideProgress();
                        showToast("DONE!");
                    }

                }
            });

        }
    };



    private void getIP() {
        showProgress("Wait...", "Scanning server...");
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        myIP = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
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

        }
    }

    private void processClient(String ip) throws Exception {

        File myDir = Environment.getExternalStorageDirectory();
        String FILE_TO_RECEIVED = myDir.getAbsolutePath() + "/";
        File file = new File(FILE_TO_RECEIVED + FOLDER_NAME);
        if (!file.exists()) {
            file.mkdir();
        }
        while (count != 0) {
            Socket socket = new Socket(ip, 13267);

            InputStream is = socket.getInputStream();
            DataInputStream d = new DataInputStream(is);

            String data = d.readUTF().trim();
            if(count == -1){
                count = Integer.parseInt(data.split(";")[1]);
            }

            count--;
            Log.d(TAG, "count "+ count);
            String name = file.getAbsolutePath() + "/" + data.split(";")[0];
            BufferedInputStream in =
                    new BufferedInputStream(socket.getInputStream());

            BufferedOutputStream out =
                    new BufferedOutputStream(new FileOutputStream(name));

            int len;
            byte[] buffer = new byte[1024 * 50];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            Message message = new Message();
            message.obj = new SendModel(count, ip, data.split(";")[0], new Date().toString(), name);
            handler.handleMessage(message);
            in.close();
            out.flush();
            out.close();
            socket.close();


        }

    }
}
