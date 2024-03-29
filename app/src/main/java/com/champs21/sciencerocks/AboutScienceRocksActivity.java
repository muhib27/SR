package com.champs21.sciencerocks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutScienceRocksActivity extends AppCompatActivity {


    private TextView txtAbout;
    private AppCompatButton btnMta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_science_rocks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initAction();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initView(){
        txtAbout = (TextView)this.findViewById(R.id.txtAbout);
        btnMta = (AppCompatButton)this.findViewById(R.id.btnMta);
    }

    private void initAction(){
        txtAbout.setText(Html.fromHtml(getString(R.string.about_science_rocks)));
        btnMta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutScienceRocksActivity.this, MeetTheAnchorsRootActivity.class);
                startActivity(intent);
            }
        });
    }

}
