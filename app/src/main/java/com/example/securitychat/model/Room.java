package com.example.securitychat.model;
import java.util.List;
public class Room {
    private List<String> participants; private long createdAt;
    public Room(){}
    public Room(List<String> p,long t){participants=p;createdAt=t;}
    public List<String> getParticipants(){return participants;}
    public long getCreatedAt(){return createdAt;}
}
