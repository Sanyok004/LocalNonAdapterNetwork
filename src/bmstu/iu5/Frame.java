package bmstu.iu5;

class Frame {
    static final byte DATA_TRANSFER = 0;
    static final byte SET_ADDRESS = 1;

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

    Frame (byte type, byte destAddress, byte srcAddress, byte address) {
        typeFrame = type;
        destinationAddress = destAddress;
        sourceAddress = srcAddress;
        contentFrame = new byte[1];
        contentFrame[0] = address;
        lengthData = (byte)1;
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
                new Message(data);
                break;

            case SET_ADDRESS:
                if (Main.address == 0) {
                    Main.address = readBuffer[5];
                    byte address = Main.address;
                    sendFrame(new Frame(typeFrame, destination, source, ++address));
                }
                break;
        }

        System.out.print("Номер кадра: " + numberFrame);
        System.out.print("; Тип кадра: " + typeFrame);
        System.out.print("; Источник: " + source);
        System.out.println("; Пункт назначения: " + destination);
        System.out.print("> ");
    }

    byte getLengthFrame() {
        byte lengthFrame = 4;
        switch (typeFrame) {
            case DATA_TRANSFER:
            case SET_ADDRESS:
                lengthFrame += (byte)(lengthData + 1);
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
                bytesStream[3] = numberFrame;
                bytesStream[4] = lengthData;
                System.arraycopy(contentFrame, 0, bytesStream, 5, lengthData);
                break;
        }
        return bytesStream;
    }

    private void sendFrame(Frame frame) {
        Main.outTerminal.send(frame);
    }
}
