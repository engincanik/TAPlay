package com.engin.taplay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import org.json.JSONException;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity  {
    private final String TAG = "GameApplication";
    private PlayersClient playersClient;
    private String playerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    private void init(AuthHuaweiId authHuaweiId) {
        JosAppsClient appsClient = JosApps.getJosAppsClient(this, authHuaweiId);
        appsClient.init();
        showLog("Initiated");
    }

    public void showLog(String logLine) {
        StringBuffer sbLog = new StringBuffer();
        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.ENGLISH);
        String time = format.format(new Date());

        sbLog.append(time).append(":").append(logLine);

        Log.i(TAG, sbLog.toString());
    }

    public HuaweiIdAuthParams getHuaweiIdParams() {
        return new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME)
                .setIdToken().createParams();
    }

    @OnClick(R.id.loginBtn)
    public void signIn() {
        Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.getService(this, getHuaweiIdParams())
                .silentSignIn();
        authHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
            @Override
            public void onSuccess(AuthHuaweiId authHuaweiId) {
                Log.i(TAG, "silent signIn success");
                Log.i(TAG, "display:" + authHuaweiId.getDisplayName());
                init(authHuaweiId);
                login(authHuaweiId);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.i(TAG, "signIn failed:" + apiException.getStatusCode());
                    Log.i(TAG, "start getSignInIntent");
                    HuaweiIdAuthService service = HuaweiIdAuthManager.getService(MainActivity.this, getHuaweiIdParams());
                    startActivityForResult(service.getSignInIntent(), 6013);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 6013) {

            if (null == data) {
                showLog("signIn intent is null");
                return;
            }
            String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
            if (TextUtils.isEmpty(jsonSignInResult)) {
                showLog("signIn result is empty");
                return;
            }
            try {
                HuaweiIdAuthResult signInResult = new HuaweiIdAuthResult().fromJson(jsonSignInResult);
                if (0 == signInResult.getStatus().getStatusCode()) {
                    showLog("signIn success.");
                    showLog("signIn result: " + signInResult.toJson());
                } else {
                    showLog("signIn failed: " + signInResult.getStatus().getStatusCode());
                }
            } catch (JSONException var7) {
                showLog("Failed to convert json from signInResult.");
            }

        }
    }

    public void login(AuthHuaweiId authHuaweiId) {
        playersClient = Games.getPlayersClient(this, authHuaweiId);
        Task<Player> playerTask = playersClient.getCurrentPlayer();
        playerTask.addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                playerID = player.getPlayerId();
                Log.i(TAG, "getPlayerInfo Success, player info: " + player.getPlayerId());
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("playerID", playerID);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    Log.e(TAG, "getPlayerInfo failed, status: " + ((ApiException) e).getStatusCode());
                }
            }
        });

    }
}
