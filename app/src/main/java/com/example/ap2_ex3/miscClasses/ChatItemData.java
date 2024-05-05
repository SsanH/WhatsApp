package com.example.ap2_ex3.miscClasses;

public class ChatItemData {
    private String username;
    private String profilePic;
    private String displayName;
    private String lastMessageContent;
    private String lastMessageCreated;


    public ChatItemData(String username, String profilePic, String displayName, String lastMessageContent, String lastMessageCreated) {
        this.username = username;
        this.profilePic = profilePic;
        this.displayName = displayName;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageCreated = lastMessageCreated;
    }

    public String getUsername() {return this.username;}

    public String getProfilePic() {
        return profilePic;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public String getLastMessageCreated() {
        return lastMessageCreated;
    }

    public void setLastMessageContent(String created) {
        this.lastMessageCreated = created;
    }

    public void setLastMessageCreated(String content) {
        this.lastMessageContent = content;
    }
}
