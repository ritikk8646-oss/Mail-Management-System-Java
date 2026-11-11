package mailapp;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Represents a message exchanged between two registered users. */
public class Email implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private LocalDateTime date;
    private boolean read;
    private boolean draft;
    private boolean senderDeleted;
    private boolean recipientDeleted;

    public Email(String id, String sender, String recipient, String subject, String body,
                 LocalDateTime date, boolean read, boolean draft) {
        this.id = id; this.sender = sender; this.recipient = recipient;
        this.subject = subject; this.body = body; this.date = date;
        this.read = read; this.draft = draft;
    }
    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public LocalDateTime getDate() { return date; }
    public boolean isRead() { return read; }
    public boolean isDraft() { return draft; }
    public boolean isSenderDeleted() { return senderDeleted; }
    public boolean isRecipientDeleted() { return recipientDeleted; }
    public void setSender(String sender) { this.sender = sender; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setBody(String body) { this.body = body; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setRead(boolean read) { this.read = read; }
    public void setDraft(boolean draft) { this.draft = draft; }
    public void setSenderDeleted(boolean deleted) { senderDeleted = deleted; }
    public void setRecipientDeleted(boolean deleted) { recipientDeleted = deleted; }
}
