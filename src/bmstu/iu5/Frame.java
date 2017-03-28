package bmstu.iu5;

import java.util.ArrayList;

class Frame {
    static final byte DATA_TRANSFER = 0;
    static final byte SET_ADDRESS = 1;
    private static final byte IS_READY = 2;
    static final byte GET_NAMES = 3;
    private static final byte SET_NAMES = 4;
    static final byte ACK = 5;
    private static final byte SYN = 6;
    private static final byte NO_SYN = 7;

    private byte[] contentFrame;
    private byte numberFrame = 0;
    private byte typeFrame;
    private byte sourceAddress, destinationAddress, lengthData = 0;
    //private byte[] amountFrame = new byte[4];

    Frame(byte type, byte destAddress, byte srcAddress, Message message) {
        contentFrame = message.getBytes();
        typeFrame = type;
        destinationAddress = destAddress;
        sourceAddress = srcAddress;
        lengthData = (byte)message.getLengthMessage();
        //amountFrame[0] = 0;
        numberFrame++;

        sendFrame(this);
    }

    Frame(byte type, byte destAddress, byte srcAddress, byte[] names) {
        typeFrame = type;
        destinationAddress = destAddress;
        sourceAddress = srcAddress;
        contentFrame = names;
        lengthData = (byte)contentFrame.length;
    }

    Frame (byte type, byte destAddress, byte srcAddress, byte address) {
        typeFrame = type;
        destinationAddress = destAddress;
        sourceAddress = srcAddress;
        contentFrame = new byte[1];
        contentFrame[0] = address;
        lengthData = (byte)1;
    }

    Frame (byte type, byte destAddress, byte srcAddress) {
        typeFrame = type;
        destinationAddress = destAddress;
        sourceAddress = srcAddress;
    }

    Frame(byte[] readBuffer) {
        byte destination = readBuffer[0];
        byte source = readBuffer[1];
        typeFrame = readBuffer[2];

        switch (typeFrame) {
            case DATA_TRANSFER:
                numberFrame = readBuffer[3];
                byte length = readBuffer[4];
                byte[] data = new byte[length];
                System.arraycopy(readBuffer, 5, data, 0, length);
                if (destination == Main.address) new Message(data);
                else sendFrame(new Frame(typeFrame, destination, source, data));
                break;

            case SET_ADDRESS:
                if (Main.address == 0) {
                    Main.address = readBuffer[5];
                    byte address = Main.address;
                    sendFrame(new Frame(typeFrame, destination, source, ++address));
                }
                else Main.isReady = true;
                break;

            case GET_NAMES:
                byte lengthNames = readBuffer[4];
                if (source != Main.address) {
                    ArrayList<String> usersList = new ArrayList<>();
                    byte[] names = new byte[lengthNames + Main.userName.length() + 2];
                    System.arraycopy(readBuffer, 5, names, 0, lengthNames);

                    int position = 0;
                    while (position < lengthNames) {
                        int pos = 0;
                        while (names[position + pos] != -127) {
                            pos++;
                            if (position + pos == lengthNames) break;
                        }
                        byte[] name = new byte[pos - 1];
                        System.arraycopy(names, position + 1, name, 0, pos - 1);
                        String sName = new String(name);
                        usersList.add(sName);
                        position += pos + 1;
                    }

                    if (usersList.contains(Main.userName)) new ChangeName(usersList);

                    names[lengthNames] = -127;
                    names[lengthNames + 1] = Main.address;
                    System.arraycopy(Main.userName.getBytes(), 0, names, lengthNames + 2, Main.userName.length());
                    sendFrame(new Frame(typeFrame, destination, source, names));
                }
                else {
                    byte[] allNames = new byte[lengthNames];
                    System.arraycopy(readBuffer, 5, allNames, 0, lengthNames);
                    int position = 0;
                    while (position < lengthNames) {
                        int pos = 0;
                        byte address = allNames[position];
                        while (allNames[position + pos] != -127) {
                            pos++;
                            if (position + pos == lengthNames) break;
                        }
                        byte[] name = new byte[pos - 1];
                        System.arraycopy(allNames, position + 1, name, 0, pos - 1);
                        String sName = new String(name);
                        Main.usersMap.put(sName, address);
                        position += pos + 1;
                    }
                    sendFrame(new Frame(SET_NAMES, destination, source, allNames));
                }
                break;

            case SET_NAMES:
                ArrayList<String> usersList = new ArrayList<>();
                byte lengthNamesList = readBuffer[4];
                byte[] allNames = new byte[lengthNamesList];
                System.arraycopy(readBuffer, 5, allNames, 0, lengthNamesList);
                int position = 0;
                while (position < lengthNamesList) {
                    int pos = 0;
                    byte address = allNames[position];
                    while (allNames[position + pos] != -127) {
                        pos++;
                        if (position + pos == lengthNamesList) break;
                    }
                    byte[] name = new byte[pos - 1];
                    System.arraycopy(allNames, position + 1, name, 0, pos - 1);
                    String sName = new String(name);
                    if (!sName.equals(Main.userName)) usersList.add(sName);
                    Main.usersMap.put(sName, address);
                    position += pos + 1;
                }
                String[] users = usersList.toArray(new String[usersList.size()]);
                Main.chat.UsersList.setListData(users);

                if (source != Main.address) sendFrame(new Frame(typeFrame, destination, source, allNames));
                else sendFrame(new Frame(IS_READY, (byte)-1, Main.address));
                break;

            case IS_READY:
                Main.chat.setReadMessage("Завершено.", "System");
                Main.chat.setReadMessage("Выберите пользователя, с которым хотите начать диалог -->", "System");
                if (source != Main.address) sendFrame(new Frame(typeFrame, destination, source));
                break;

            case ACK:
                if (destination == Main.address) {
                    String user = "";
                    for (String k : Main.usersMap.keySet()) {
                        if (Main.usersMap.get(k) == source) user = k;
                    }
                    Main.chat.setReadMessage("Пользователь " + user + " хочет начать с вами диалог", "System");
                    if (Main.chat.choiseUser(user) == 0) {
                        Main.dialogNameUser = user;
                        Main.dialogAddressUser = source;
                        Main.chat.setTitle("Chat: " + Main.userName + " - " + Main.dialogNameUser);
                        Main.chat.setReadMessage("\n ---------------------\n", "");
                        Main.chat.setDialog();
                        sendFrame(new Frame(SYN, source, Main.address));
                    }
                    else {
                        Main.chat.setReadMessage("Вы отказались начать диалог с " + user, "System");
                        sendFrame(new Frame(NO_SYN, source, Main.address));
                    }
                }
                else sendFrame(new Frame(typeFrame, destination, source));
                break;

            case SYN:
                if (destination == Main.address) {
                    String user = "";
                    for (String k : Main.usersMap.keySet()) {
                        if (Main.usersMap.get(k) == source) user = k;
                    }
                    Main.chat.setTitle("Chat: " + Main.userName + " - " + Main.dialogNameUser);
                    Main.chat.setReadMessage("Пользователь " + user + " согласился начать с вами диалог", "System");
                    Main.chat.setReadMessage("\n ---------------------\n", "");
                    Main.chat.setDialog();
                }
                else sendFrame(new Frame(typeFrame, destination, source));
                break;

            case NO_SYN:
                if (destination == Main.address) {
                    String user = "";
                    for (String k : Main.usersMap.keySet()) {
                        if (Main.usersMap.get(k) == source) user = k;
                    }
                    Main.chat.setReadMessage("Пользователь " + user + " не согласился начать с вами диалог", "System");
                    Main.dialogAddressUser = 0;
                    Main.dialogNameUser = null;

                }
                else sendFrame(new Frame(typeFrame, destination, source));
                break;
        }

        System.out.print("Номер кадра: " + numberFrame);
        System.out.print("; Тип кадра: " + typeFrame);
        System.out.print("; Источник: " + source);
        System.out.println("; Пункт назначения: " + destination);
        System.out.println("--------------");
    }

    byte getLengthFrame() {
        byte lengthFrame = 3;
        switch (typeFrame) {
            case DATA_TRANSFER:
            case SET_ADDRESS:
            case GET_NAMES:
            case SET_NAMES:
                lengthFrame += (byte)(lengthData + 2);
                break;

            case IS_READY:
            case ACK:
            case SYN:
            case NO_SYN:
                break;
        }
        return lengthFrame;
    }

    byte[] getBytes() {
        byte[] bytesStream = new byte[getLengthFrame()];

        bytesStream[0] = destinationAddress;
        bytesStream[1] = sourceAddress;
        bytesStream[2] = typeFrame;

        switch (typeFrame) {
            case DATA_TRANSFER:
            case SET_ADDRESS:
            case GET_NAMES:
            case SET_NAMES:
                bytesStream[3] = numberFrame;
                bytesStream[4] = lengthData;
                System.arraycopy(contentFrame, 0, bytesStream, 5, lengthData);
                break;

            case IS_READY:
            case ACK:
            case SYN:
            case NO_SYN:
                break;
        }
        return bytesStream;
    }

    private void sendFrame(Frame frame) {
        Main.outTerminal.send(frame);
    }
}
