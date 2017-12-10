package greenerLawn.androidthings.GreenerHub;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.ALARM_SERVICE;


public class SprinklerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        if(extras != null) {
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String zoneId = extras.getString("zoneId");
            boolean zOnOff = extras.getBoolean("zoneOn");
            Log.d("BROADCASTRECIEVED", "onReceive For: " +zoneId);

            //Build the intent for saving a schedule
            Intent i = new Intent(context.getApplicationContext(), ScheduleManager.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (zOnOff) {
                Log.d("BROADCASTRECIEVED", "zone on is true");
                i.putExtra("zOnOff", true);
                setNextSchedule(zoneId, context);
            } else {
                Log.d("BROADCASTRECIEVED", "Ending Schedule");
                i.putExtra("zOnOff", false);
            }
            i.putExtra("zoneName", "1");
            i.putExtra("endTime", "1:00");
            i.putExtra("startTime", "10:00");
            i.putExtra("zoneID", zoneId);
            context.getApplicationContext().startActivity(i);
        }


    }

    private void setNextSchedule(String zoneGUID, Context context){
        long dur = 1000;
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), SprinklerReceiver.class);
        intent.setAction("greenerLawn.androidthings.GreenerHub");
        intent.putExtra("zoneId", zoneGUID);
        intent.putExtra("duration",  dur);
        intent.putExtra("zoneOn", false);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, 0);

        long currentTime = System.currentTimeMillis();
        long oneMinute = 120 * 100; //second not min
        Log.d("BROADCASTRECIEVED", "onReceive: Setting up reciever");
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                currentTime + oneMinute,
                pendingIntent);
    }
}
