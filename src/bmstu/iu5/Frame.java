package bmstu.iu5;

class Frame {
    private byte startByte = -1;
    private byte stopByte = startByte;
    private String contentFrame;
    private static byte numberFrame = 0;
    private byte typeFrame;
    private byte sourceAddress, destinationAddress, lengthData = 0;
    //private byte[] amountFrame = new byte[4];

    Frame(byte type, byte srcAddress, byte destAddress, String message) {
        contentFrame = message;
        typeFrame = type;
        sourceAddress = srcAddress;
        destinationAddress = destAddress;
        lengthData = (byte)message.length();
        //amountFrame[0] = 0;
        numberFrame++;
    }

    Frame(byte[] readBuffer) {
        byte destination = readBuffer[1];
        byte source = readBuffer[2];
        typeFrame = readBuffer[3];

        switch (typeFrame) {
            case 0:
                numberFrame = readBuffer[4];
                byte length = readBuffer[5];
                byte[] data = new byte[length];
                System.arraycopy(readBuffer, 6, data, 0, length);
                contentFrame = new String(data, 0, data.length);
        }

        System.out.print("Номер кадра: " + numberFrame);
        System.out.print("; Тип кадра: " + typeFrame);
        System.out.print("; Источник: " + source);
        System.out.print("; Пункт назначения: " + destination);
        System.out.println("; Сообщение: " + contentFrame);
    }

    byte[] getBytes() {
        byte[] bytesStream = new byte[200];
        byte[] bytesContentFrame = contentFrame.getBytes();

        bytesStream[0] = startByte;
        bytesStream[1] = destinationAddress;
        bytesStream[2] = sourceAddress;
        bytesStream[3] = typeFrame;

        switch (typeFrame) {
            case 0:
                bytesStream[4] = numberFrame;
                bytesStream[5] = lengthData;
                System.arraycopy(bytesContentFrame, 0, bytesStream, 6, bytesContentFrame.length);
                bytesStream[lengthData + 6] = stopByte;
                break;
        }

        return bytesStream;
    }
}
