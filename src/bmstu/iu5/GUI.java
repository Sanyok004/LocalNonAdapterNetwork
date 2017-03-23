package bmstu.iu5;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame{
    private JPanel rootPanel;
    private JTextField OutPort;
    private JLabel OutPortLabel;
    private JLabel InPortLabel;
    private JTextField InPort;
    private JButton OKButton;
    private JLabel NameLabel;
    private JTextField Name;

    String outPort, inPort, userName;

    GUI() {
        setSize(400, 400);
        setTitle("Settings");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        OKButton.addActionListener(new OKButtonActionListener());

        setContentPane(rootPanel);

        setVisible(true);

        while (outPort == null || inPort == null || userName == null) {
            //...
        }
    }

    class OKButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            outPort = OutPort.getText();
            inPort = InPort.getText();
            userName = Name.getText();
            setVisible(false);
        }
    }
}
