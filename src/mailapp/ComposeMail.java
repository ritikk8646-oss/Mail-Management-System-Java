package mailapp;

import javax.swing.*;
import java.awt.*;

/** Dialog used to compose a new message or edit an existing draft. */
public class ComposeMail extends JDialog {
    private final MailService service;
    private final User user;
    private final Email existingDraft;
    private final JTextField recipient = new JTextField(30);
    private final JTextField subject = new JTextField(30);
    private final JTextArea message = new JTextArea(12, 30);
    private final Runnable refresh;

    public ComposeMail(JFrame owner, MailService service, User user, Email draft, Runnable refresh) {
        super(owner, draft == null ? "Compose Mail" : "Edit Draft", true);
        this.service = service; this.user = user; this.existingDraft = draft; this.refresh = refresh;
        setSize(560, 470); setLocationRelativeTo(owner); setLayout(new BorderLayout(12, 12));
        JPanel fields = new JPanel(new GridBagLayout()); fields.setBorder(BorderFactory.createEmptyBorder(16, 16, 4, 16));
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6, 4, 6, 4); c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.gridy=0; fields.add(new JLabel("To (registered email):"), c); c.gridx=1; c.weightx=1; fields.add(recipient,c);
        c.gridx=0; c.gridy=1; c.weightx=0; fields.add(new JLabel("Subject:"),c); c.gridx=1; c.weightx=1; fields.add(subject,c);
        add(fields, BorderLayout.NORTH);
        message.setLineWrap(true); message.setWrapStyleWord(true); message.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(message); scroll.setBorder(BorderFactory.createTitledBorder("Message"));
        JPanel center = new JPanel(new BorderLayout()); center.setBorder(BorderFactory.createEmptyBorder(0,16,8,16)); center.add(scroll); add(center,BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Save Draft"), send = new JButton("Send Mail"), cancel = new JButton("Cancel");
        buttons.add(cancel); buttons.add(save); buttons.add(send); add(buttons,BorderLayout.SOUTH);
        if (draft != null) { recipient.setText(draft.getRecipient()); subject.setText(draft.getSubject()); message.setText(draft.getBody()); }
        save.addActionListener(e -> saveDraft()); send.addActionListener(e -> sendMail()); cancel.addActionListener(e -> dispose());
    }
    private void saveDraft() {
        try {
            service.saveDraft(user.getEmailAddress(), recipient.getText(), subject.getText(), message.getText(),
                    existingDraft == null ? null : existingDraft.getId());
            JOptionPane.showMessageDialog(this,"Draft saved successfully."); refresh.run(); dispose();
        } catch (Exception ex) { showError(ex); }
    }
    private void sendMail() {
        try {
            if (existingDraft == null) service.send(user.getEmailAddress(),recipient.getText(),subject.getText(),message.getText());
            else {
                service.saveDraft(user.getEmailAddress(), recipient.getText(), subject.getText(), message.getText(), existingDraft.getId());
                service.sendDraft(existingDraft);
            }
            JOptionPane.showMessageDialog(this,"Mail sent successfully!"); refresh.run(); dispose();
        } catch (Exception ex) { showError(ex); }
    }
    private void showError(Exception ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Unable to continue",JOptionPane.ERROR_MESSAGE); }
}
