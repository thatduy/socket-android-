package studio.uit.vdt.socketsendfile;

import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_CLIENT = "LOG_CLIENT";
    String myIP = "";
    Button btnPress;
    TextView txtLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPress = findViewById(R.id.btnPress);
        txtLog = findViewById(R.id.txtLog);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        myIP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        myIP = myIP.substring(0, myIP.lastIndexOf("."));
        Log.d(TAG_CLIENT, myIP);
        btnPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeProcess();
            }
        });
    }
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(final Message msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtLog.setText((String) msg.obj);
                }
            });
        }
    };
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