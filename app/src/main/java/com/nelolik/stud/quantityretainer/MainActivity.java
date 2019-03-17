package com.nelolik.stud.quantityretainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nelolik.stud.quantityretainer.Test.ShowAllActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, new MainFragment());
            fragmentTransaction.commit();
        }
    }

    public void onMenuShowAllRetentions(MenuItem item) {
        Intent intent = new Intent(this, ShowAllActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount()>0) {
            getSupportFragmentManager().popBackStack();
            return true;
        } else {
            return super.onSupportNavigateUp();
        }
    }

}
