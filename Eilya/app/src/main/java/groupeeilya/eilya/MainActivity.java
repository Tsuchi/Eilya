package groupeeilya.eilya;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.ImageView;
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
    private ArrayList<String> Tab_img = new ArrayList<String>();
    private String lien_img;
    private DownloadTask jsonconn = null;
    private int flag=1;
    private int count=0;
    private String str = "";

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
    }

    public class DownloadTask extends AsyncTask<URL, Void, StringBuilder>
    {
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
            switch (flag){
                case 1:
                    Log.d("Case1", result.toString());
                    Get_ID(result);
                    break;

                case 2:
                    Log.d("pom", "case2");
                    Get_ImageLink(result);
                    break;
            }
        }
    }

    //A METTRE DANS LACTIVITE OU ON AFFICHERA LES IMAGES (SI BASTIEN N'AS PAS DEJA FAIT SA)
    public class DownloadImage extends AsyncTask<URL, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(URL... params) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }


           return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp)
        {
            ImageView iv = (ImageView) findViewById(R.id.imageView);
            iv.setImageBitmap(bmp);
            jsonconn.cancel(true);
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
        TabId_url.clear();
        try {
            JSONArray jsonArray = new JSONArray(str);
            for (i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TabId_url.add(jsonObject.getString("id"));

            }
            String[] simpleArray = TabId_url.toArray(new String[TabId_url.size()]);
            for (j = 0; j < TabId_url.size(); j++) {
                lien = "https://danbooru.donmai.us/posts/";
                lien = lien + simpleArray[j] + ".json";
                URL url2 = new URL(lien);
                flag = 2;
                new DownloadTask().execute(url2);
            }
            //Temporisation avant de passer tout les liens vers l'activité de Bastien
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //PassDataToSearchResultsActivity(); //La fonction qui va lancer l'activité d'affichage (IL FAUT DECOMMENTER POUR LANCER L'ACTIVITE DE BASTIEN)
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
            String lien_img_full;
            Tab_preview.clear();
            Tab_img.clear();
            count = 0;
        try
        {
            JSONObject jsonObject = new JSONObject(str);
            lien_img=jsonObject.getString("preview_file_url");
            lien_img_full = jsonObject.getString("file_url");
            lien_img="https://danbooru.donmai.us"+lien_img;
            lien_img_full = "https://danbooru.donmai.us"+lien_img_full;
            Tab_preview.add(lien_img);
            Tab_img.add(lien_img_full);
            Log.d("Work_plz",Tab_preview.get(count));
            count++;

            DownloadImage di = new DownloadImage();
            try{
                di.execute(new URL(Tab_preview.get(count - 1)));
                //di.execute(new URL(Tab_img.get(count - 1)));
            }
            catch(Exception e)
            {
                System.out.print(e + " rater \n");
            }
            jsonconn.cancel(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void LaunchSearchWithKeywords(String keywords, boolean SaveSearchHistory) {
        if (!keywords.isEmpty()) {
            flag = 1;
            editText_Search.setText(""); //Reset of the search bar
            String url = "https://danbooru.donmai.us/tags.json?search[name_matches]=";
            //String url = "https://danbooru.donmai.us/tags.json?search[name]=";
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
            //PARTI A METTRE SUR L'ACTIVITE DE BASTIEN POUR RECUPERER LES IMAGES EN FONCTION DE LEUR URL
        }
        else
            Toast.makeText(this, "Veuillez saisir un ou plusieurs mots-clés", Toast.LENGTH_LONG).show();
    }

    //Fonction a appelé quand on aura récupérer toute les info en fonction de la recherche pour les envoyé a Bastien
    private void PassDataToSearchResultsActivity()
    {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        Bundle paquet = new Bundle();
        paquet.putSerializable("Tab_img", Tab_img);
        paquet.putSerializable("Tab_preview", Tab_preview);
        intent.putExtra("paquet", paquet);
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

   /* public void Btn_TestOnClick(View view) {
        Intent openTest = new Intent(this, Activity_SearchResults.class);
        startActivity(openTest);
        //overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }*/

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
            Toast.makeText(this, "Historique de recherche effacé", Toast.LENGTH_LONG).show();
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


