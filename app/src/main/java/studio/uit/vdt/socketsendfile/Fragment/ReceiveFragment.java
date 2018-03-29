package studio.uit.vdt.socketsendfile.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

import studio.uit.vdt.socketsendfile.R;

/**
 * Created by ASUS on 29-Mar-18.
 */

public class ReceiveFragment extends Fragment {
    Button btnPress;
    TextView txtLog;
    Toolbar toolbar;
    private static final String TAG_CLIENT = "LOG_CLIENT";
    String myIP = "";
    View v;
    ProgressDialog mDialog ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return v = inflater.inflate(R.layout.receive_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPress = view.findViewById(R.id.btnPress);
        txtLog = view.findViewById(R.id.txtLog);

        btnPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = new ProgressDialog(getContext());
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                getIP();
                codeProcess();
            }
        });


    }

    @Nullable
    @Override
    public View getView() {
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(final Message msg) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtLog.setText((String) msg.obj);
                    mDialog.cancel();
                }
            });

        }
    };
    public void showToast(){
        Toast.makeText(getContext(), "File downloaded",Toast.LENGTH_LONG).show();
    }
    private void getIP() {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        myIP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        myIP = myIP.substring(0, myIP.lastIndexOf("."));
        Log.d(TAG_CLIENT, myIP);


    }
    public void codeProcess(){
        for (int i = 1; i <= 254; i++) {
            final int j = i;
            new Thread(new Runnable() { // new thread for
                public void run() {
                    try {
                        String output = myIP+"."+j;

                        processClient(output);

                    }catch (Exception e) {
                        // TODO: handle exception
                        Log.d(TAG_CLIENT, e.toString());
                    }
                }
            }).start();;
        }
    }

    public void processClient(String ip) throws Exception{
        File myDir = Environment.getExternalStorageDirectory();
        String FILE_TO_RECEIVED = myDir.getAbsolutePath() + "/";
        File file = new File(FILE_TO_RECEIVED + "SOCKET FILE");
        if (!file.exists()){
            file.mkdir();
        }
        Socket socket = new Socket(ip, 13267);
        InputStream is = socket.getInputStream();
        DataInputStream d = new DataInputStream(is);
        // GlobalVar.log += ip + " is connected \n";
        String data = d.readUTF().trim();
        String name = file.getAbsolutePath() + "/"+data;
        BufferedInputStream in =
                new BufferedInputStream(socket.getInputStream());

        BufferedOutputStream out =
                new BufferedOutputStream(new FileOutputStream(name));

        int len = 0;
        byte[] buffer = new byte[1024*50];
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.flush();
        out.close();
        socket.close();
        // GlobalVar.log += "RECEIVED at " + name + "\n";
        //  GlobalVar.log+="RECEIVED successfully \n";
        //  System.out.println("\nDone!");
        Log.d(TAG_CLIENT, "DONE");

        Message message = new Message();
        message.obj = "Connected to "+ip + "\nReceiving file " + data + "\nSaving at " + name + "\n" + "Receive successfully!";
        handler.handleMessage(message);

    }
}
