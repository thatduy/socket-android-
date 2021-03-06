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
import studio.uit.vdt.socketsendfile.model.SendModel;

/*
 * Created by ASUS on 29-Mar-18.
 */


public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.ViewHolder> {
    private ArrayList<SendModel> mDataset;
    private Context context;
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
    public ReceiveAdapter(ArrayList<SendModel> myDataset, Context context ) {
        mDataset = myDataset;
        this.context = context;

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
        file = new File(filename);

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
        holder.txt_file_name.setText(mDataset.get(p).getName());
        holder.txt_from_ip.setText(mDataset.get(p).getDate());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_file(mDataset.get(p).getPath());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}