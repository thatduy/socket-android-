package studio.uit.vdt.socketsendfile.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import studio.uit.vdt.socketsendfile.R;

/**
 * Created by ASUS on 29-Mar-18.
 */

public class SendFragment extends Fragment {

    Button btn_send_file;
    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return v = inflater.inflate(R.layout.send_fragment, container, false);
    }


    @Override
    public View getView() {
        return  v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_send_file = view.findViewById(R.id.btn_send_file);

        //Toolbar toolbar = view.findViewById(R.id.toolbar);
       // AppCompatActivity activity = (AppCompatActivity) getActivity();
       // activity.setSupportActionBar(toolbar);
       // activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_send_file.setText("CHANGE");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

}
