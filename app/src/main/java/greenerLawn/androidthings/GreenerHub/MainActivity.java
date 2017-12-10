package greenerLawn.androidthings.GreenerHub;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.widget.Switch;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String deviceID = "pi1";
    private static final String serial = "pi1Password";
    private static final int availZones = 8;
    private static List<Zone> zoneList = new ArrayList<Zone>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference deviceDBRef;
    private final List<String> LEDS= new ArrayList<String>(Arrays.asList("BCM4", "BCM17", "BCM27", "BCM22", "BCM12", "BCM23", "BCM24", "BCM25"));
    List <Gpio> LedGPIO;
    private Handler mHandler = new Handler();
    private Gpio mLedGpio4,mLedGpio17,mLedGpio27,mLedGpio22,mLedGpio12,mLedGpio23,mLedGpio24,mLedGpio25;
    private Switch switch1, switch2, switch3, switch4, switch5, switch6, switch7, switch8;
    private List<Switch> switchList;
    private GreenHub hub = new GreenHub(serial, availZones);
    private Schedules mCurrentSchedule = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGPIO();
        zoneSetup();
        setContentView(R.layout.activity_main);
//        if (isNetworkAvailable()){
//            Log.e(TAG, "onCreate: Connected");
//            Intent wifi = new Intent(this, WiFi_activity.class);
//            startActivity(wifi);
//        }
        setSwitches();

        // SETUP DB

        DatabaseReference myRef = database.getReference("greennerHubs").child(deviceID);
        eventHandler(myRef);


        // TODO activity to connect to wifi

        // TODO authenticate PI to restore firebase security

        // TODO update pie on boot or resume previous programming

        // TODO setting scheduling up and changes
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        setFirstSchedule(currentDayOfWeek);
    }

    private void setSwitches() {
        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);
        switch4 = (Switch) findViewById(R.id.switch4);
        switch5 = (Switch) findViewById(R.id.switch5);
        switch6 = (Switch) findViewById(R.id.switch6);
        switch7 = (Switch) findViewById(R.id.switch7);
        switch8 = (Switch) findViewById(R.id.switch8);

    }

    private void setupGPIO() {
        try{
            PeripheralManagerService service = new PeripheralManagerService();
            mLedGpio4 = service.openGpio(LEDS.get(0));
            mLedGpio17  = service.openGpio(LEDS.get(1));
            mLedGpio27  = service.openGpio(LEDS.get(2));
            mLedGpio22 = service.openGpio(LEDS.get(3));
            mLedGpio12 = service.openGpio(LEDS.get(4));
            mLedGpio23 = service.openGpio(LEDS.get(5));
            mLedGpio24 = service.openGpio(LEDS.get(6));
            mLedGpio25 = service.openGpio(LEDS.get(7));
            mLedGpio4.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio17.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio27.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio22.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio12.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio23.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio24.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio25.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }


    private void zoneSetup() {
        for (int i = 0; i<availZones; i++){
            zoneList.add(new Zone());
            zoneList.get(i).setZoneNumber("" + (i+1) );
        }
    }

    private void eventHandler(final DatabaseReference myRef) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //iterate
                if (!dataSnapshot.exists()){
                    // SETUP THE DEVICE FOR THE FIRST TIME
                    Log.e("SOMETHING", "onDataChange: DEVICE DOESN'T EXIST");
                    myRef.setValue(hub);
                    for (Zone zone: zoneList){
                        String key = myRef.child("zones").push().getKey();
                        zone.setZoneId(key);
                        myRef.child("zones").child(key).setValue(zone);
                    }
                    hub.setZoneList(zoneList);
                }
                hub = dataSnapshot.getValue(GreenHub.class);
                Log.e("PORTS", "HUBPORTS: " + hub.getPorts());
                myRef.child("zones").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d("BROADCASTRECIEVED", "onReceive: Setting up reciever part 1");
                        String port = dataSnapshot.getValue(Zone.class).getZoneNumber();
                        boolean iszOnOff= dataSnapshot.getValue(Zone.class).iszOnOff();

                        toggleLED(port, iszOnOff );
                        if(!iszOnOff){
                     //       setNextSchedule(mCurrentSchedule);
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        myRef.child("schedules").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                Schedules temp = dataSnapshot.getValue(Schedules.class);
//                if(mCurrentSchedule == null) {
//                    mCurrentSchedule = temp;
//                } else if (mCurrentSchedule.getDay() == temp.getDay()){}
                setFirstSchedule(Calendar.DAY_OF_WEEK);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                String zoneGUID = dataSnapshot.getValue(Schedules.class).getzGUID();
//                setNextSchedule(zoneGUID);
                setFirstSchedule(Calendar.DAY_OF_WEEK);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                setFirstSchedule(Calendar.DAY_OF_WEEK);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setNextSchedule(Schedules currentSchedule){

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, SprinklerReceiver.class);
        intent.setAction("greenerLawn.androidthings.GreenerHub");
        intent.putExtra("zoneId", currentSchedule.getzGUID());
        intent.putExtra("duration",  currentSchedule.getDuration());
        intent.putExtra("startTime", currentSchedule.getStartTime());
        intent.putExtra("endTime", currentSchedule.getEndTime());
        intent.putExtra("zoneOn", true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        //Cancel any alarms that are happening right now.
        alarmManager.cancel(pendingIntent);

        long currentTime = System.currentTimeMillis();
        long oneMinute = 30 * 1000; //second not min
        Log.d("BROADCASTRECIEVED", "onReceive: Setting up reciever");
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                currentTime + oneMinute,
                pendingIntent);
    }

    private void setFirstSchedule(final int currentDayOfWeek){
        DatabaseReference scheduleRef = database.getReference("greennerHubs/"+deviceID+"/schedules");

        Query q = scheduleRef.orderByChild("day").equalTo(currentDayOfWeek);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    boolean changed = false;
                    for(DataSnapshot schedule : dataSnapshot.getChildren()){
                        Schedules temp = schedule.getValue(Schedules.class);
                        if(mCurrentSchedule == null) {
                            mCurrentSchedule = temp;
                            changed = true;
                        } else if (temp.getStartTime() < mCurrentSchedule.getStartTime()) {
                            mCurrentSchedule = temp;
                            changed = true;
                        }
                    }
                    if(changed){
                        setNextSchedule(mCurrentSchedule);
                    }
                }  else {
                    int nextDayOfWeek;
                    if(currentDayOfWeek == Calendar.SATURDAY) {
                        nextDayOfWeek = Calendar.SUNDAY;
                    }
                    //@TODO this is gonna cause problems if no schedule, need to come up with better solution
                    nextDayOfWeek = currentDayOfWeek +1;
                    setFirstSchedule(nextDayOfWeek);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void toggleLED(String zoneNumber, boolean ledStateToSet) {
        LedGPIO = new ArrayList<Gpio>(Arrays.asList( mLedGpio4, mLedGpio17,mLedGpio27,mLedGpio22,mLedGpio12,mLedGpio23,mLedGpio24,mLedGpio25));
        switchList = new ArrayList<>(Arrays.asList(switch1, switch2, switch3, switch4, switch5, switch6, switch7, switch8));
        LedGPIO.get((Integer.parseInt(zoneNumber))-1);
        switchList.get((Integer.parseInt(zoneNumber))-1).setChecked(ledStateToSet);
        Log.e(TAG, "ZONES: " + zoneNumber + " is " + ledStateToSet);
        if (LedGPIO.get((Integer.parseInt(zoneNumber))-1) == null) {
            Log.e(TAG, "LED VALUE IS NULL FOR " + zoneNumber);
            return;
        }
        try {
            // Toggle the GPIO state
            LedGPIO.get((Integer.parseInt(zoneNumber))-1).setValue(ledStateToSet);
            Log.e(TAG, "State set to " + LedGPIO.get((Integer.parseInt(zoneNumber))-1).getValue());
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mHandler.removeCallbacks(mBlinkRunnable);
//        Log.i(TAG, "Closing LED GPIO pin");
        for (int i = 0; i < LedGPIO.size(); i++) {
            try {
                LedGPIO.get(i).close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            } finally {
                for (int j = 0; j < LedGPIO.size(); j++) {
                    try {
                        LedGPIO.get(i).setValue(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}