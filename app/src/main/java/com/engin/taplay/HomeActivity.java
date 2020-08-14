package com.engin.taplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {
    private String playerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        playerID = intent.getStringExtra("playerID");
        ButterKnife.bind(this);
    }

    @OnClick(R.id.gameBtn)
    public void openNewGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("playerID", playerID);
        startActivity(intent);
    }

    @OnClick(R.id.leaderboardBtn)
    public void openLeaderboard() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.archiveBtn)
    public  void openArchive() {
        Intent intent = new Intent(this, ArchiveActivity.class);
        startActivity(intent);
    }


}
