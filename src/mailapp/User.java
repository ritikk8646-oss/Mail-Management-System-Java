package mailapp;

import java.io.Serializable;

/** Represents a registered application user. */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String emailAddress;
    private String passwordHash;

    public User(String username, String emailAddress, String passwordHash) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.passwordHash = passwordHash;
    }
    public String getUsername() { return username; }
    public String getEmailAddress() { return emailAddress; }
    public String getPasswordHash() { return passwordHash; }
    public void setUsername(String username) { this.username = username; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
