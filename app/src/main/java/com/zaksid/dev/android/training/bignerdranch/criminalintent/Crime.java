package com.zaksid.dev.android.training.bignerdranch.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Model of Crime.
 * Contains following fields:
 * <ul>
 *     <li>id - unique crime's identifier</li>
 *     <li>title - name of crime</li>
 *     <li>date - when crime happened</li>
 *     <li>isSolved - is crime solved or not</li>
 *     <li>suspect - name of suspect in crime person (to be chosen from Contacts)</li>
 *     <li>suspectPhoneNumber - phone number of suspect</li>
 * </ul>
 *
 */
public class Crime {
    private UUID id;
    private String title;
    private Date date;
    private boolean isSolved;
    private String suspect;
    private String suspectPhoneNumber;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        this.id = id;
        date = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    public String getSuspectPhoneNumber() {
        return suspectPhoneNumber;
    }

    public void setSuspectPhoneNumber(String suspectPhone) {
        this.suspectPhoneNumber = suspectPhone;
    }
}
