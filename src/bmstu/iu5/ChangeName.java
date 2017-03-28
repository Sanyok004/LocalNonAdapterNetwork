package bmstu.iu5;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChangeName extends JFrame {
    private JPanel rootPanel;
    private JLabel ChangeNameLabel;
    private JTextField ChangeNameTextField;
    private JButton ChangeNameButton;
    private ArrayList<String> usersList = new ArrayList<>();
    private boolean isBusy = true;

    ChangeName(ArrayList<String> names) {
        usersList = names;
        setSize(300, 150);
        setLocationRelativeTo(Main.chat);
        ChangeNameButton.addActionListener(new ChangeNameButtonActionListener());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setContentPane(rootPanel);
        setVisible(true);

        while (isBusy);
    }

    class ChangeNameButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = ChangeNameTextField.getText();
            if (!usersList.contains(name)) {
                Main.userName = name;
                isBusy = false;
                Main.chat.setTitle("Chat - " + Main.userName);
                setVisible(false);
            }
        }
    }
}
