package com.zaksid.dev.android.training.bignerdranch.criminalintent;

import java.util.UUID;

public class Crime {
    private UUID id;
    private String title;

    public Crime() {
        this.id = UUID.randomUUID();
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
}
