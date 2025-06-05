package com.example.securitychat.model;

import java.util.List;

public class Room {
    private List<String> participants; // size == 2
    private long createdAt;

    public Room(){}     // Firestore
    public Room(List<String> participants, long createdAt) {
        this.participants = participants;
        this.createdAt = createdAt;
    }
    public List<String> getParticipants(){ return participants; }
    public long getCreatedAt(){ return createdAt; }
}
