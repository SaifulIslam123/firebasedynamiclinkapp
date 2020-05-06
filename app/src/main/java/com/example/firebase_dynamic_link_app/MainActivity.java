package com.example.firebase_dynamic_link_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    TextView shareTV, resultTV, titelTV;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareTV = findViewById(R.id.share);
        resultTV = findViewById(R.id.resultTV);
        titelTV = findViewById(R.id.titleTV);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        shareTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                createAndShareDeepLink();
            }
        });


        getDeepLinkData();


    }

    private void getDeepLinkData() {

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Log.d(TAG, "onSuccess: ");

                        Uri deepLinkUri = null;
                        if (pendingDynamicLinkData != null)
                            deepLinkUri = pendingDynamicLinkData.getLink();

                        if (deepLinkUri != null) {
                            titelTV.setText("Deeplink value: ");
                            resultTV.setText(deepLinkUri.getQueryParameter("curpage"));
                            Log.d(TAG, deepLinkUri.getQueryParameter("curpage"));

                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });

    }

    private void createAndShareDeepLink() {

        progressDialog.show();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.mainpassedurl.com/?curpage="+new Random().nextInt(1000)))
                .setDomainUriPrefix("https://firebasedynamiclinkapp.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.example.firebase_dynamic_link_app")
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            titelTV.setText("Shareable short link: ");
                            resultTV.setText(shortLink.toString());
                            shareData(shortLink.toString());
                        } else {
                            // Error
                            // ...
                            titelTV.setText("Shareable short link: error occured");
                            progressDialog.dismiss();
                        }
                    }
                });


    }

    private void shareData(String url) {
        //TODO: share url code from here


    }
}

//ref links:
// https://firebase.google.com/docs/dynamic-links/android/create
//https://www.youtube.com/watch?v=zra2DCd0DnY&t=174s
//https://www.youtube.com/watch?v=H4ae9Jv5B3I
