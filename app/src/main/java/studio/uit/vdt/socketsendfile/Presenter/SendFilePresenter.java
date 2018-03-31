package studio.uit.vdt.socketsendfile.Presenter;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import studio.uit.vdt.socketsendfile.ServiceSendFile;
import studio.uit.vdt.socketsendfile.adapter.ReceiveAdapter;

/*
 * Created by ASUS on 30-Mar-18.
 */

public class SendFilePresenter extends BasePresenter {
    private final static String TAG = "SendFilePresenter";
    private final static int PICKFILE_REQUEST_CODE = 1122;
    private RecyclerView recyclerView;
    private ReceiveAdapter receiveAdapter;
    private ArrayList<String> filePaths;
    private ArrayList<String> fileNames;
    private Fragment activity;
    ServerSocket socket;
    Socket client;

    public SendFilePresenter(Context context, Fragment fragment, ArrayList<String> filePaths,
                             ArrayList<String> fileNames, RecyclerView recyclerView) {
        super(context);
        this.filePaths = filePaths;
        this.fileNames = fileNames;
        this.activity = fragment;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        receiveAdapter = new ReceiveAdapter(filePaths, fileNames, context, true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(receiveAdapter);
    }

    public void updateList() {
        receiveAdapter.notifyDataSetChanged();
    }


    public void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
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
        } catch (android.content.ActivityNotFoundException ex) {
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

    public void startServer(){
//        Intent intent = new Intent(context, ServiceSendFile.class);
//        intent.putStringArrayListExtra("FILENAME", fileNames);
//        intent.putStringArrayListExtra("FILEPATH", filePaths);
//
//        context.startService(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                startServer2();
            }
        }).start();
    }
    public void startServer2() {
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

        }}
}



