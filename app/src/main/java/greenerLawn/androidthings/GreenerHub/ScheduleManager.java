package greenerLawn.androidthings.GreenerHub;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by JC on 2017-12-09.
 */

public class ScheduleManager extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_start_activity);

        //Initiate text Views
        TextView zoneNameTV = (TextView) findViewById(R.id.zoneTextView);
        TextView startTimeTV = (TextView) findViewById(R.id.startedTextView);
        TextView endTimeTV = (TextView) findViewById(R.id.endTimeTextView);
        final Button countDownTV = (Button) findViewById(R.id.finishButton);
        Bundle bundle = getIntent().getExtras();

        String zoneId = bundle.getString("zoneID");
        long startTime = bundle.getLong("startTimer", 0);
        long endTime = bundle.getLong("endTimer", 0);
        boolean onOff = bundle.getBoolean("zOnOff");

        if (zoneId == null) {
            zoneId = "-L-tzs9RXl3icYcvJ2t0";
        }

        zoneNameTV.append(bundle.getString("zoneName"));
        startTimeTV.append(""+startTime);
        endTimeTV.append(""+endTime);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //@TODO Once greener hub is static use the device id insstead of hard code
        DatabaseReference zoneRef = database.getReference("greennerHubs/pi3/zones").child(zoneId);
        updateZone(zoneRef, onOff);

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownTV.setText("Ok: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                finish();
            }
        }.start();

    }

    public void accept(View view) {
        finish();
    }

    private void updateZone(DatabaseReference ref, boolean onOff){ref.child("zOnOff").setValue(onOff);}
}
