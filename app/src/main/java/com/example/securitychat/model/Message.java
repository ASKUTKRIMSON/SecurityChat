// app/src/main/java/com/example/securitychat/model/Message.java
package com.example.securitychat.model;

/**
 * Простая модель сообщения.
 * Должен иметь пустой конструктор для Firestore.
 */
public class Message {
    private String text;
    private String senderUid;
    private long timestamp;

    public Message() {
        // Пустой конструктор необходим для Firestore
    }

    public Message(String text, String senderUid, long timestamp) {
        this.text = text;
        this.senderUid = senderUid;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
