package greenerLawn.androidthings.GreenerHub;

import java.io.File;

/**
 * Created by joeanca on 2017-11-18.
 */

public class Zone {
    //TODO multiple sprinklers
    //todo multiple devices

    private boolean zOnOff;
    private String zGUID, zName;
    private File zImage;
    private String zoneNumber;

    public Zone(){};

    //default constructor for empty zone
    public Zone(String zGUID) {
        this.zGUID = zGUID;
    }
    //constructor allows for full zone config
    public Zone(String zGUID, String zName, boolean zOnOff) {
        this.zGUID = zGUID;
        this.zName = zName;
        this.zOnOff = zOnOff;
    }

    public String getzGUID() {
        return zGUID;
    }

    public void setzGUID(String zGUID) {
        this.zGUID = zGUID;
    }

    public String getzName() {
        return zName;
    }

    public void setzName(String zName) {
        this.zName = zName;
    }

    public boolean iszOnOff() {
        return zOnOff;
    }

    public void setzOnOff(boolean zOnOff) {
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