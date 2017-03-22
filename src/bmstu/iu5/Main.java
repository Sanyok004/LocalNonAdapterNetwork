package bmstu.iu5;

import javax.comm.*;
import  java.util.*;

public class Main {
    private static CommPortIdentifier portId = null;
    static Terminal outTerminal;

    public static void main(String[] args) {
        new GUI();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите исходящий порт: ");
        String name = scanner.nextLine();
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier _portId = (CommPortIdentifier) portList.nextElement();
            if (_portId.getName().equals(name)) {
                portId = _portId;
            }
        }
        if (portId == null) {
            System.out.println("Port not found.");
            System.exit(1);
        }

        System.out.println("Введите входящий порт: ");
        String inName = scanner.nextLine();
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

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line.equals("exit")) System.exit(1);

            Message message = new Message("BlaBla", "LoL", line);
        }
    }
}
