package com.engin.taplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnCanceledListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.RankingsClient;
import com.huawei.hms.jos.games.ranking.Ranking;
import com.huawei.hms.jos.games.ranking.RankingScore;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.OnClick;

public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = "LeaderboardActivity";
    private TextView leaderboardTv, leaderboardTitle;
    private ImageView imageIv;
    RecyclerView recyclerView;
    private RankingsClient rankingsClient;
    StringBuffer buffer ;
    List<RankingScore> scoresBuffer;
    private static final String LEADERBOARD_ID = "078966F608D0E5ADB5661D7F2BFE67EFD94CB61426D1487587A34FFA0C5F21F6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardTv = findViewById(R.id.leaderboardTv);
        leaderboardTitle = findViewById(R.id.leaderboardTitle);
        imageIv = findViewById(R.id.imageviewlb);


        rankingsClient = Games.getRankingsClient(this);
        Intent intent = getIntent();
        int intentData = intent.getIntExtra("LB" , 1);
        buffer = new StringBuffer();
        if(intentData == 1) {
            leaderboardTitle.setText("Math Profs");
            int timeDimension = 2;
            int maxResults = 20;
            long offsetPlayerRank = 0;
            int pageDirection = 0;
            Task<RankingsClient.RankingScores> task
                    = rankingsClient.getRankingTopScores(LEADERBOARD_ID, timeDimension, maxResults, offsetPlayerRank, pageDirection);
            buffer = new StringBuffer();
            addClientRankingScoresListener(task, buffer.toString());
        }
    }

    private void addClientRankingScoresListener(final Task<RankingsClient.RankingScores> task, final String method) {
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG , method + " failure. exception: " + e);
            }
        });
        task.addOnSuccessListener(new OnSuccessListener<RankingsClient.RankingScores>() {
            @Override
            public void onSuccess(RankingsClient.RankingScores s) {
                Log.e(TAG ,  " method " + " success. ");
                Ranking ranking = task.getResult().getRanking();
                scoresBuffer = task.getResult().getRankingScores();
                if (scoresBuffer.size() < 1) {
                    Toast.makeText(LeaderboardActivity.this, "scoresBuffer empty", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView = findViewById(R.id.leaderboardRv);
                    LeaderboardAdapter leaderboardAdapter = new LeaderboardAdapter(LeaderboardActivity.this, scoresBuffer);
                    recyclerView.setAdapter(leaderboardAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(LeaderboardActivity.this));
                }
            }
        });
        task.addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.d(TAG, method + " canceled. ");
            }
        });
    }

    @OnClick(R.id.imageviewlb)
    public void goBack() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}