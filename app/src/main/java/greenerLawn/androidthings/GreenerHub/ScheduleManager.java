package greenerLawn.androidthings.GreenerHub;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
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
        final TextView countDownTV = (TextView) findViewById(R.id.countDown);
        Bundle bundle = getIntent().getExtras();

        String zoneId = bundle.getString("zoneID");
        boolean onOff = bundle.getBoolean("zOnOff");
        if (zoneId == null) {
            zoneId = "-L-tzs9RXl3icYcvJ2t0";
        }
        zoneNameTV.append(bundle.getString("zoneName"));
        startTimeTV.append(bundle.getString("startTime"));
        endTimeTV.append(bundle.getString("endTime"));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference zoneRef = database.getReference("greennerHubs/pi1/zones").child(zoneId);
        updateZone(zoneRef, onOff);

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownTV.setText("seconds remaining: " + millisUntilFinished / 1000);
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
