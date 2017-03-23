package bmstu.iu5;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chat extends JFrame{
    private JPanel rootPanel;
    private JTextField SendMessage;
    private JButton SendButton;
    private JScrollPane ScrollPane;
    private JTextArea ReadMessage;

    String sendMessage, readMessage;

    Chat() {
        setSize(800, 600);
        setTitle("Chat - " + Main.userName);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        SendButton.addActionListener(new SendButtonActionListener());

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
            sendMessage = SendMessage.getText();
            SendMessage.setText("");

            Message message = new Message("Lol", sendMessage);
        }
    }
}
