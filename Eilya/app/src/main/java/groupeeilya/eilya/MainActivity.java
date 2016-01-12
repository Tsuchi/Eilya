package groupeeilya.eilya;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private EditText editText_Search;
    private GestureDetectorCompat detectSwipe;

    private DownloadTask jsonconn = null;
    private String str = "";
    private String test="http://danbooru.donmai.us/tags.json?search[name_matches]=batman";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText_Search = (EditText) findViewById(R.id.editTextSearch);

        detectSwipe =  new GestureDetectorCompat(this, new MyGestureListener());

        jsonconn = new DownloadTask();
        try{
            jsonconn.execute(new URL(test));
        }
        catch (Exception e)
        {
            System.out.print("faux");
        }



    }



    public class DownloadTask extends AsyncTask<URL, Void, StringBuilder> {
        @Override
        protected StringBuilder doInBackground(URL... params) {
            String result="";
            StringBuilder resultSB = new StringBuilder();
            try {

                URL url = new URL("http://danbooru.donmai.us/tags.json?search[name_matches]=batman");
                URLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader (new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    resultSB.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultSB;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
             str=result.toString();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detectSwipe.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void Btn_Search(View view)
    {
        String keyword = editText_Search.getText().toString();
        editText_Search.setText(""); //Reset of the search bar
        Toast.makeText(this, "Search with keyword : " + keyword, Toast.LENGTH_LONG).show();


    }

    public void Btn_OptionOnClick(View view)
    {
        Intent openOption = new Intent(this, Activity_Options.class);
        startActivity(openOption);
        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);
    }

    public void Btn_FiltrageOnClick(View view)
    {
        Intent openFiltrage = new Intent(this, Activity_Filtrage.class);
        startActivity(openFiltrage);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    //Classe qui va détecter les balayements vers la droite et la gauche sur l'écran
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event2.getX() < event1.getX())
            {
                Intent intent = new Intent(
                        MainActivity.this, Activity_Filtrage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }

            if(event2.getX() > event1.getX())
            {
                Intent intent = new Intent(
                        MainActivity.this, Activity_Options.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);
            }

            return true;
        }
    }
}


