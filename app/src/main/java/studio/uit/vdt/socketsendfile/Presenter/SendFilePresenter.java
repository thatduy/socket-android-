package studio.uit.vdt.socketsendfile.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ASUS on 30-Mar-18.
 */

public class SendFilePresenter extends BasePresenter {

    private final static int PICKFILE_REQUEST_CODE = 1122;

    public SendFilePresenter(Context context) {
        super(context);
    }

    public void openFile(String minmeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(minmeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA_MULTIPLE");
        // if you want any file type, you can skip next line
        sIntent.putExtra("CONTENT_TYPE", minmeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (context.getPackageManager().resolveActivity(sIntent, 0) != null){
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        }
        else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }
        try {
            ((Activity) context).startActivityForResult(chooserIntent, PICKFILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }
}
