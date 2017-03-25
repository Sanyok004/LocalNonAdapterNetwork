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

    Chat() {
        setSize(800, 600);
        setTitle("Chat - " + Main.userName);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        SendButton.addActionListener(new SendButtonActionListener());
        SendMessage.addKeyListener(new SendMessageKeyListener());

        setContentPane(rootPanel);

        setVisible(true);
    }

    void setReadMessage(String message, String src) {
        String textBuffer = ReadMessage.getText();
        textBuffer = textBuffer + src + ": " + message + "\n";
        ReadMessage.setText(textBuffer);
    }

    class SendButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String sendMessage = SendMessage.getText();
            SendMessage.setText("");

            Message message = new Message("Lol", sendMessage);
            setReadMessage(sendMessage, Main.userName);
        }
    }

    class SendMessageKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String sendMessage = SendMessage.getText();
                SendMessage.setText("");

                Message message = new Message("Lol", sendMessage);
                setReadMessage(sendMessage, Main.userName);
            }
        }
    }
}
