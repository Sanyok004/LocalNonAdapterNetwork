package bmstu.iu5;

import javax.comm.*;
import  java.util.*;

public class Main {
    private static CommPortIdentifier portId = null;
    static Terminal outTerminal;
    static String userName;
    static Chat chat;

    public static void main(String[] args) {
        GUI gui = new GUI();
        Scanner scanner = new Scanner(System.in);
        userName = gui.userName;
        String outName = gui.outPort;
        String inName = gui.inPort;

        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier _portId = (CommPortIdentifier) portList.nextElement();
            if (_portId.getName().equals(outName)) {
                portId = _portId;
            }
        }
        if (portId == null) {
            System.out.println("Port not found.");
            System.exit(1);
        }

        CommPortIdentifier inPortId = null;
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier _portId = (CommPortIdentifier) portList.nextElement();
            if (_portId.getName().equals(inName)) {
                inPortId = _portId;
            }
        }
        if (inPortId == null) {
            System.out.println("Port not found.");
            System.exit(1);
        }

        outTerminal = new Terminal(portId, true);
        Terminal inTerminal = new Terminal(inPortId, false);

        chat = new Chat();

    }
}
