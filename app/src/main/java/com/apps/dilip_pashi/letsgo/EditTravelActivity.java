package com.apps.dilip_pashi.letsgo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import static com.apps.dilip_pashi.letsgo.LetsGo.UserPic;
import static com.apps.dilip_pashi.letsgo.LetsGo.mReference;
import static com.apps.dilip_pashi.letsgo.LetsGo.mUsername;


public class EditTravelActivity extends AppCompatActivity {
    public static String placeName, startDate, endDate, Place;
    static Button ST, ET;
    EditText editPlaceTitle;
    private String getKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_add_edit);
        ST = (Button) findViewById(R.id.startingDate);
        ET = (Button) findViewById(R.id.endingDate);
        ImageView UserImg = (ImageView) findViewById(R.id.profile_pic);
        getKey = getIntent().getExtras().getString("key");
        Picasso.with(this).load(UserPic).resize(500, 500).centerCrop().into(UserImg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.tvtool);
        toolTitle.setText("Edit Travel of " + getKey);
        setSupportActionBar(toolbar);
        ImageView back = (ImageView) findViewById(R.id.backBtn);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditTravelActivity.this, TravelActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        mReference.child(getKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placeName = (String) dataSnapshot.child("placeName").getValue();
                editPlaceTitle = (EditText) findViewById(R.id.placeTitle);
                editPlaceTitle.setText(placeName);
                startDate = (String) dataSnapshot.child("startDate").getValue();
                Button editStartingDate = (Button) findViewById(R.id.startingDate);
                editStartingDate.setText("Travel Starts on:  " + startDate);
                endDate = (String) dataSnapshot.child("endDate").getValue();
                Button editEndingDate = (Button) findViewById(R.id.endingDate);
                editEndingDate.setText("Travel Ends on:    " + endDate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater blowup = getMenuInflater();
        blowup.inflate(R.menu.add_edit_menu, menu);
        MenuItem register = menu.findItem(R.id.delete);
        register.setVisible(true);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                editPlaceTitle = (EditText) findViewById(R.id.placeTitle);
                Place = editPlaceTitle.getText().toString();
                if (!TextUtils.isEmpty(Place) && !TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
                    mReference.child(getKey).removeValue();
                    mReference.child(Place).child("endDate").setValue(startDate);
                    mReference.child(Place).child("startDate").setValue(endDate);
                    mReference.child(Place).child("placeName").setValue(Place);
                    Intent i = new Intent(EditTravelActivity.this, TravelActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    Toast.makeText(EditTravelActivity.this, "Please Fill all Fields!", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.cancel:
                Toast.makeText(EditTravelActivity.this, "Update Canceled by " + mUsername, Toast.LENGTH_SHORT).show();
                finish();
                return true;
            case R.id.delete:
                mReference.child(getKey).removeValue();
                startActivity(new Intent(EditTravelActivity.this, TravelActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onBackPressed() {
        finish();
    }

    public static class FromDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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
            startDate = String.valueOf(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append(" "));
            fromDate(startDate);
        }
    }

    public static class ToDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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
            endDate = String.valueOf(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append(" "));
            toDate(endDate);
        }
    }


    public static void fromDate(String date) {
        ST.setText("Travel Start on:  " + startDate);
    }

    public static void toDate(String date) {
        ET.setText("Travel Start on:    " + endDate);
    }
}
