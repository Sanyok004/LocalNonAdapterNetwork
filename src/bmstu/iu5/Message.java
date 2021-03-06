package bmstu.iu5;

class Message {
    private String sourceName = Main.userName;
    private String destinationName, message;
    private byte lenSrcName, lenDestName, lenMessage;

    Message(String destName, String message) {
        destinationName = destName;
        this.message = message;
        lenSrcName = (byte)sourceName.getBytes().length;
        lenDestName = (byte)destinationName.getBytes().length;
        lenMessage = (byte)message.getBytes().length;

        sendMessage();
    }

    Message(byte[] bytes) {
        byte lenDest = bytes[0];
        byte lenSrc = bytes[lenDest + 1];
        byte lenMess = bytes[lenDest + lenSrc + 2];

        byte[] byteDest = new byte[lenDest];
        System.arraycopy(bytes, 1, byteDest, 0, lenDest);
        String dest = new String(byteDest, 0, bytes[0]);
        System.out.println("dest: " + dest);

        byte[] byteSrc = new byte[lenSrc];
        System.arraycopy(bytes, lenDest + 2, byteSrc, 0, lenSrc);
        String src = new String(byteSrc, 0 , lenSrc);
        System.out.println("src: " + src);

        byte[] byteMess = new byte[lenMess];
        System.arraycopy(bytes, lenDest + lenSrc + 3, byteMess, 0, lenMess);
        String mess = new String(byteMess, 0 , lenMess);
        System.out.println("mess: " + mess);

        Main.chat.setReadMessage(mess, src);
    }

    int getLengthMessage() {
        return (int)lenDestName + (int)lenSrcName + (int)lenMessage + 3;
    }

    byte[] getBytes() {
        byte[] byteStream = new byte[getLengthMessage()];

        byteStream[0] = lenDestName;
        System.arraycopy(destinationName.getBytes(), 0, byteStream, 1, lenDestName);
        byteStream[lenDestName + 1] = lenSrcName;
        System.arraycopy(sourceName.getBytes(), 0, byteStream, lenDestName + 2, lenSrcName);
        byteStream[lenDestName + lenSrcName + 2] = lenMessage;
        System.arraycopy(message.getBytes(), 0, byteStream, lenDestName + lenSrcName + 3, lenMessage);

        return byteStream;
    }

    private void sendMessage() {
        new Frame(Frame.DATA_TRANSFER, Main.dialogAddressUser, Main.address, this);
    }
}
