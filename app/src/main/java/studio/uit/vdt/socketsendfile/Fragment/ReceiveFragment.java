package studio.uit.vdt.socketsendfile.Fragment;

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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import studio.uit.vdt.socketsendfile.Presenter.ReceiveFilePresenter;
import studio.uit.vdt.socketsendfile.R;

/**
 * Created by ASUS on 29-Mar-18.
 */

public class ReceiveFragment extends Fragment {
    private Button btnPress;
    private ReceiveFilePresenter filePresenter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.receive_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPress = view.findViewById(R.id.btnPress);
        RecyclerView mRecyclerView = view.findViewById(R.id.my_recycler_view);
        filePresenter = new ReceiveFilePresenter(getContext(), mRecyclerView);
        filePresenter.getReceivedFiles();
        btnPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePresenter.codeProcess();
            }
        });


    }




}
