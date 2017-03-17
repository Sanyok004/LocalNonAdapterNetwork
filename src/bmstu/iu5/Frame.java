package bmstu.iu5;

import java.nio.ByteBuffer;

class Frame {
    private byte STARTBYTE = Byte.valueOf("11111111", 2);
    public String contentFrame;
    public static int numberFrame = 0;
    public byte typeFrame;
    public int sourceAddress, destinationAddress, lengthFrame;
    public byte[] amountFrame = new byte[4];

    Frame(byte type, int srcAddress, int destAddress, String message) {
        contentFrame = message;
        typeFrame = type;
        sourceAddress = srcAddress;
        destinationAddress = destAddress;
        lengthFrame = message.length() + 17; // Не забудь поменять!!!
        amountFrame[0] = 0;
        numberFrame++;
    }

    Frame(byte[] readBuffer) {
        int numberFrame = ((readBuffer[0] & 0xFF) << 24) + ((readBuffer[1] & 0xFF) << 16) + ((readBuffer[2] & 0xFF) << 8) + (readBuffer[3] & 0xFF);
        typeFrame = readBuffer[4];
        int source = ((readBuffer[5] & 0xFF) << 24) + ((readBuffer[6] & 0xFF) << 16) + ((readBuffer[7] & 0xFF) << 8) + (readBuffer[8] & 0xFF);
        int destination = ((readBuffer[9] & 0xFF) << 24) + ((readBuffer[10] & 0xFF) << 16) + ((readBuffer[11] & 0xFF) << 8) + (readBuffer[12] & 0xFF);
        byte[] data = new byte[readBuffer.length - 13];
        System.arraycopy(readBuffer, 13, data, 0, data.length);

        contentFrame = new String(data, 0, data.length);
        System.out.print("Номер кадра: " + numberFrame);
        System.out.print("; Тип кадра: " + typeFrame);
        System.out.print("; Источник: " + source);
        System.out.print("; Пункт назначения: " + destination);
        System.out.println("; Сообщение: " + contentFrame);
    }

    byte[] getBytes() {
        byte[] bytesStream = new byte[200];
        byte[] bytesNumberFrame = ByteBuffer.allocate(4).putInt(numberFrame).array();
        byte[] bytesSourceAddress = ByteBuffer.allocate(4).putInt(sourceAddress).array();
        byte[] bytesDestinationAddress = ByteBuffer.allocate(4).putInt(destinationAddress).array();
        byte[] bytesContentFrame = contentFrame.getBytes();

        System.arraycopy(bytesNumberFrame, 0, bytesStream, 0, bytesNumberFrame.length);
        bytesStream[4] = typeFrame;
        System.arraycopy(bytesSourceAddress, 0, bytesStream, 5, bytesSourceAddress.length);
        System.arraycopy(bytesDestinationAddress, 0, bytesStream, 9, bytesDestinationAddress.length);
        System.arraycopy(bytesContentFrame, 0, bytesStream, 13, bytesContentFrame.length);

        return bytesStream;
    }
}
