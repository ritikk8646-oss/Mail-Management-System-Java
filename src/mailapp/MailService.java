package mailapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

/** Application logic. Swing screens call this class instead of editing data directly. */
public class MailService {
    private final FileManager fileManager;
    private final DataStore store;

    public MailService() {
        fileManager = new FileManager();
        store = fileManager.load();
    }

    public synchronized void register(String username, String email, String password) throws Exception {
        username = username.trim(); email = email.trim().toLowerCase(Locale.ROOT);
        if (username.isEmpty() || email.isEmpty() || password.length() < 4)
            throw new IllegalArgumentException("Enter all fields. Password must contain at least 4 characters.");
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new IllegalArgumentException("Enter a valid email address.");
        for (User user : store.getUsers()) {
            if (user.getUsername().equalsIgnoreCase(username) || user.getEmailAddress().equalsIgnoreCase(email))
                throw new IllegalArgumentException("Username or email address is already registered.");
        }
        store.getUsers().add(new User(username, email, hash(password)));
        save();
    }

    public synchronized User login(String identity, String password) {
        String candidate = hash(password);
        for (User user : store.getUsers()) {
            if ((user.getUsername().equalsIgnoreCase(identity.trim()) ||
                 user.getEmailAddress().equalsIgnoreCase(identity.trim())) &&
                 user.getPasswordHash().equals(candidate)) return user;
        }
        return null;
    }

    public synchronized List<User> getUsers() { return new ArrayList<>(store.getUsers()); }

    public synchronized Email send(String sender, String recipient, String subject, String body) throws Exception {
        User receiver = findUser(recipient);
        if (receiver == null) throw new IllegalArgumentException("No registered user has that email address.");
        if (receiver.getEmailAddress().equalsIgnoreCase(sender))
            throw new IllegalArgumentException("You cannot send a message to yourself.");
        if (subject.trim().isEmpty() || body.trim().isEmpty())
            throw new IllegalArgumentException("Subject and message cannot be empty.");
        Email email = new Email(UUID.randomUUID().toString(), sender, receiver.getEmailAddress(),
                subject.trim(), body.trim(), LocalDateTime.now(), false, false);
        store.getEmails().add(email); save(); return email;
    }

    public synchronized Email saveDraft(String sender, String recipient, String subject, String body, String id) throws Exception {
        Email draft = id == null ? null : findEmail(id);
        if (draft == null) {
            draft = new Email(UUID.randomUUID().toString(), sender, recipient.trim(), subject,
                    body, LocalDateTime.now(), false, true);
            store.getEmails().add(draft);
        } else {
            draft.setRecipient(recipient.trim()); draft.setSubject(subject); draft.setBody(body);
            draft.setDate(LocalDateTime.now()); draft.setDraft(true);
        }
        save(); return draft;
    }

    public synchronized void sendDraft(Email draft) throws Exception {
        if (draft == null || !draft.isDraft()) throw new IllegalArgumentException("Draft not found.");
        User receiver = findUser(draft.getRecipient());
        if (receiver == null) throw new IllegalArgumentException("Recipient is not a registered user.");
        if (draft.getSubject().trim().isEmpty() || draft.getBody().trim().isEmpty())
            throw new IllegalArgumentException("Subject and message cannot be empty.");
        draft.setRecipient(receiver.getEmailAddress()); draft.setDraft(false);
        draft.setDate(LocalDateTime.now()); draft.setRead(false); save();
    }

    public synchronized void deleteDraft(Email draft) throws Exception { store.getEmails().remove(draft); save(); }

    public synchronized List<Email> getInbox(String user) {
        return filtered(e -> !e.isDraft() && e.getRecipient().equalsIgnoreCase(user) && !e.isRecipientDeleted());
    }
    public synchronized List<Email> getSent(String user) {
        return filtered(e -> !e.isDraft() && e.getSender().equalsIgnoreCase(user) && !e.isSenderDeleted());
    }
    public synchronized List<Email> getDrafts(String user) {
        return filtered(e -> e.isDraft() && e.getSender().equalsIgnoreCase(user));
    }
    public synchronized List<Email> getTrash(String user) {
        return filtered(e -> !e.isDraft() && ((e.getSender().equalsIgnoreCase(user) && e.isSenderDeleted()) ||
                (e.getRecipient().equalsIgnoreCase(user) && e.isRecipientDeleted())));
    }
    public synchronized List<Email> search(String user, String query, String folder) {
        String q = query.toLowerCase(Locale.ROOT);
        List<Email> source = switch (folder) {
            case "Sent" -> getSent(user); case "Drafts" -> getDrafts(user);
            case "Trash" -> getTrash(user); default -> getInbox(user);
        };
        List<Email> result = new ArrayList<>();
        for (Email e : source) {
            String other = folder.equals("Sent") ? e.getRecipient() : e.getSender();
            if (e.getSubject().toLowerCase(Locale.ROOT).contains(q) || other.toLowerCase(Locale.ROOT).contains(q)) result.add(e);
        }
        return result;
    }
    public synchronized void markRead(Email email, boolean read) throws Exception { email.setRead(read); save(); }
    public synchronized void delete(Email email, String user) throws Exception {
        if (email.getSender().equalsIgnoreCase(user)) email.setSenderDeleted(true);
        if (email.getRecipient().equalsIgnoreCase(user)) email.setRecipientDeleted(true);
        save();
    }
    public synchronized void restore(Email email, String user) throws Exception {
        if (email.getSender().equalsIgnoreCase(user)) email.setSenderDeleted(false);
        if (email.getRecipient().equalsIgnoreCase(user)) email.setRecipientDeleted(false);
        save();
    }
    public synchronized void permanentlyDelete(Email email, String user) throws Exception {
        if (email.getSender().equalsIgnoreCase(user) && email.isRecipientDeleted() ||
            email.getRecipient().equalsIgnoreCase(user) && email.isSenderDeleted()) store.getEmails().remove(email);
        else delete(email, user);
        save();
    }
    private List<Email> filtered(java.util.function.Predicate<Email> predicate) {
        List<Email> result = new ArrayList<>();
        for (Email e : store.getEmails()) if (predicate.test(e)) result.add(e);
        result.sort(Comparator.comparing(Email::getDate).reversed()); return result;
    }
    private User findUser(String email) {
        for (User u : store.getUsers()) if (u.getEmailAddress().equalsIgnoreCase(email.trim())) return u;
        return null;
    }
    private Email findEmail(String id) {
        for (Email e : store.getEmails()) if (e.getId().equals(id)) return e;
        return null;
    }
    private void save() throws IOException { fileManager.save(store); }
    private String hash(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException ex) { throw new IllegalStateException(ex); }
    }
}
