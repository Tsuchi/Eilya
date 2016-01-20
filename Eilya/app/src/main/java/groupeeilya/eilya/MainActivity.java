package groupeeilya.eilya;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editText_Search;
    private GestureDetectorCompat detectSwipe;
    private ListView lv_searchHistorique;
    private ArrayList<String> TabId_url = new ArrayList<String>();
    private ArrayList<String> Tab_preview = new ArrayList<String>();
    private DownloadTask jsonconn = null;
    private String str = "";
    private String test = "http://danbooru.donmai.us/tags.json?search[name_matches]=batman";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.print("vrai");
        editText_Search = (EditText) findViewById(R.id.editTextSearch);
        detectSwipe = new GestureDetectorCompat(this, new MyGestureListener());
        lv_searchHistorique = (ListView) findViewById(R.id.listView_HistoriqueRecherche);

        //Loading searchHistory
        loadListViewHistorique();


        /*// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://groupeeilya.eilya/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://groupeeilya.eilya/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }

    public class DownloadTask extends AsyncTask<URL, Void, StringBuilder> {
        @Override
        protected StringBuilder doInBackground(URL... params) {
            String result = "";
            int i = 0;
            int j = 0;
            StringBuilder resultSB = new StringBuilder();
            try {
                System.out.print("URL");

                URLConnection urlConnection = (HttpURLConnection) params[0].openConnection();
                Log.d("pom", "pompompolmui");
                InputStream input = urlConnection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = in.readLine()) != null) {
                    resultSB.append(line);
                    str = resultSB.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultSB;
        }

        @Override
        protected void onPostExecute(StringBuilder result)
        {




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

    public void Btn_Search(View view) {
        String keywords = editText_Search.getText().toString();
        LaunchSearchWithKeywords(keywords, true);
    }
    public void Get_ID(StringBuilder string)
    {
        String lien;
        int i = 0;
        int j=0;
        str = string.toString();
        try {
            JSONArray jsonArray = new JSONArray(str);
            for (i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TabId_url.add(jsonObject.getString("id"));
                String[] simpleArray = TabId_url.toArray(new String[TabId_url.size()]);
                for (j = 0; j < TabId_url.size(); j++)
                {
                    lien = "https://danbooru.donmai.us/posts/";
                    lien = lien + simpleArray[j] + ".json";
                    URL url2 = new URL(lien);
                    jsonconn.execute(url2);
                }
            }
        } catch (JSONException e) {
            Log.d("JSON", "JSONException");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void Get_ImageLink(StringBuilder string)
    {
            int i=0;
            str = string.toString();
        JSONArray jsonArray = null;
        try
        {
            jsonArray = new JSONArray(str);
            for (i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Tab_preview.add(jsonObject.getString("preview_file_url"));
                String[] simpleArray = Tab_preview.toArray(new String[Tab_preview.size()]);
                for (int j = 0 ; j<(Tab_preview.size()) ; j++)
                {
                    simpleArray[j]="https://danbooru.donmai.us"+simpleArray[j];
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }



    }

    private void LaunchSearchWithKeywords(String keywords, boolean SaveSearchHistory) {
        if (!keywords.isEmpty()) {
            editText_Search.setText(""); //Reset of the search bar
            String url = "https://danbooru.donmai.us/tags.json?search[name_matches]=";
            String[] tabKeyword = keywords.split(" ");

            for (int i = 0; i < tabKeyword.length; i++) {
                if (i > 0)
                    url = url.concat("+");
                url = url.concat("*");
                url = url.concat(tabKeyword[i]);
                url = url.concat("*");
            }
            //Toast.makeText(this, url.toString(), Toast.LENGTH_SHORT).show();
            if(IsKeywordAlreadyExist(keywords) && SaveSearchHistory)
                writeToSearchHistoryfile(tabKeyword);
            jsonconn = new DownloadTask();
            try{
                System.out.print("vrai");
                jsonconn.execute(new URL(url));
            }
            catch (Exception e)
            {
                System.out.print("faux");
            }
        }
        else
            Toast.makeText(this, "Veuillez saisir un ou plusieurs mots-clés", Toast.LENGTH_LONG).show();
    }

    //Fonction a appelé quand on aura récupérer toute les info en fonction de la recherche pour les envoyé a Bastien
    private void passDataToSearchResultsActivity(String[] tabUrl)
    {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("tabUrl", tabUrl);
        startActivity(intent);
    }

    public void Btn_OptionOnClick(View view) {
        Intent openOption = new Intent(this, Activity_Options.class);
        startActivity(openOption);
        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);
    }

    public void Btn_FiltrageOnClick(View view) {
        Intent openFiltrage = new Intent(this, Activity_Filtrage.class);
        startActivity(openFiltrage);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    public void Btn_TestOnClick(View view) {
        Intent openTest = new Intent(this, Activity_SearchResults.class);
        startActivity(openTest);
        //overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    private void writeToSearchHistoryfile(String[] tabkeyword) {
        FileOutputStream fos = null;
        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Eilya/");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            final File myFile = new File(dir, "search_history.txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
            }

            fos = new FileOutputStream(myFile, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            StringBuilder str = new StringBuilder();

            for (int i = 0; i < tabkeyword.length; i++) {
                str.append(tabkeyword[i]);
                str.append(" ");
            }
            str.append("\n");
            osw.append(str.toString());
            osw.close();
            fos.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        loadListViewHistorique();
    }

    private void loadListViewHistorique() {
        ArrayList<String> array_Url = readHistoryFile();
        // Create ArrayAdapter using the planet list.
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, array_Url);
        // Set the ArrayAdapter as the ListView's adapter.
        lv_searchHistorique.setAdapter(listAdapter);
        lv_searchHistorique.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                Object o = lv_searchHistorique.getItemAtPosition(position);
                String str = (String) o;//As you are using Default String Adapter
                LaunchSearchWithKeywords(((TextView) view).getText().toString(), false);
            }
        });
    }

    private boolean IsKeywordAlreadyExist(String keywords) {
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(sdcard, "/Eilya/search_history.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                keywords = keywords.trim();
                if (line.equalsIgnoreCase(keywords))
                    return false;
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return true;
    }

    private ArrayList<String> readHistoryFile() {
        //Find the directory for the SD Card using the API
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(sdcard, "/Eilya/search_history.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        String str_text = text.toString();
        String[] tabUrl = str_text.split("\\n");
        final ArrayList<String> array_Url = new ArrayList<String>();
        array_Url.addAll(Arrays.asList(tabUrl));
        return array_Url;
    }

    public void resetHistory(View view) {
        //Find the directory for the SD Card using the API
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(sdcard, "/Eilya/search_history.txt");

        boolean deleted = file.delete();
        if (deleted == true)
            Toast.makeText(this, "Historique de recherche effacer", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Echec suppression historique de recherche", Toast.LENGTH_LONG).show();

        loadListViewHistorique();
    }

    //Classe qui va détecter les balayements vers la droite et la gauche sur l'écran
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (event2.getX() < event1.getX()) {
                Intent intent = new Intent(
                        MainActivity.this, Activity_Filtrage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }

            if (event2.getX() > event1.getX()) {
                Intent intent = new Intent(
                        MainActivity.this, Activity_Options.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);
            }

            return true;
        }


    }
}


