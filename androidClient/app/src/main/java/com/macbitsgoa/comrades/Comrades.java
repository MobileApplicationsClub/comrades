package com.macbitsgoa.comrades;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;
import com.macbitsgoa.comrades.persistance.Database;

import static com.macbitsgoa.comrades.HomeActivity.SETTINGS;

/**
 * @author aayush
 */

public class Comrades extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Comrades.context = getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Boolean coursesPresent = preferences.getBoolean("Courses Present", false);
        if (!coursesPresent) {

        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference(BuildConfig.BUILD_TYPE).child("courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FetchAllCourses fetchAllCourses = new FetchAllCourses();
                        fetchAllCourses.execute(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public static Context getAppContext() {
        return context;
    }


    private static class FetchAllCourses extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... params) {
            DataSnapshot dataSnapshot = params[0];
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                MyCourse myCourse = snapshot.getValue(MyCourse.class);
                myCourse.setFollowing(false);
                Database.getInstance(getAppContext()).getCourseDao().insert(myCourse);
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(SETTINGS, true);
            edit.putBoolean("Courses Present", Boolean.TRUE);
            edit.apply();
            return null;
        }
    }
}
