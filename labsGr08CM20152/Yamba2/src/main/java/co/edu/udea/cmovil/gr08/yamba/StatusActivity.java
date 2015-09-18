package co.edu.udea.cmovil.gr08.yamba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;


public class StatusActivity extends Activity {
    //INSTANCIAR
    private static final String TAG =StatusActivity.class.getSimpleName();
    private Button mButtonTweet;
    private EditText mTextStatus;
    private TextView mTextCount;
    private int mDefaultColor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_activity);
        //INICIALIZAR
        mButtonTweet = (Button) findViewById(R.id. status_button_tweet );
        mTextStatus = (EditText) findViewById(R.id. status_text );
        mTextCount = (TextView) findViewById(R.id. status_text_count );
        mTextCount .setText(Integer. toString ( 140 ));
        mDefaultColor = mTextCount .getTextColors().getDefaultColor();
        mTextStatus.requestFocus();

    }

    public void clickTweet(View v){

            mButtonTweet.setEnabled(true);
            String status = mTextStatus.getText().toString();
            PostTask postTask = new PostTask();
            postTask.execute(status);
            Log.d(TAG, "clickTweet");
          //  esconderTeclado();
      //  }
    }
/*
    private void mostrarTeclado(){
        InputMethodManager imm = (InputMethodManager)
            getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    private void esconderTeclado() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class PostTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute(){
            progress = new ProgressDialog(StatusActivity.this);
            progress.setMessage("Posteando a la nube");
            progress.setIndeterminate(false);
            progress.setCancelable(true);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                YambaClient cloud = new YambaClient("student", "password");
                cloud.postStatus(params[0]);
                Log.d(TAG, "Tweet enviado con exito a la nube: " +params[0] );
                return "Tweet enviado con exito";
            }catch(Exception e){
                Log.e(TAG, "No se pudo enviar a la nube", e);
                e.printStackTrace();
                return "No se pudo enviar a la nube";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(progress.isShowing()){
                progress.dismiss();
            }

            if(this != null && result != null)
                Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
       //     mostrarTeclado();
        }
    }

}


