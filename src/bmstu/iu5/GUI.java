package bmstu.iu5;

import javax.comm.CommPortIdentifier;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class GUI extends JFrame{
    private JPanel rootPanel;
    private JTextField OutPort;
    private JLabel OutPortLabel;
    private JLabel InPortLabel;
    private JTextField InPort;
    private JButton OKButton;
    private JLabel NameLabel;
    private JTextField Name;
    private JLabel PortNotFound1;
    private JLabel PortNotFound2;
    private JCheckBox IsMain;

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

        setVisible(true);
        checkForm();
        setVisible(false);
    }

    private boolean checkPort(CommPortIdentifier portId) {
        boolean isNotFound;
        isNotFound = portId == null;
        return isNotFound;
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

    private void checkForm() {
        while (isError) {
            //...
        }
    }

    class OKButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            outPort = OutPort.getText();
            boolean outPortIsNotFound = checkPort(foundPort(outPort));
            if (outPortIsNotFound) PortNotFound1.setText("Порт не найден");
            else PortNotFound1.setText("");

            inPort = InPort.getText();
            boolean inPortIsNotFound = checkPort(foundPort(inPort));
            if (inPortIsNotFound) PortNotFound2.setText("Порт не найден");
            else PortNotFound2.setText("");

            isMain = IsMain.isSelected();
            userName = Name.getText();
            isError = outPortIsNotFound || inPortIsNotFound;
        }
    }
}
