package com.engin.taplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {
    private String playerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        playerID = intent.getStringExtra("playerID");
    }

    public void openNewGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("playerID", playerID);
        startActivity(intent);
    }
}
