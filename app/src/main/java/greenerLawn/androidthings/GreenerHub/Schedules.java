package greenerLawn.androidthings.GreenerHub;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by JC on 2017-11-04.
 */

// todo allow for IDFC setting
//need to figure out flag settings for tempSuspend
public class Schedules {

    private String schGUID;
    String tempGuid = "";

    //0 - 7, 0 = Sunday, 1 = Monday... 7 = saturday
    private int day;
    private Long startTime, duration, endTime;
    private String zGUID;

    //todo remove is from names
    private boolean valid, repeat, suspended, pausedByIDC;
    private final boolean VALID_AT_CREATE = true;
    private final boolean SUSPEND_AT_CREATE = true;

    //so what am i using my flags for
    // valid is a running schedule
    //suspended is a paused schedule
    //repeat is the repeat flag


    // needs to be extracted to a manager class



    //todo firebase updates
    //todo add cascade enforcement


    public Schedules() {
    }

    public Schedules(String schGUID, int day, Long startTime, Long duration, Long endTime, String zGUID, boolean isRepeat, boolean suspended, boolean valid, boolean pausedByIDC) {
        this.schGUID = schGUID;
        this.day = day;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = endTime;
        this.zGUID = zGUID;
        this.repeat = isRepeat;
        this.suspended = suspended;
        this.valid = valid;
        this.pausedByIDC = pausedByIDC;
    }

    private List<Schedules> schedulesList = new ArrayList<Schedules>();

    public void addSchedule(int day, Long startTime, Long duration, String zGUID, boolean repeat) {
        // create new schedule item
        Schedules newSched  = new Schedules("",day, startTime, duration, null, zGUID, repeat, SUSPEND_AT_CREATE, VALID_AT_CREATE, pausedByIDC);
        newSched.endTime = newSched.calcEndTime(newSched.startTime, newSched.duration);

        //check to see that new schedule can be made
        verifyValid(newSched);

        if(newSched.valid){
            schedulesList.add(newSched);
        }else{
            //todo error handling
        }

    }

    private void verifyValid(Schedules newSched) {
        //iterate over list
        for (int i = 0; i < schedulesList.size(); i++) {
            Schedules tempCheck = schedulesList.get(i);
            // check running schedules for conflicts
            // should check against all schedules
            // case
            // pause sched A
            //create sched B which conflicts with sched A
            //resume sched A and problems
            if (newSched.day == tempCheck.getDay()) {
                enforceTime(newSched, tempCheck);
            }
        }
    }


    private void enforceTime(Schedules newSched, Schedules tempCheck) {
        if ((tempCheck.getStartTime()< newSched.getStartTime() && newSched.getStartTime() < tempCheck.getEndTime())
                || (tempCheck.getStartTime()> newSched.getStartTime() && newSched.startTime > tempCheck.getEndTime() )){
            newSched.setValid(false);
        }
    }

    public void removeSchedule(String schGUID){
        for(int i = 0; i< schedulesList.size(); i++){
            if (schedulesList.get(i).schGUID.equals(schGUID)){
                Schedules temp = schedulesList.get(i);
                schedulesList.remove(i);
                temp = null;
            }
        }
    }

    public List<Schedules> getSchedulesList() {
        return schedulesList;
    }

    public void setSchedulesList(List<Schedules> schedulesList) {
        this.schedulesList = schedulesList;
    }

    public String getSchGUID() {
        return schGUID;
    }

    public void setSchGUID(String schGUID) {
        this.schGUID = schGUID;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getEndTime() {
        return endTime;
    }

    public Long calcEndTime(Long startTime, Long duration){
        Long cEndTime = startTime + duration;

        return cEndTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getzGUID() {
        return zGUID;
    }

    public void setzGUID(String zGUID) {
        this.zGUID = zGUID;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        repeat = repeat;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isPausedByIDC() {
        return pausedByIDC;
    }

    public void setPausedByIDC(boolean pausedByIDC) {
        this.pausedByIDC = pausedByIDC;
    }
}
