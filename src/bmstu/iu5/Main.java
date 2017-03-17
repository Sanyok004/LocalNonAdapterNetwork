package bmstu.iu5;

import javax.comm.*;
import  java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter port name: ");
        String name = scanner.nextLine();
        CommPortIdentifier portId = null;
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier _portId = (CommPortIdentifier) portList.nextElement();
            if (_portId.getName().equals(name)) {
                portId = _portId;
            }
        }
        if (portId == null) {
            System.out.println("Port not found.");
            return;
        }
        new Terminal(portId, scanner);
    }
}
