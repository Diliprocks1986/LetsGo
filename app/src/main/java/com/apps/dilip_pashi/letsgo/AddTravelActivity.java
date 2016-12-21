package com.apps.dilip_pashi.letsgo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import static com.apps.dilip_pashi.letsgo.LetsGo.UserPic;
import static com.apps.dilip_pashi.letsgo.LetsGo.mDatabase;
import static com.apps.dilip_pashi.letsgo.LetsGo.mReference;
import static com.apps.dilip_pashi.letsgo.LetsGo.mUsername;

/**
 * Created by Dilip_pashi on 04-12-2016.
 */

public class AddTravelActivity extends AppCompatActivity {
    public static String FromDate, ToDate;
    public static String Place;
    static Button ST, ET;
    EditText placeTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_add_edit);
        ST = (Button) findViewById(R.id.startingDate);
        ET = (Button) findViewById(R.id.endingDate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.tvtool);
        toolTitle.setText("Add Travel");
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false);
        mDatabase = FirebaseDatabase.getInstance();
        ImageView UserImg = (ImageView) findViewById(R.id.profile_pic);
        Picasso.with(this).load(UserPic).resize(500, 500).centerCrop().into(UserImg);
        ImageView back = (ImageView) findViewById(R.id.backBtn);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddTravelActivity.this, TravelActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


    }

    public void showDatePickerDialog(View v) {
        switch (v.getId()) {
            case R.id.startingDate:
                DialogFragment FromFragment = new FromDateFragment();
                FromFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.endingDate:
                DialogFragment ToFragment = new ToDateFragment();
                ToFragment.show(getSupportFragmentManager(), "datePicker");
                break;
        }
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater blowup = getMenuInflater();
        blowup.inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                placeTitle = (EditText) findViewById(R.id.placeTitle);
                Place = placeTitle.getText().toString();
                TravelModel PlaceVisit = new TravelModel(Place, FromDate, ToDate);
                if (!TextUtils.isEmpty(Place) && !TextUtils.isEmpty(FromDate) && !TextUtils.isEmpty(ToDate)) {
                    mReference.child(Place).setValue(PlaceVisit);
                    Intent i = new Intent(AddTravelActivity.this, TravelActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    Toast.makeText(AddTravelActivity.this, "Please Fill all Fields!", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.cancel:
                Toast.makeText(AddTravelActivity.this, "Entry Canceled by " + mUsername, Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(AddTravelActivity.this, TravelActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static class FromDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public static void fromDate(String date) {
            ST.setText("Travel Starts on:  " + date);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            FromDate = String.valueOf(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append(" "));
            fromDate(FromDate);
        }
    }

    public static class ToDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public static void toDate(String date) {
            ET.setText("Travel Ends on:    " + date);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ToDate = String.valueOf(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append(" "));
            toDate(ToDate);
        }
    }
}
