package bmstu.iu5;

import javax.comm.*;

public class Main {
    static Terminal outTerminal;
    static String userName;
    static Chat chat;
    static boolean isMain;
    static boolean isReady = true;
    static byte address = 0;

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
        if (isMain) {
            address = 1;
            isReady = false;
            outTerminal.send(new Frame(Frame.SET_ADDRESS, (byte)-1, address, (byte)2));
            while (!isReady);

            byte[] name = new byte[userName.length() + 1];
            name[0] = address;
            System.arraycopy(userName.getBytes(), 0, name, 1, userName.length());
            isReady = false;
            outTerminal.send(new Frame(Frame.GET_NAMES, (byte)-1, address, name));
            while (!isReady);

            System.out.println("!-------- I am here!!!!!!!!!");
        }
    }
}
