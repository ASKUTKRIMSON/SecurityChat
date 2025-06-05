package com.example.securitychat.model;
public class Message {
    private String text, senderUid; private long timestamp;
    public Message(){}
    public Message(String text,String senderUid,long t){
        this.text=text; this.senderUid=senderUid; this.timestamp=t;
    }
    public String getText(){return text;}        public void setText(String t){text=t;}
    public String getSenderUid(){return senderUid;}
    public long getTimestamp(){return timestamp;}
}
