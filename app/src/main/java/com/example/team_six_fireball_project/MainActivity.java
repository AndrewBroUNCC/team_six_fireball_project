package com.example.team_six_fireball_project;
//Andrew Brown
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //how to get the textView
        TextView textViewTitle = findViewById(R.id.TextViewTesting);

        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewTitle.setText("Yes i was clicked!");
            }
        });
    }
}