package bmstu.iu5;

import javax.comm.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static Terminal outTerminal;
    static String userName;
    static String dialogNameUser;
    static byte dialogAddressUser;
    static Chat chat;
    static byte address = 0;
    static Map<String, Byte> usersMap = new HashMap<>();
    static Frame buffer;
    static boolean isMain, isMarker;

    public static void main(String[] args) throws InterruptedException {
        GUI gui = new GUI();
        userName = gui.userName;
        String outName = gui.outPort;
        String inName = gui.inPort;
        isMain = gui.isMain;

        CommPortIdentifier outPortId = gui.foundPort(outName);
        CommPortIdentifier inPortId = gui.foundPort(inName);

        outTerminal = new Terminal(outPortId, true);
        Terminal inTerminal = new Terminal(inPortId, false);

        chat = new Chat();
        chat.setReadMessage("Подождите, идет настройка сети...", "System");

        if (isMain) {
            address = 1;
            buffer = new Frame(Frame.SET_ADDRESS, (byte)-1, address, (byte)2);
            outTerminal.send(new Frame(Frame.MARKER, (byte)-1, (byte)-1));
        }
    }
}
