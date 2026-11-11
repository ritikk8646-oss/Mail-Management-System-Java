package mailapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Main mail dashboard with folder navigation and message actions. */
public class Dashboard extends JFrame {
    private final MailService service;
    private final User user;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> emailList = new JList<>(listModel);
    private final JTextField searchField = new JTextField(22);
    private final JLabel heading = new JLabel("Inbox");
    private String folder="Inbox";
    private List<Email> currentEmails = List.of();
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public Dashboard(MailService service, User user) {
        this.service=service;this.user=user;
        setTitle("MailDesk - "+user.getUsername());setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);setSize(1050,680);setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel sidebar=new JPanel();sidebar.setBackground(new Color(30,52,82));sidebar.setPreferredSize(new Dimension(205,0));sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));sidebar.setBorder(new EmptyBorder(22,14,18,14));
        JLabel brand=new JLabel("✉  MailDesk");brand.setForeground(Color.WHITE);brand.setFont(new Font("SansSerif",Font.BOLD,23));brand.setBorder(new EmptyBorder(0,5,24,0));sidebar.add(brand);
        JButton compose=navButton("＋  Compose Mail",true);compose.addActionListener(e->new ComposeMail(this,service,user,null,this::refresh).setVisible(true));sidebar.add(compose);sidebar.add(Box.createVerticalStrut(18));
        addNav(sidebar,"Inbox");addNav(sidebar,"Sent");addNav(sidebar,"Drafts");addNav(sidebar,"Trash");sidebar.add(Box.createVerticalGlue());
        JLabel signed=new JLabel("Signed in as\n"+user.getUsername());signed.setForeground(new Color(210,220,235));signed.setBorder(new EmptyBorder(10,5,10,5));sidebar.add(signed);
        JButton logout=navButton("↪  Logout",false);logout.addActionListener(e->{dispose();new LoginFrame(service).setVisible(true);});sidebar.add(logout);
        add(sidebar,BorderLayout.WEST);
        JPanel main=new JPanel(new BorderLayout(12,12));main.setBackground(new Color(246,248,252));main.setBorder(new EmptyBorder(22,24,20,24));
        JPanel top=new JPanel(new BorderLayout());top.setOpaque(false);heading.setFont(new Font("SansSerif",Font.BOLD,26));heading.setForeground(new Color(38,57,83));top.add(heading,BorderLayout.WEST);
        JPanel search=new JPanel(new FlowLayout(FlowLayout.RIGHT,6,0));search.setOpaque(false);JButton find=new JButton("Search");search.add(searchField);search.add(find);top.add(search,BorderLayout.EAST);main.add(top,BorderLayout.NORTH);
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);emailList.setFont(new Font("SansSerif",Font.PLAIN,14));emailList.setFixedCellHeight(58);emailList.setBorder(new EmptyBorder(8,10,8,10));
        emailList.setCellRenderer(new DefaultListCellRenderer(){@Override public Component getListCellRendererComponent(JList<?> l,Object value,int index,boolean selected,boolean focus){JLabel label=(JLabel)super.getListCellRendererComponent(l,value,index,selected,focus);label.setBorder(new EmptyBorder(8,10,8,10));if(!selected)label.setBackground(index%2==0?Color.WHITE:new Color(250,251,253));return label;}});
        JScrollPane scroll=new JScrollPane(emailList);scroll.setBorder(BorderFactory.createLineBorder(new Color(225,230,238)));main.add(scroll,BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));actions.setOpaque(false);
        JButton open=new JButton("Open / Read"), unread=new JButton("Mark Unread"), delete=new JButton("Delete"), restore=new JButton("Restore"), refresh=new JButton("Refresh");
        actions.add(open);actions.add(unread);actions.add(delete);actions.add(restore);actions.add(refresh);main.add(actions,BorderLayout.SOUTH);
        open.addActionListener(e->openSelected());unread.addActionListener(e->markUnread());delete.addActionListener(e->deleteSelected());restore.addActionListener(e->restoreSelected());refresh.addActionListener(e->refresh());find.addActionListener(e->search());searchField.addActionListener(e->search());
        emailList.addListSelectionListener(e->{if(!e.getValueIsAdjusting())updateActionButtons(restore,delete);});
        add(main,BorderLayout.CENTER);refresh();updateActionButtons(restore,delete);
    }
    private JButton navButton(String text,boolean primary){JButton b=new JButton(text);b.setAlignmentX(Component.LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(180,42));b.setHorizontalAlignment(SwingConstants.LEFT);b.setFocusPainted(false);b.setForeground(Color.WHITE);b.setBackground(primary?new Color(55,117,190):new Color(30,52,82));b.setBorder(new EmptyBorder(10,12,10,12));return b;}
    private void addNav(JPanel panel,String name){JButton b=navButton("   "+name,false);b.addActionListener(e->{folder=name;heading.setText(name);refresh();});panel.add(b);panel.add(Box.createVerticalStrut(5));}
    private void refresh(){currentEmails=switch(folder){case "Sent"->service.getSent(user.getEmailAddress());case "Drafts"->service.getDrafts(user.getEmailAddress());case "Trash"->service.getTrash(user.getEmailAddress());default->service.getInbox(user.getEmailAddress());};showEmails(currentEmails);}
    private void showEmails(List<Email> emails){currentEmails=emails;listModel.clear();for(Email e:emails){String other=folder.equals("Sent")?"To: "+e.getRecipient():"From: "+e.getSender();String status=e.isDraft()?"[DRAFT] ":(!e.isRead()&&!folder.equals("Sent")?"● ":"");listModel.addElement(status+other+"     |     "+e.getSubject()+"     |     "+e.getDate().format(DATE));}}
    private void search(){String q=searchField.getText().trim();if(q.isEmpty()){refresh();return;}showEmails(service.search(user.getEmailAddress(),q,folder));}
    private Email selected(){int index=emailList.getSelectedIndex();return index>=0&&index<currentEmails.size()?currentEmails.get(index):null;}
    private void openSelected(){Email e=selected();if(e==null){message("Select a message first.");return;}if(folder.equals("Drafts")){new ComposeMail(this,service,user,e,this::refresh).setVisible(true);return;}
        if(folder.equals("Inbox")&&!e.isRead()){try{service.markRead(e,true);}catch(Exception ex){message(ex.getMessage());}}
        String details="From: "+e.getSender()+"\nTo: "+e.getRecipient()+"\nDate: "+e.getDate().format(DATE)+"\nSubject: "+e.getSubject()+"\n\n"+e.getBody();JTextArea area=new JTextArea(details,16,55);area.setEditable(false);area.setLineWrap(true);area.setWrapStyleWord(true);area.setFont(new Font("SansSerif",Font.PLAIN,14));JOptionPane.showMessageDialog(this,new JScrollPane(area),"Message details",JOptionPane.INFORMATION_MESSAGE);refresh();}
    private void markUnread(){Email e=selected();if(e==null){message("Select a message first.");return;}if(!folder.equals("Inbox")){message("Mark Unread is available in Inbox.");return;}try{service.markRead(e,false);refresh();}catch(Exception ex){message(ex.getMessage());}}
    private void deleteSelected(){Email e=selected();if(e==null){message("Select a message first.");return;}try{if(folder.equals("Drafts"))service.deleteDraft(e);else service.delete(e,user.getEmailAddress());refresh();}catch(Exception ex){message(ex.getMessage());}}
    private void restoreSelected(){Email e=selected();if(e==null){message("Select a message first.");return;}try{service.restore(e,user.getEmailAddress());refresh();}catch(Exception ex){message(ex.getMessage());}}
    private void updateActionButtons(JButton restore,JButton delete){restore.setEnabled(folder.equals("Trash"));delete.setEnabled(!folder.equals("Trash"));}
    private void message(String text){JOptionPane.showMessageDialog(this,text);}
}
