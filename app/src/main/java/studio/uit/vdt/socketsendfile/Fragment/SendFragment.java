package studio.uit.vdt.socketsendfile.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;

import studio.uit.vdt.socketsendfile.Presenter.SendFilePresenter;
import studio.uit.vdt.socketsendfile.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ASUS on 29-Mar-18.
 */

public class SendFragment extends Fragment {

    private Button btn_send_file;
    private SendFilePresenter sendFilePresenter;
    private final static int PICKFILE_REQUEST_CODE = 1122;
    private final static String TAG_SEND_FILE = "SendFragment";


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
        sendFilePresenter = new SendFilePresenter(getContext());

        btn_send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFilePresenter.openFile("*/*");
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

                        File file = new File(uri.getPath());
                        Log.d(TAG_SEND_FILE, file.getAbsolutePath());
                    }
                } else {
                    Uri uri = data.getData();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

}
