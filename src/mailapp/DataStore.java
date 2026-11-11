package mailapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Serializable container for all application data. */
public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<User> users = new ArrayList<>();
    private List<Email> emails = new ArrayList<>();
    public List<User> getUsers() { return users; }
    public List<Email> getEmails() { return emails; }
}
