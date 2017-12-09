package greenerLawn.androidthings.GreenerHub;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SprinklerReceiver extends BroadcastReceiver {


    private DatabaseReference zoneRef ;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BROADCASTRECIEVED", "onReceive: FUCK THIS SHIT");
        Bundle extras = intent.getExtras();
        if(extras != null) {
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String zoneId = extras.getString("zoneId");
            Toast.makeText(context, zoneId, Toast.LENGTH_LONG).show();
//            Long duration = extras.getLong("duration", 0);
//
//
//            zoneRef = database.getReference("greennerHubs/pi3/zones").child(zoneId);
//            zoneRef.child("zOnOff").setValue("True");
        }


    }
}
