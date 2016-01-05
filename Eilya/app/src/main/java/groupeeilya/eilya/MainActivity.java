package groupeeilya.eilya;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editText_Search;
    private GestureDetectorCompat detectSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_Search = (EditText) findViewById(R.id.editTextSearch);

        detectSwipe =  new GestureDetectorCompat(this, new MyGestureListener());
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
        String keywords = editText_Search.getText().toString();
        editText_Search.setText(""); //Reset of the search bar
        String url = "https://danbooru.donmai.us/tags.json?search[name_matches]=";
        String [] tabKeyword = keywords.split(" ");

        for(int i=0; i < tabKeyword.length; i++)
        {
            if (i > 0)
                url = url.concat("+");
            url = url.concat("*");
            url = url.concat(tabKeyword[i]);
            url = url.concat("*");
        }
        Toast.makeText(this, "" + url, Toast.LENGTH_LONG).show();
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

    public void Btn_TestOnClick(View view)
    {
        Intent openTest = new Intent(this, Activity_SearchResults.class);
        startActivity(openTest);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
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


