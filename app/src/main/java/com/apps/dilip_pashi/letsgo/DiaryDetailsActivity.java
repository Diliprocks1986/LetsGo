package com.apps.dilip_pashi.letsgo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;

import static com.apps.dilip_pashi.letsgo.LetsGo.DETAILS;
import static com.apps.dilip_pashi.letsgo.LetsGo.mReference;

public class DiaryDetailsActivity extends AppCompatActivity {
    String Location;
    private String getDiaryKey, getKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_details);
        getKey = getIntent().getExtras().getString("Key");
        getDiaryKey = getIntent().getExtras().getString("DiaryKey");
        Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setTitle(getDiaryKey);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        mReference.child(getKey).child(DETAILS).child(getDiaryKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView DiaryImage = (ImageView) findViewById(R.id.DiaryImage);
                Picasso.with(DiaryDetailsActivity.this)
                        .load((String) dataSnapshot.child("diaryImage").getValue())
                        .resize(1000, 1000)
                        .centerCrop()
                        .into(DiaryImage);
                TextView DeskView = (TextView) findViewById(R.id.DetailsDesc);
                DeskView.setText("Diary Desc: "+(String) dataSnapshot.child("diaryDesc").getValue());
                TextView DateView = (TextView) findViewById(R.id.DetailsDate);
                DateView.setText("Diary Date: "+(String) dataSnapshot.child("diaryDate").getValue());
                TextView LocationView = (TextView) findViewById(R.id.DetailsLoc);
                Location = (String) dataSnapshot.child("diaryLocation").getValue();
                LocationView.setText(String.format("Diary Location: "+ Location));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ImageView back = (ImageView) findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DiaryDetailsActivity.this, DiaryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("key", getKey);
                startActivity(i);
            }
        });
    }

    public void fabClick(View view) {
        Uri gmmIntentUri = Uri.parse(String.format("geo:0,0?q=%s", URLEncoder.encode(Location)));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
