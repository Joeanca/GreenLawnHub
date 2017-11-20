package greenerLawn.androidthings.GreenerHub;

import java.io.File;

/**
 * Created by joeanca on 2017-11-18.
 */

public class Zone {
    //TODO multiple sprinklers
    //todo multiple devices

    private boolean zOnOff;
    private File zImage;
    private String zoneNumber;

    public Zone(){}

    //default constructor for empty zone
    //constructor allows for full zone config
    public Zone(boolean zOnOff) {
        this.zOnOff = zOnOff;
    }
    public boolean iszOnOff() {
        return zOnOff;
    }

    public void setzOnOff(boolean zOnOff) {
        // TURN ON OFF
        this.zOnOff = zOnOff;
    }

    public File getzImage() {
        return zImage;
    }

    public void setzImage(File zImage) {
        this.zImage = zImage;
    }

    public void setZoneNumber(String zoneNumber){this.zoneNumber = zoneNumber;}

    public String getZoneNumber(){return zoneNumber;}
}