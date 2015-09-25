package co.edu.udea.cmovil.gr08.yamba;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.thenewcircle.yamba.client.YambaClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {
    private static final String TAG = StatusFragment.class.getSimpleName();
    private Button mButtonTweet;
    private EditText mTextStatus;
    private TextView mTextCount;
    private int mDefaultColor;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status, container, false);

        mButtonTweet = (Button) v.findViewById(R.id.status_button_tweet);
        mTextStatus = (EditText) v.findViewById(R.id.status_text);
        mTextCount = (TextView) v.findViewById(R.id.status_text_count);
        mTextCount.setText(Integer.toString(140));
        mDefaultColor = mTextCount.getTextColors().getDefaultColor();

        mButtonTweet.setOnClickListener(new View.OnClickListener() {
            @Override
               public void onClick(View v) {
                mButtonTweet.setEnabled(true);
                String status = mTextStatus.getText().toString();
                PostTask postTask = new PostTask();
                postTask.execute(status);
                Log.d(TAG, "onClick");
                mTextStatus.setText("");  //borramos texto cuando el usuario envia a la nube
            }
        });

        mTextStatus.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                int count = 140 - s.length();
                mTextCount.setText(Integer.toString(count));

                if (count < 50) {
                    mTextCount.setTextColor(Color.RED);
                } else {
                    mTextCount.setTextColor(mDefaultColor);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

        });

        Log.d(TAG, "onCreated");

        return v;
    }


    class PostTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "Posteando a la nube",
                    "Espere...");
            progress.setCancelable(true);
        }

        @Override
        protected String doInBackground(String... params) {
                try {
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    String username = prefs.getString("username", "");
                    String password = prefs.getString("password", "");

                    // Check that username and password are not empty
                    // If empty, Toast a message to set login info and bounce to
                    // SettingActivity
                    // Hint: TextUtils.
                    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                        getActivity().startActivity(
                                new Intent(getActivity(), SettingsActivity.class));
                        return "Por favor actualizar nombre de usuario y contraseña";
                    }

                    YambaClient cloud = new YambaClient(username, password);
                    cloud.postStatus(params[0]);


                    Log.d(TAG, "Tweet enviado con exito a la nube: " +params[0] );
                return "Tweet enviado con exito";
            }catch(Exception e){
                Log.e(TAG, "No se pudo enviar a la nube", e);
                e.printStackTrace();
                return "No se pudo enviar a la nube";
            }
        }

        // Called after doInBackground() on UI thread
        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (getActivity() != null && result != null)
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
        }

    }
}

