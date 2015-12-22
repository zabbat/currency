package net.wandroid.badooconvert;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.wandroid.badooconvert.json.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductFragment.IProductFragmentListener {

    public static final String TAG_MAIN_FRAG = "TAG_MAIN_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ProductFragment fragment = ProductFragment.newInstance();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_frag_container, fragment, TAG_MAIN_FRAG).addToBackStack("product").commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

    @Override
    public void onItemClicked(String sku, ArrayList<Transaction> transactions) {
        CurrencyFragment fragment = CurrencyFragment.newInstance(sku,transactions);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_frag_container, fragment, TAG_MAIN_FRAG).addToBackStack("currency").commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() <= 1) {
            //onBackPressed();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            return super.onSupportNavigateUp();
        } else {
            manager.popBackStack();
            return true;
        }
    }
}
