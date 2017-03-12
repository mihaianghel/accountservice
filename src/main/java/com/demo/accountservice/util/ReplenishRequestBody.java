package com.demo.accountservice.util;

import java.io.Serializable;
import java.util.List;

public class ReplenishRequestBody implements Serializable {

    List<Note> notes;

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}


