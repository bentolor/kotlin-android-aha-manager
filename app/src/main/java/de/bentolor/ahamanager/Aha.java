package de.bentolor.ahamanager;

import java.util.Date;
import java.util.UUID;

public class Aha {
    private final UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mUseful;

    public Aha() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Aha(UUID mId, String mTitle, Date mDate, boolean mUseful) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mUseful = mUseful;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isUseful() {
        return mUseful;
    }

    public void setUseful(boolean useful) {
        mUseful = useful;
    }

    public UUID getId() {
        return mId;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
