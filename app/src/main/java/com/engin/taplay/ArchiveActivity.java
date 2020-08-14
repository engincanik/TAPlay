package com.engin.taplay;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.ArchivesClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.archive.ArchiveDetails;
import com.huawei.hms.jos.games.archive.ArchiveSummary;
import com.huawei.hms.jos.games.archive.ArchiveSummaryUpdate;

import java.nio.charset.Charset;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArchiveActivity extends AppCompatActivity {
    ArchivesClient client;
    int maxThumbnailSize;
    int detailSize;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        ButterKnife.bind(this);
        client = Games.getArchiveClient(this);
        queryLimits();
//        bitmap = Bitmap.createBitmap(detailSize, detailSize, Bitmap.Config.ARGB_8888);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
    }

    public void queryLimits() {
        Task<Integer> thumbnailSizeTask = Games.getArchiveClient(this).getLimitThumbnailSize();
        thumbnailSizeTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                maxThumbnailSize = integer;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e("archive","statusCode:" + apiException.getStatusCode());
                }
            }
        });
        Task<Integer> detailSizeTask = Games.getArchiveClient(this).getLimitDetailsSize();
        detailSizeTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                detailSize = integer;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e("archive","statusCode:" + apiException.getStatusCode());
                }
            }
        });

        Toast.makeText(this, "Max Size: " + maxThumbnailSize + "Detail Size: " + detailSize,
                Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.saveBtn)
    public void saveGame() {
        ArchiveSummaryUpdate archiveSummaryUpdate =
                new ArchiveSummaryUpdate.Builder().setActiveTime(10000)
                        .setCurrentProgress(200)
                        .setDescInfo("Hungry for Apples")
                        .setThumbnail(bitmap)
                        .setThumbnailMimeType("png")
                        .build();
        ArchiveDetails archiveContents = new ArchiveDetails.Builder().build();
        archiveContents.set(("Hungry for Apples" + "," + 200 + "," + 10000).getBytes(Charset.forName("UTF-8")));
        ArchivesClient client = Games.getArchiveClient(this);
        Task<ArchiveSummary> addArchiveTask = client.addArchive(archiveContents, archiveSummaryUpdate, false);
        addArchiveTask.addOnSuccessListener(new OnSuccessListener<ArchiveSummary>() {
            @Override
            public void onSuccess(ArchiveSummary archiveSummary) {
                if (archiveSummary != null) {
                    String fileName = archiveSummary.getFileName();
                    String archiveId = archiveSummary.getId();
                    Toast.makeText(ArchiveActivity.this, "File name: " + fileName + "id: " + archiveId,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e("archive","statusCode:" + apiException.getStatusCode());
                }
            }
        });
    }



}