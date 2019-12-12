package com.hitenter.chataround;


import java.util.Map;




//TODO Pt4 -1 : Give Message Model Class
public class InstantMessage {


    public static final String MSG_SENT = "MSG_SENT";
    public static final String MSG_RECEIVED = "MSG_RECEIVED";

    String message;
    String author;
    String messageType;
    String time;


    InstantMessage(String message, String author, String messageType, String time) {
        this.message = message;
        this.author = author;
        this.messageType = messageType;
        this.time = time;

    }

    public InstantMessage() {



    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

   public String getAuthor() {
        return author;
    }


    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
