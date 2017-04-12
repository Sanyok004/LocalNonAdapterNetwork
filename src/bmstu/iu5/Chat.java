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
    private JButton FinishDialogButton;

    Chat() {
        setSize(830, 600);
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

    void clearChat() {
        ReadMessage.setText("");
    }

    void setDialog() {
        SendButton.addActionListener(new SendButtonActionListener());
        ChoiseUser.removeActionListener(ChoiseUser.getActionListeners()[0]);
        ChoiseUser.addActionListener(new LockedChoiseUserButtonActionListener());
        SendMessage.addKeyListener(new SendMessageKeyListener());
        FinishDialogButton.addActionListener(new FinishDialogButtonActionListener());
        SendMessage.setEnabled(true);
    }

    void finishDialog() {
        SendButton.removeActionListener(SendButton.getActionListeners()[0]);
        ChoiseUser.removeActionListener(ChoiseUser.getActionListeners()[0]);
        ChoiseUser.addActionListener(new ChoiseUserButtonActionListener());
        SendMessage.removeKeyListener(SendMessage.getKeyListeners()[0]);
        FinishDialogButton.removeActionListener(FinishDialogButton.getActionListeners()[0]);
        SendMessage.setEnabled(false);

        Main.dialogNameUser = null;
        Main.dialogAddressUser = 0;
        setTitle("Chat - " + Main.userName);
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
                Main.buffer = new Frame(Frame.ACK, Main.dialogAddressUser, Main.address);
            }
        }
    }

    class LockedChoiseUserButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(rootPanel, "Вы не можите начать новый диалог, пока не закончите старый", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class FinishDialogButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Main.buffer = new Frame(Frame.FINISH, Main.dialogAddressUser, Main.address);
            finishDialog();
            clearChat();
            setReadMessage("Вы закончили диалог", "System");
            setReadMessage("Выберите пользователя, с которым хотите начать диалог -->", "System");
        }
    }
}
