package greenerLawn.androidthings.GreenerHub;

import android.app.Activity;
import android.content.Context;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Switch;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String deviceID = "pi1";
    private static final String serial = "pi1Password";
    private static final int availZones = 8;
    private static List<Zone> zoneList = new ArrayList<Zone>();
    private DatabaseReference deviceDBRef;
    private final List<String> LEDS= new ArrayList<String>(Arrays.asList("BCM4", "BCM17", "BCM27", "BCM22", "BCM12", "BCM23", "BCM24", "BCM25"));
    List <Gpio> LedGPIO;
    private Handler mHandler = new Handler();
    private Gpio mLedGpio4,mLedGpio17,mLedGpio27,mLedGpio22,mLedGpio12,mLedGpio23,mLedGpio24,mLedGpio25;
    private Switch switch1, switch2, switch3, switch4, switch5, switch6, switch7, switch8;
    private List<Switch> switchList;
    private GreenHub hub = new GreenHub(serial, availZones);;
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("greennerHubs");
        eventHandler(myRef);


        // TODO activity to connect to wifi

        // TODO authenticate PI to restore firebase security

        // TODO update pie on boot or resume previous programming

        // TODO setting scheduling up and changes


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
                if (dataSnapshot.child(deviceID).exists()){
                    deviceDBRef = myRef.child(deviceID);
                    hub = dataSnapshot.child(deviceID).getValue(GreenHub.class);
                    Log.e("PORTS", "HUBPORTS: " + hub.getPorts());
                    deviceDBRef.child("zones").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            String port = dataSnapshot.getValue(Zone.class).getZoneNumber();
                            boolean iszOnOff= dataSnapshot.getValue(Zone.class).iszOnOff();
                            toggleLED(port, iszOnOff );

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    // SETUP THE DEVICE FOR THE FIRST TIME
                    Log.e("SOMETHING", "onDataChange: DEVICE DOESN'T EXIST");
                    deviceDBRef = myRef.child(deviceID);
                    deviceDBRef.setValue(hub);
                    for (Zone zone: zoneList){
                        deviceDBRef.child("zones").push().setValue(zone);
                    }
                    hub.setZoneList(zoneList);
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