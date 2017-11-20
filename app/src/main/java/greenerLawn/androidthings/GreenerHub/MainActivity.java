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

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;
    private static final String LED = "BCM22";
    private static final String deviceID = "pi1";
    private static final String serial = "pi1Password";
    private static final int availZones = 8;
    private static List<Zone> zoneList = new ArrayList<Zone>();
    private DatabaseReference deviceDBRef;
    private final List<String> LEDS= new ArrayList<String>(Arrays.asList("BCM4", "BCM17", "BCM27", "BCM22", "BCM12", "BCM23", "BCM24", "BCM25"));

    private Handler mHandler = new Handler();
     private Gpio mLedGpio4, mLedGpio17,mLedGpio27,mLedGpio22,mLedGpio12,mLedGpio23,mLedGpio24,mLedGpio25;
    private List <Gpio> LedGPIO = new ArrayList<Gpio>(Arrays.asList( mLedGpio4, mLedGpio17,mLedGpio27,mLedGpio22,mLedGpio12,mLedGpio23,mLedGpio24,mLedGpio25));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (isNetworkAvailable()){
//            Log.e(TAG, "onCreate: Connected");
//            Intent wifi = new Intent(this, WiFi_activity.class);
//            startActivity(wifi);
//        }

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("greennerHubs");
        zoneSetup();
        eventHandler(myRef);

        // TODO setup listeners and tie to led

        // TODO activity to connect to wifi

        // TODO SETUP GPIO
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

    private void setupGPIO(Gpio gpio, String ledPort) {


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
                    deviceDBRef.child("zones").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            String port = dataSnapshot.getValue(Zone.class).getZoneNumber();
                            boolean iszOnOff= dataSnapshot.getValue(Zone.class).iszOnOff();
//                            Log.e(TAG, "ZONES: " + zone.getPort() + " is " + zone.iszOnOff());
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
                    Log.e("SOMETHING", "onDataChange: DEVICE DOESN'T EXIST");
                    deviceDBRef = myRef.child(deviceID);
                    deviceDBRef.child("serial").setValue(serial);
                    for (Zone zone: zoneList){
                        deviceDBRef.child("zones").push().setValue(zone);
                        Log.e("PUSHED", "onDataChange: " + zone.toString());

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void toggleLED(String zoneNumber, boolean ledStateToSet) {
        Log.e(TAG, "ZONES: " + zoneNumber + " is " + ledStateToSet);
        if (mLedGpio4 == null) {
            Log.e(TAG, "didn't go in");
            return;
        }
        try {
            // Toggle the GPIO state
            mLedGpio4.setValue(ledStateToSet);
            Log.e(TAG, "State set to " + mLedGpio4.getValue());
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }



//    private Runnable mBlinkRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (mLedGpio == null) {
//                return;
//            }
//            try {
//                // Toggle the GPIO state
//                mLedGpio.setValue(!mLedGpio.getValue());
//                Log.d(TAG, "State set to " + mLedGpio.getValue());
//                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
//            } catch (IOException e) {
//                Log.e(TAG, "Error on PeripheralIO API", e);
//            }
//        }
//    };
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