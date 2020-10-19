package com.example.myutil.MusicUtil;


public class MusicSendObject {
    int tempo;
    Yin[] notes;

    public MusicSendObject(int tempo, Yin[] yins) {
        this.tempo = tempo;
        notes = yins;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public Yin[] getNotes() {
        return notes;
    }

    public void setNotes(Yin[] notes) {
        this.notes = notes;
    }
}

