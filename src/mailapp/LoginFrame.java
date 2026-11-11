package mailapp;

import javax.swing.*;
import java.awt.*;

/** Login and registration window. */
public class LoginFrame extends JFrame {
    private final MailService service;
    private final JTextField identity = new JTextField(22);
    private final JPasswordField password = new JPasswordField(22);

    public LoginFrame(MailService service) {
        this.service = service;
        setTitle("Mail Management System | Sign in"); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(470,330); setLocationRelativeTo(null); setResizable(false);
        JPanel root = new JPanel(new GridBagLayout()); root.setBackground(new Color(242,246,252));
        JPanel card = new JPanel(new GridBagLayout()); card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,228,239)),BorderFactory.createEmptyBorder(24,30,24,30)));
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(7,5,7,5); c.fill=GridBagConstraints.HORIZONTAL;
        JLabel title = new JLabel("Welcome to MailDesk",SwingConstants.CENTER); title.setFont(new Font("SansSerif",Font.BOLD,23)); title.setForeground(new Color(37,75,125));
        c.gridx=0;c.gridy=0;c.gridwidth=2;card.add(title,c);
        JLabel subtitle = new JLabel("Sign in to manage your messages",SwingConstants.CENTER); subtitle.setForeground(Color.GRAY); c.gridy++;card.add(subtitle,c);
        c.gridwidth=1;c.gridy++;c.gridx=0;card.add(new JLabel("Username / Email"),c);c.gridx=1;card.add(identity,c);
        c.gridy++;c.gridx=0;card.add(new JLabel("Password"),c);c.gridx=1;card.add(password,c);
        JButton login = new JButton("Login"), register = new JButton("Create account");
        login.setBackground(new Color(43,105,180));login.setForeground(Color.WHITE);login.setFocusPainted(false);
        c.gridy++;c.gridx=0;card.add(login,c);c.gridx=1;card.add(register,c);
        root.add(card);setContentPane(root);
        login.addActionListener(e -> doLogin()); register.addActionListener(e -> doRegister());
        getRootPane().setDefaultButton(login);
    }
    private void doLogin() {
        User user=service.login(identity.getText(),new String(password.getPassword()));
        if(user==null){JOptionPane.showMessageDialog(this,"Incorrect username/email or password.","Login failed",JOptionPane.ERROR_MESSAGE);return;}
        new Dashboard(service,user).setVisible(true);dispose();
    }
    private void doRegister() {
        JTextField name=new JTextField(), email=new JTextField();JPasswordField pass=new JPasswordField();
        Object[] fields={"Username:",name,"Email address:",email,"Password (minimum 4 characters):",pass};
        int result=JOptionPane.showConfirmDialog(this,fields,"Create account",JOptionPane.OK_CANCEL_OPTION);
        if(result==JOptionPane.OK_OPTION){
            try{service.register(name.getText(),email.getText(),new String(pass.getPassword()));
                JOptionPane.showMessageDialog(this,"Account created. You can now log in.");
            }catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Registration failed",JOptionPane.ERROR_MESSAGE);}
        }
    }
}
