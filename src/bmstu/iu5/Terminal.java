package bmstu.iu5;

import javax.comm.*;
import java.io.*;
import java.util.Scanner;
import java.util.TooManyListenersException;

public class Terminal implements Runnable, SerialPortEventListener {
    InputStream inputStream;
    OutputStream outputStream;
    SerialPort serialPort;
    Scanner scanner;

    public Terminal(CommPortIdentifier portId, Scanner scanner) {
        this.scanner = scanner;
        try {
            serialPort = (SerialPort) portId.open("TerminalApp", 2000);
        } catch (PortInUseException e) {
        }
        try {
            outputStream = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
        }

        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) {
        }
        serialPort.notifyOnDataAvailable(true);

        try {
            serialPort.setSerialPortParams(9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {
        }
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine();
                Frame frame = new Frame((byte)0, (byte)1, (byte)2, line);
                if (line.equals("exit")) System.exit(1);
                outputStream.write(frame.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
                }

                int size = 0;
                byte[] buffer;

                if (readBuffer[0] == -1) {
                    for (int i = 1; i < readBuffer.length; i++) {
                        if (readBuffer[i] == -1) {
                            size = i;
                            break;
                        }
                    }
                }

                if (size != 0) {
                    buffer = new byte[size + 1];
                    System.arraycopy(readBuffer, 0, buffer, 0, size + 1);
                    new Frame(buffer);
                }

                break;
        }
    }
}