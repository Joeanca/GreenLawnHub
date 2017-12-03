package greenerLawn.androidthings.GreenerHub;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SprinklerReceiver extends BroadcastReceiver {


    private DatabaseReference zoneRef ;
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String zoneId = intent.getStringExtra("zoneID");
        Long duration = intent.getLongExtra("duration", 0);

        zoneRef = database.getReference("greennerHubs/pi1/zones").child(zoneId);
        zoneRef.child("zOnOff").setValue("True");

    }
}
