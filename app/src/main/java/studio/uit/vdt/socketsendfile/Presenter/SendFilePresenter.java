package studio.uit.vdt.socketsendfile.Presenter;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import studio.uit.vdt.socketsendfile.adapter.ReceiveAdapter;
import studio.uit.vdt.socketsendfile.adapter.SendCompleteListener;
import studio.uit.vdt.socketsendfile.model.SendModel;

/*
 * Created by ASUS on 30-Mar-18.
 */

public class SendFilePresenter extends BasePresenter {
    private final static String TAG = "SendFilePresenter";
    private final static int PICKFILE_REQUEST_CODE = 1122;
    //private RecyclerView recyclerView;
    private ReceiveAdapter receiveAdapter;
    private ArrayList<SendModel> models;
    private Fragment activity;
    private ServerSocket socket;
    private Socket client;
    private SendCompleteListener listener;

    public SendFilePresenter(Context context, Fragment fragment,
                             ArrayList<SendModel> models, RecyclerView recyclerView, SendCompleteListener listener) {
        super(context);

        this.models = models;
        this.activity = fragment;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        receiveAdapter = new ReceiveAdapter(models, context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(receiveAdapter);
        this.listener = listener;
    }

    public void updateList() {
        receiveAdapter.notifyDataSetChanged();
    }


    public void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
            // Do something for lollipop and above versions
            //intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/* video/*");

        } else{
            // do something for phones running an SDK before lollipop
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
        }


        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA_MULTIPLE");
        // if you want any file type, you can skip next line
        sIntent.putExtra("CONTENT_TYPE", "*/*");
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (context.getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }
        try {
            activity.startActivityForResult(chooserIntent, PICKFILE_REQUEST_CODE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(final Message msg) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SendModel a = (SendModel)msg.obj;
                    progressDialog.setTitle("Sending to " + client);
                    progressDialog.setMessage("Sent " + a.getName());
                    //models.remove(a);
                    //Log.d(TAG, "INDEX " + a);receiveAdapter.getItemCount();


                }
            });
        }
    };

    public void startServer() throws Exception{
            showProgress("Wait....", "Scanning client...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                        try {
                            socket = new ServerSocket(13267);
                            startServer2();
                            }

                        catch (Exception e){
                            Log.d(TAG, e.toString());
                        }
            }}).start();


    }
    public void stopSocket() throws Exception{
        if(socket != null){
            socket.close();
        }
        if(client != null){
            client.close();
        }
    }
    private void startServer2() {
            try {
                for (SendModel file : models) {
                    client = socket.accept();
                    Log.d(TAG, models.size() + " items");
                    DataOutputStream d = new DataOutputStream(client.getOutputStream());
                    d.writeUTF(file.getName() + ";" + models.size());
                    BufferedInputStream in = new BufferedInputStream(
                            new FileInputStream(file.getPath()));
                    BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
                    int len;
                    byte[] buffer = new byte[1024 * 50];
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                    d.close();
                    in.close();
                    out.close();

                    Message message = new Message();
                    message.obj = file;
                    handler.handleMessage(message);
                }

            } catch (Exception e) {
                // TODO: handle exception
                Log.d(TAG, e.toString());

            } finally {

                updateRemove();
                try {
                    socket.close();
                    client.close();
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }

            }

        }
        private void updateRemove(){

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.callback(true);
                    models.clear();
                    receiveAdapter.notifyDataSetChanged();
                    hideProgress();
                    showToast("DONE!");



                }
            });
        }
}



