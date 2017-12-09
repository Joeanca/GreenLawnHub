package greenerLawn.androidthings.GreenerHub;

/**
 * Created by joeanca on 2017-11-18.
 */

import java.util.List;

public class GreenHub {

    private String serial;
    private int ports;
    private List<Zone> zoneList;
    private List<Schedules> schedulesList;

    // EMPTY CONSTRUCTOR FOR FIREBASE
    public GreenHub() {}
    // OVERLOADED CONSTRUCTOR FOR SETTING IT UP
    public GreenHub(String serial, List<Zone> zoneList, List<Schedules> schedulesList) {
        this.serial = serial;
        this.zoneList = zoneList;
        this.schedulesList = schedulesList;
    }
    public GreenHub(String serial, int ports) {
        this.serial = serial;
        this.ports = ports;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getPorts() {
        return ports;
    }

    public void setPorts(int ports) {
        this.ports = ports;
    }

    public List<Zone> getZoneList() {
        return zoneList;
    }


    public boolean setZoneList(List<Zone> zoneList) {
        boolean valid = false;
        if(zoneList.size()<= this.getPorts()){
            //i can't let you do that dave.
            valid = true;
            this.zoneList = zoneList;
        }
        return valid;
    }

    public List<Schedules> getSchedulesList() {
        return schedulesList;
    }

    public void setSchedulesList(List<Schedules> schedulesList) {
        this.schedulesList = schedulesList;
    }
}