package bmstu.iu5;

class Frame {
    private Message contentFrame;
    private static byte numberFrame = 0;
    private byte typeFrame;
    private byte sourceAddress, destinationAddress, lengthData = 0;
    //private byte[] amountFrame = new byte[4];

    Frame(byte type, byte srcAddress, byte destAddress, Message message) {
        contentFrame = message;
        typeFrame = type;
        sourceAddress = srcAddress;
        destinationAddress = destAddress;
        lengthData = (byte)message.getLengthMessage();
        //amountFrame[0] = 0;
        numberFrame++;
    }

    Frame(byte[] readBuffer) {
        byte destination = readBuffer[0];
        byte source = readBuffer[1];
        typeFrame = readBuffer[2];

        switch (typeFrame) {
            case 0:
                numberFrame = readBuffer[3];
                byte length = readBuffer[4];
                byte[] data = new byte[length];
                System.arraycopy(readBuffer, 5, data, 0, length);
                new Message(data);
        }

        System.out.print("Номер кадра: " + numberFrame);
        System.out.print("; Тип кадра: " + typeFrame);
        System.out.print("; Источник: " + source);
        System.out.println("; Пункт назначения: " + destination);
        System.out.print("> ");
    }

    byte getLengthFrame() {
        return (byte)(lengthData + 5);
    }

    byte[] getBytes() {
        byte[] bytesStream = new byte[getLengthFrame()];

        bytesStream[0] = destinationAddress;
        bytesStream[1] = sourceAddress;
        bytesStream[2] = typeFrame;

        switch (typeFrame) {
            case 0:
                bytesStream[3] = numberFrame;
                bytesStream[4] = lengthData;
                System.arraycopy(contentFrame.getBytes(), 0, bytesStream, 5, lengthData);
                break;
        }

        return bytesStream;
    }
}
