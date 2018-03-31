package studio.uit.vdt.socketsendfile.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import studio.uit.vdt.socketsendfile.R;

/*
 * Created by ASUS on 29-Mar-18.
 */


public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    private ArrayList<String> filePaths;
    private Context context;
    private boolean isSend = false;
    private final static String TAG = "ReceiveAdapter";
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView txt_file_name;
        TextView txt_from_ip;
        public View view;

        ViewHolder(View v) {
            super(v);
            view = v;
            txt_file_name = v.findViewById(R.id.txt_name_file);
            txt_from_ip = v.findViewById(R.id.txt_from_ip);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReceiveAdapter(ArrayList<String> filePaths,ArrayList<String> myDataset, Context context, boolean isSend ) {
        mDataset = myDataset;
        this.context = context;
        this.isSend = isSend;
        this.filePaths = filePaths;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReceiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receive, parent, false);
        return new ViewHolder(v);
    }

    private void open_file(String filename) {
        File file;
        if(isSend){
            file = new File(filename);
        }else{
            File myDir = Environment.getExternalStorageDirectory();
            String FILE_TO_RECEIVED = myDir.getAbsolutePath() + "/";
            file = new File(FILE_TO_RECEIVED + "SOCKETFILEs/" + filename);
            Log.d(TAG, file.getAbsolutePath());
        }

        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(filename.substring(filename.lastIndexOf(".") + 1));
        newIntent.setDataAndType(Uri.fromFile(file), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final int p = mDataset.size() - position - 1;
        holder.txt_file_name.setText(mDataset.get(p).split(";")[0]);
        holder.txt_from_ip.setText(mDataset.get(p).split(";")[1].substring(0, 19));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSend){
                    open_file(filePaths.get(p));
                } else {
                    open_file(mDataset.get(p).split(";")[0]);
                }

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}