package mauro.rodriguez.visualizadorrss.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import mauro.rodriguez.visualizadorrss.manejadores.EventManager;
import mauro.rodriguez.visualizadorrss.manejadores.ScreenManager;
import mauro.rodriguez.visualizadorrss.R;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class DescActivity extends ActionBarActivity {
    private EventManager em;
    private ScreenManager sm;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.descripcion_notica_layout);
        em = new EventManager(this);
        sm= new ScreenManager(this, em);
        ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_only_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
