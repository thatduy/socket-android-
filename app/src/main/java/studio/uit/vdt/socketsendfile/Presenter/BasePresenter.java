package studio.uit.vdt.socketsendfile.Presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import studio.uit.vdt.socketsendfile.R;

/**
 * Created by ASUS on 30-Mar-18.
 */

public class BasePresenter {
    protected Context context;
    ProgressDialog progressDialog;
    public BasePresenter(Context context) {
        this.context = context;
    }
    public void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
    public void showProgress(String til, String mess) {
        progressDialog = new ProgressDialog(context, R.style.NewDialog);
        progressDialog.setTitle(til);
        progressDialog.setMessage(mess);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }
}
