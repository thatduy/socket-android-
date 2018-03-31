package studio.uit.vdt.socketsendfile.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import studio.uit.vdt.socketsendfile.Presenter.SendFilePresenter;
import studio.uit.vdt.socketsendfile.R;
import studio.uit.vdt.socketsendfile.model.SendModel;

import static android.app.Activity.RESULT_OK;

/*
 * Created by ASUS on 29-Mar-18.
 */

public class SendFragment extends Fragment {

    private Button btn_send_file;
    private SendFilePresenter sendFilePresenter;
    private final static int PICKFILE_REQUEST_CODE = 1122;
    private final static String TAG_SEND_FILE = "SendFragment";
    private ArrayList<SendModel> sendModels = new ArrayList<>();
    private boolean isSelectedFile = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.send_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_send_file = view.findViewById(R.id.btn_send_file);
        RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view);
        sendFilePresenter = new SendFilePresenter(getContext(), SendFragment.this,
                sendModels, recyclerView);

        btn_send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSelectedFile){
                    try {
                        sendFilePresenter.startServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    sendFilePresenter.openFile();
                }

            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK){
            if(null != data) { // checking empty selection
                if(null != data.getClipData()) { // checking multiple selection or not
                    for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        File file = new File(sendFilePresenter.getRealPathFromURI(uri));
                        Log.d(TAG_SEND_FILE, file.getAbsolutePath());
                        SendModel model = new SendModel(0,"", file.getName(),
                                new Date(file.lastModified()).toString(), file.getAbsolutePath());

                        sendModels.add(model);
                    }
                } else {
                    Uri uri = data.getData();
                    File file = new File(sendFilePresenter.getRealPathFromURI(uri));
                    Log.d(TAG_SEND_FILE, file.getAbsolutePath());
                    SendModel model = new SendModel(0,"", file.getName(),
                            new Date(file.lastModified()).toString(), file.getAbsolutePath());

                    sendModels.add(model);
                }
                isSelectedFile = true;
                btn_send_file.setText(getString(R.string.send_file));
                sendFilePresenter.updateList();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            sendFilePresenter.stopSocket();
        } catch (Exception e) {
            Log.d(TAG_SEND_FILE, e.toString());
        }
    }
}
