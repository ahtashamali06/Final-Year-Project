package mquinn.sign_language.Chat;

import java.util.ArrayList;

public class MessageObject {

    String messageId,message,senderId;
    ArrayList<String> mediaUrlList;

    public MessageObject(String messageId, String message, String senderId,ArrayList<String> mediaUrlList) {
        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.mediaUrlList = mediaUrlList;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }
}
