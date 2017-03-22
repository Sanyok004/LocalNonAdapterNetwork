package bmstu.iu5;

import javax.swing.*;

public class GUI extends JFrame{
    private JPanel rootPanel;
    private JButton pushButton;

    GUI() {
        setSize(400, 600);
        setTitle("Non adapter network");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        setContentPane(rootPanel);

        setVisible(true);
    }
}
