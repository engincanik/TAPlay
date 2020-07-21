package com.engin.taplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.RankingsClient;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity {
    TextView number1;
    TextView number2;
    TextView number3;
    TextView number4;
    TextView number5;
    TextView number6;
    EditText answer1;
    EditText answer2;
    EditText answer3;
    Integer gameScore = 0;
    Integer totalScore;
    SharedPreferences sharedPreferences;
    RankingsClient rankingsClient;
    private static final String LEADERBOARD_ID = "078966F608D0E5ADB5661D7F2BFE67EFD94CB61426D1487587A34FFA0C5F21F6";
    public static final String TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        sharedPreferences = this.getSharedPreferences("com.engin.taplay", Context.MODE_PRIVATE);
        rankingsClient = Games.getRankingsClient(this);
        enableRankingSwitchStatus(1);
        getTotalScore();
        randomNumbers();
    }




    public void randomNumbers() {
        Random random = new Random();
        number1 = findViewById(R.id.numberTv);
        number2 = findViewById(R.id.numberTv2);
        number3 = findViewById(R.id.numberTv3);
        number4 = findViewById(R.id.numberTv4);
        number5 = findViewById(R.id.numberTv5);
        number6 = findViewById(R.id.numberTv6);

        number1.setText(String.valueOf(random.nextInt(1000)));
        number2.setText(String.valueOf(random.nextInt(1000)));
        number3.setText(String.valueOf(random.nextInt(1000)));
        number4.setText(String.valueOf(random.nextInt(1000)));
        number5.setText(String.valueOf(random.nextInt(1000)));
        number6.setText(String.valueOf(random.nextInt(1000)));
    }

    @OnClick(R.id.checkBtn)
    public  void checkAnswers() {
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        int answer1Val = Integer.parseInt(answer1.getText().toString());
        int answer2Val = Integer.parseInt(answer2.getText().toString());
        int answer3Val = Integer.parseInt(answer3.getText().toString());

        if (answer1 == null) {
            answer1Val = 0;
        }
        if (answer2.getText() == null) {
            answer2Val = 0;
        }
        if (answer3.getText() == null) {
            answer3Val = 0;
        }


        if (answer1Val == Integer.parseInt(number1.getText().toString()) + Integer.parseInt(number2.getText().toString())) {
            if (answer1.isEnabled()) {
                gameScore++;
                totalScore++;
            }
            answer1.setBackgroundColor(Color.parseColor("#008000"));
            answer1.setEnabled(false);
        } else {
            answer1.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        if (answer2Val == Integer.parseInt(number3.getText().toString()) + Integer.parseInt(number4.getText().toString())) {
            if (answer2.isEnabled()) {
                gameScore++;
                totalScore++;
            }
            answer2.setBackgroundColor(Color.parseColor("#008000"));
            answer2.setEnabled(false);
        } else {
            answer2.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        if (answer3Val == Integer.parseInt(number5.getText().toString()) + Integer.parseInt(number6.getText().toString())) {
            if (answer3.isEnabled()) {
                gameScore++;
                totalScore++;
            }
            answer3.setBackgroundColor(Color.parseColor("#008000"));
            answer3.setEnabled(false);
        } else {
            answer3.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        
        sharedPreferences.edit().putInt("totalScore", totalScore).apply();
        submitScore(totalScore);
        if (gameScore == 3) {
            Toast.makeText(this, "You answered all questions correct", Toast.LENGTH_LONG).show();
            return;
        }

    }

    public void submitScore(int score) {
        rankingsClient.submitRankingScore(LEADERBOARD_ID, score);
    }

    private void enableRankingSwitchStatus (int status) {
        Task<Integer> task = rankingsClient.setRankingSwitchStatus(status);
        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer statusValue) {
                // success to set the value,the server will reponse the latest value.
                Log.d(TAG, "setRankingSwitchStatus success : " +statusValue) ;
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // errCode information
                if (e instanceof ApiException) {
                    String result = "Err Code:" + ((ApiException) e).getStatusCode();
                    Log.e(TAG , "setRankingSwitchStatus error : " + result);
                }
            }
        });
    }


    public void getTotalScore() {
        if (sharedPreferences != null) {
            totalScore = sharedPreferences.getInt("totalScore", 0);
            Toast.makeText(this, totalScore.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Couldn't get your total score. This game score cannot be recorded",
                    Toast.LENGTH_LONG).show();
        }
    }

}
