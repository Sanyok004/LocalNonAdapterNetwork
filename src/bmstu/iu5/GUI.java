package bmstu.iu5;

import javax.comm.CommPortIdentifier;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class GUI extends JFrame{
    private JPanel rootPanel;
    private JLabel OutPortLabel;
    private JLabel InPortLabel;
    private JButton OKButton;
    private JLabel NameLabel;
    private JTextField Name;
    private JCheckBox IsMain;
    private JComboBox<String> OutPortComboBox;
    private JComboBox<String> InPortComboBox;
    private JLabel NameIsEmptyLabel;
    private JLabel EqualsPortsLabel;

    String outPort, inPort, userName;
    private boolean isError = true;
    boolean isMain;

    GUI() {
        setSize(400, 400);
        setTitle("Settings");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        OKButton.addActionListener(new OKButtonActionListener());
        setContentPane(rootPanel);
        portsList(OutPortComboBox);
        portsList(InPortComboBox);

        setVisible(true);
        checkForm();
        setVisible(false);
    }

    CommPortIdentifier foundPort (String namePort) {
        CommPortIdentifier portId = null;
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier _portId = (CommPortIdentifier) portList.nextElement();
            if (_portId.getName().equals(namePort)) {
                portId = _portId;
            }
        }

        return portId;
    }

    private void portsList(JComboBox<String> comboBox) {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier _portId = (CommPortIdentifier) portList.nextElement();
            comboBox.addItem(_portId.getName());
        }
    }

    private void checkForm() {
        while (isError) {
            //...
        }
    }

    class OKButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            outPort = (String) OutPortComboBox.getSelectedItem();
            inPort = (String ) InPortComboBox.getSelectedItem();
            isMain = IsMain.isSelected();
            userName = Name.getText();

            if (outPort.equals(inPort)) EqualsPortsLabel.setText("Порты не могут быть одинаковыми");
            else EqualsPortsLabel.setText("");
            if (userName.equals("")) NameIsEmptyLabel.setText("Имя не может быть пустым");
            else if (userName.length() > 8) NameIsEmptyLabel.setText("Имя не может содержать больше 8 символов");
            else NameIsEmptyLabel.setText("");

            isError = outPort.equals(inPort) || userName.equals("") || userName.length() > 8;
        }
    }
}
