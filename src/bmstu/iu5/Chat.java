package bmstu.iu5;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Chat extends JFrame{
    private JPanel rootPanel;
    private JTextField SendMessage;
    private JButton SendButton;
    private JScrollPane ScrollPane;
    private JTextArea ReadMessage;
    private JLabel UsersListLabel;
    private JScrollPane UsersScroll;
    JList<String> UsersList;
    private JButton ChoiseUser;

    Chat() {
        setSize(800, 600);
        setTitle("Chat - " + Main.userName);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        SendMessage.setEnabled(false);
        ChoiseUser.addActionListener(new ChoiseUserButtonActionListener());
        setContentPane(rootPanel);

        setVisible(true);
    }

    void setReadMessage(String message, String src) {
        String textBuffer = ReadMessage.getText();
        textBuffer = textBuffer + src + ": " + message + "\n";
        ReadMessage.setText(textBuffer);
    }

    void setDialog() {
        SendButton.addActionListener(new SendButtonActionListener());
        SendMessage.addKeyListener(new SendMessageKeyListener());
        SendMessage.setEnabled(true);
    }

    int choiseUser(String user) {
        return JOptionPane.showConfirmDialog(rootPanel, "Пользователь " + user + " хочет начать с вами диалог", "Начать диалог?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    class SendButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String sendMessage = SendMessage.getText();
            SendMessage.setText("");

            new Message(Main.dialogNameUser, sendMessage);
            setReadMessage(sendMessage, Main.userName);
        }
    }

    class SendMessageKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String sendMessage = SendMessage.getText();
                SendMessage.setText("");

                new Message(Main.dialogNameUser, sendMessage);
                setReadMessage(sendMessage, Main.userName);
            }
        }
    }

    class ChoiseUserButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String user = UsersList.getSelectedValue();
            if (user != null) {
                Main.dialogNameUser = user;
                Main.dialogAddressUser = Main.usersMap.get(user);
                setReadMessage("Запрос на установление соединения с пользователем " + user, "System");
                Main.outTerminal.send(new Frame(Frame.ACK, Main.dialogAddressUser, Main.address));
            }
        }
    }
}
