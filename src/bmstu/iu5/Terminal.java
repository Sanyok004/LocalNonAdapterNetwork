package bmstu.iu5;

import javax.comm.*;
import java.io.*;
import java.util.TooManyListenersException;

public class Terminal implements Runnable, SerialPortEventListener {
    private InputStream inputStream;
    private OutputStream outputStream;
    SerialPort serialPort;
    private boolean isOut;
    private Frame frame;

    Terminal(CommPortIdentifier portId, boolean isOut) {
        this.isOut = isOut;

        try {
            serialPort = (SerialPort) portId.open("TerminalApp", 2000);
        } catch (PortInUseException e) {
            System.out.println("! - " + e.getMessage());
        }
        try {
            outputStream = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            System.out.println("! - " + e.getMessage());
        }

        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) {
            System.out.println("! - " + e.getMessage());
        }
        serialPort.notifyOnDataAvailable(true);

        try {
            serialPort.setSerialPortParams(9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("! - " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            if (isOut) {
                byte lengthFrame = frame.getLengthFrame();
                byte[] bytes = new byte[lengthFrame + 1];
                bytes[0] = lengthFrame;
                System.arraycopy(frame.getBytes(), 0, bytes, 1, lengthFrame);

                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (IOException e) {
            System.out.println("! - " + e.getMessage());
        }
    }

    void send(Frame frame) {
        this.frame = frame;
        new Thread(this).start();
    }

    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[200];
                int read = 0;
                try {
                    while (inputStream.available() > 0) {
                        int a = inputStream.read(readBuffer, read, readBuffer.length);
                        if (a == -1) break;
                        read += a;
                    }
                } catch (IOException e) {
                    System.out.println("! - " + e.getMessage());
                }

                byte lengthFrame = readBuffer[0];
                byte[] frame = new byte[lengthFrame];
                System.arraycopy(readBuffer, 1, frame, 0, lengthFrame);

                new Frame(frame);
                break;
        }
    }
}