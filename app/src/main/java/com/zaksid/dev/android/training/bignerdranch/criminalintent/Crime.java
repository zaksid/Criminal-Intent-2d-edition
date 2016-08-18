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
 * </ul>
 *
 */
public class Crime {
    private UUID id;
    private String title;
    private Date date;
    private boolean isSolved;

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
}
