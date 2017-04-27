package bmstu.iu5;

import java.util.ArrayList;

class Frame {
    static final byte MARKER = -1;
    static final byte DATA_TRANSFER = 0;
    static final byte SET_ADDRESS = 1;
    private static final byte IS_READY = 2;
    private static final byte GET_NAMES = 3;
    private static final byte SET_NAMES = 4;
    static final byte ACK = 5;
    private static final byte SYN = 6;
    private static final byte NO_SYN = 7;
    private static final byte SUCCESS = 8;
    private static final byte ERROR = 9;
    static final byte FINISH = 10;

    private byte[] contentFrame;
    private byte numberFrame = 0;
    private byte typeFrame;
    private byte sourceAddress, destinationAddress, lengthData = 0;
    private byte[] amountFrame = new byte[2];
    private Hemming h = new Hemming();
    static private int countError = 0;

    Frame(byte type, byte destAddress, byte srcAddress, Message message) {
        contentFrame = message.getBytes();
        typeFrame = type;
        destinationAddress = destAddress;
        sourceAddress = srcAddress;
        lengthData = (byte)message.getLengthMessage();
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
        if (readBuffer.length == 4) readBuffer = h.decription3(readBuffer);
        else readBuffer = h.decryption(readBuffer);
        byte destination = readBuffer[0];
        byte source = readBuffer[1];
        if (h.errorHead || h.errorData) Main.outTerminal.send(new Frame(ERROR, (byte)-1, Main.address));
        else {
            typeFrame = readBuffer[2];
            switch (typeFrame) {
                case DATA_TRANSFER:
                    numberFrame = readBuffer[3];
                    byte length = readBuffer[4];
                    byte[] amount = new byte[2];
                    System.arraycopy(readBuffer, 5, amount, 0, 2);
                    byte[] data = new byte[length];
                    System.arraycopy(readBuffer, 7, data, 0, length);
                    if (destination == Main.address) {
                        Main.outTerminal.send(new Frame(SUCCESS, source, destination));
                        new Message(data);
                    } else Main.outTerminal.send(new Frame(typeFrame, destination, source, data));
                    break;

                case SET_ADDRESS:
                    if (Main.address == 0) {
                        Main.address = readBuffer[7];
                        byte address = Main.address;
                        Main.buffer = new Frame(typeFrame, destination, Main.address, ++address);
                        Main.outTerminal.send(new Frame(SUCCESS, source, Main.address));
                    } else {
                        byte[] name = new byte[Main.userName.length() + 1];
                        name[0] = Main.address;
                        System.arraycopy(Main.userName.getBytes(), 0, name, 1, Main.userName.length());
                        Main.buffer = new Frame(Frame.GET_NAMES, (byte)-1, Main.address, name);
                        Main.outTerminal.send(new Frame(SUCCESS, source, Main.address));
                    }
                    break;

                case GET_NAMES:
                    byte lengthNames = readBuffer[4];
                    if (!Main.isMain) {
                        ArrayList<String> usersList = new ArrayList<>();
                        byte[] names = new byte[lengthNames + Main.userName.length() + 2];
                        System.arraycopy(readBuffer, 7, names, 0, lengthNames);

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

                        if (usersList.contains(Main.userName)) {
                            new ChangeName(usersList);
                            names = new byte[lengthNames + Main.userName.length() + 2];
                            System.arraycopy(readBuffer, 7, names, 0, lengthNames);
                        }

                        names[lengthNames] = -127;
                        names[lengthNames + 1] = Main.address;
                        System.arraycopy(Main.userName.getBytes(), 0, names, lengthNames + 2, Main.userName.length());
                        Main.buffer = new Frame(typeFrame, destination, Main.address, names);
                        Main.outTerminal.send(new Frame(SUCCESS, source, Main.address));
                    } else {
                        byte[] allNames = new byte[lengthNames];
                        System.arraycopy(readBuffer, 7, allNames, 0, lengthNames);
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
                        Main.buffer = new Frame(SET_NAMES, destination, Main.address, allNames);
                        Main.outTerminal.send(new Frame(SUCCESS, source, Main.address));
                    }
                    break;

                case SET_NAMES:
                    ArrayList<String> usersList = new ArrayList<>();
                    byte lengthNamesList = readBuffer[4];
                    byte[] allNames = new byte[lengthNamesList];
                    System.arraycopy(readBuffer, 7, allNames, 0, lengthNamesList);
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

                    if (!Main.isMain) {
                        Main.buffer = new Frame(typeFrame, destination, Main.address, allNames);
                        Main.outTerminal.send(new Frame(SUCCESS, source, Main.address));
                    }
                    else {
                        Main.buffer = new Frame(IS_READY, (byte) -1, Main.address);
                        Main.outTerminal.send(new Frame(SUCCESS, source, Main.address));
                    }
                    break;

                case IS_READY:
                    Main.chat.setReadMessage("Завершено.", "System");
                    Main.chat.setReadMessage("Выберите пользователя, с которым хотите начать диалог -->", "System");
                    if (!Main.isMain) {
                        Main.buffer = new Frame(typeFrame, destination, Main.address);
                        Main.outTerminal.send(new Frame(SUCCESS, source, Main.address));
                    }
                    else Main.outTerminal.send(new Frame(SUCCESS, source, Main.address));
                    break;

                case ACK:
                    if (destination == Main.address) {
                        Main.outTerminal.send(new Frame(SUCCESS, source, destination));
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
                        } else {
                            Main.chat.setReadMessage("Вы отказались начать диалог с " + user, "System");
                            sendFrame(new Frame(NO_SYN, source, Main.address));
                        }
                    } else Main.outTerminal.send(new Frame(typeFrame, destination, source));
                    break;

                case SYN:
                    if (destination == Main.address) {
                        Main.outTerminal.send(new Frame(SUCCESS, source, destination));
                        String user = "";
                        for (String k : Main.usersMap.keySet()) {
                            if (Main.usersMap.get(k) == source) user = k;
                        }
                        Main.chat.setTitle("Chat: " + Main.userName + " - " + Main.dialogNameUser);
                        Main.chat.setReadMessage("Пользователь " + user + " согласился начать с вами диалог", "System");
                        Main.chat.setReadMessage("\n ---------------------\n", "");
                        Main.chat.setDialog();
                    } else Main.outTerminal.send(new Frame(typeFrame, destination, source));
                    break;

                case NO_SYN:
                    if (destination == Main.address) {
                        Main.outTerminal.send(new Frame(SUCCESS, source, destination));
                        String user = "";
                        for (String k : Main.usersMap.keySet()) {
                            if (Main.usersMap.get(k) == source) user = k;
                        }
                        Main.chat.setReadMessage("Пользователь " + user + " не согласился начать с вами диалог", "System");
                        Main.dialogAddressUser = 0;
                        Main.dialogNameUser = null;

                    } else Main.outTerminal.send(new Frame(typeFrame, destination, source));
                    break;

                case SUCCESS:
                    if (destination == Main.address) {
                        Main.buffer = null;
                        Main.isMarker = false;
                        Main.outTerminal.send(new Frame(MARKER, (byte) -1, (byte) -1));
                    } else Main.outTerminal.send(new Frame(typeFrame, destination, source));
                    break;

                case ERROR:
                    if (Main.isMarker) {
                        System.out.println("countError: " + countError);
                        countError++;
                        if (countError == 3) {
                            Main.chat.setReadMessage("Возникла ошибка при передаче сообщения", "System");
                            countError = 0;
                            Main.buffer = null;
                            Main.isMarker = false;
                            Main.outTerminal.send(new Frame(MARKER, (byte) -1, (byte) -1));
                        } else Main.outTerminal.send(Main.buffer);
                    }
                    else Main.outTerminal.send(new Frame(typeFrame, destination, source));
                    break;

                case FINISH:
                    if (destination == Main.address) {
                        Main.outTerminal.send(new Frame(SUCCESS, source, destination));
                        String user = "";
                        for (String k : Main.usersMap.keySet()) {
                            if (Main.usersMap.get(k) == source) user = k;
                        }

                        Main.chat.finishDialog();
                        Main.chat.clearChat();
                        Main.chat.setReadMessage("Пользователь " + user + " закончил с вами диалог", "System");
                        Main.chat.setReadMessage("Выберите пользователя, с которым хотите начать диалог -->", "System");
                    } else Main.outTerminal.send(new Frame(typeFrame, destination, source));
                    break;

                case MARKER:
                    if (Main.buffer != null) {
                        Main.isMarker = true;
                        Main.outTerminal.send(Main.buffer);
                    } else Main.outTerminal.send(new Frame(typeFrame, destination, source));
                    break;
            }
        }

        if (typeFrame != MARKER) {
            System.out.print("Номер кадра: " + numberFrame);
            System.out.print("; Тип кадра: " + typeFrame);
            System.out.print("; Источник: " + source);
            System.out.println("; Пункт назначения: " + destination);
            System.out.println("--------------");
        }
    }

    byte getLengthFrame() {
        byte lengthFrame = 4;
        switch (typeFrame) {
            case DATA_TRANSFER:
            case SET_ADDRESS:
            case GET_NAMES:
            case SET_NAMES:
                lengthFrame += (byte)(lengthData + 4);
                break;

            case MARKER:
            case IS_READY:
            case ACK:
            case SYN:
            case NO_SYN:
            case SUCCESS:
            case ERROR:
            case FINISH:
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
            //case DATA_TRANSFER:
            case SET_ADDRESS:
            case GET_NAMES:
            case SET_NAMES:
                bytesStream[3] = numberFrame;
                bytesStream[4] = lengthData;
                System.arraycopy(amountFrame, 0, bytesStream, 5, 2);
                System.arraycopy(contentFrame, 0, bytesStream, 7, lengthData);
                bytesStream[7 + lengthData] = 0;
                bytesStream = h.checkbyte(bytesStream);
                bytesStream = h.encrypted(bytesStream);
                break;

            case DATA_TRANSFER:
                bytesStream[3] = numberFrame;
                bytesStream[4] = lengthData;
                System.arraycopy(amountFrame, 0, bytesStream, 5, 2);
                System.arraycopy(contentFrame, 0, bytesStream, 7, lengthData);
                bytesStream[7 + lengthData] = 0;
                bytesStream = h.checkbyte(bytesStream);
                bytesStream = h.encrypted(bytesStream);
                //bytesStream[3]++;
                break;

            case MARKER:
            case IS_READY:
            case ACK:
            case SYN:
            case NO_SYN:
            case SUCCESS:
            case ERROR:
            case FINISH:
                bytesStream[3] = 0;
                bytesStream = h.encrypted3(bytesStream);
                break;
        }
        return bytesStream;
    }

    private void sendFrame(Frame frame) {
        Main.buffer = frame;
    }
}
