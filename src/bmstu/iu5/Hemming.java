package bmstu.iu5;

public class Hemming {

    boolean errorHead = false, errorData = false;

    public byte[] checkbyte(byte[] frame){
        String str = "", str1 = "", str2 = "", str3 = "";
        int[] arr = new int[16];
        if(frame.length == 9){
            str2 = Integer.toBinaryString(frame[7]);
            int count = 8 - str2.length();
            for(int j = 0; j < count; j++){
                str3 += 0;
            }
            str += str3 + str2;
            for(int i=0;i<arr.length;i++){
                if(i>7){
                    arr[i] = 0;continue;
                }
                arr[i] = Character.getNumericValue(str.charAt(i));
            }
        }
        if(frame.length > 9){
            for(int i = 7; i < 9; i++){
                str2 = Integer.toBinaryString(frame[i]);
                int count = 8 - str2.length();
                for(int j = 0; j < count; j++){
                    str3 += 0;
                }
                str += str3 + str2;
                str2 = ""; str3 = "";
            }
            for(int i=0;i<arr.length;i++){
                arr[i] = Character.getNumericValue(str.charAt(i));
            }
            str2 = ""; str3 = ""; str1 = "";
            for(int i=9;i<frame.length-1;i++){
                str2 = Integer.toBinaryString(frame[i]);
                int str2_lenght = 8 - str2.length();
                for(int j=0;j<str2_lenght;j++){
                    str3 += "0";
                }
                str1 += str3 + str2;
                str3 = "";
                if(str1.length() == 8 && i == frame.length - 2){
                    str1 += "00000000";
                }
                if(str1.length() == 16){
                    for(int k=0;k<str1.length();k++){
                        arr[k] = (arr[k] ^ Character.getNumericValue(str1.charAt(k)));
                    }
                    str1 = "";
                }
            }
        }
        str = "";
        int k = 5;
        for(int g = 0; g < arr.length; g++){
            str += arr[g];
            if(str.length() == 8){
                frame[k] = (byte)Integer.parseInt(str, 2);
                str = "";
                k++;
            }
        }
        return frame;
    }
    public byte[] encrypted(byte[] frame){
        char[] char_frame = new char[63];
        String str = "", str2 = "", str1 = "";
        for(int i = 0; i < 7; i++){
            str2 = "";
            str1 = Integer.toBinaryString(frame[i]);
            if(frame[i] < 0) str1 = str1.substring(24, 32);
            int count = 8 - str1.length();
            for(int j = 0; j < count; j++){
                str2 += "0";
            }
            str += str2 + str1;
        }
        str = '0' + str;
        str = str.substring(0, 1) + '0' + str.substring(1, str.length());
        str = str.substring(0, 3) + '0' + str.substring(3, str.length());
        str = str.substring(0, 7) + '0' + str.substring(7, str.length());
        str = str.substring(0, 15) + '0' + str.substring(15, str.length());
        str = str.substring(0, 31) + '0' + str.substring(31, str.length());
        str += 0;
        for (int i = 0; i < str.length(); i++){
            char_frame[i] = str.charAt(i);
        }
        int temp[] = new int[]{Character.getNumericValue(char_frame[2]), Character.getNumericValue(char_frame[2]),Character.getNumericValue(char_frame[4]), Character.getNumericValue(char_frame[8]),
                Character.getNumericValue(char_frame[16]), Character.getNumericValue(char_frame[32]), Character.getNumericValue(char_frame[62])};
        //вычисляем значения 001, 010, 100,... и сохраняем их в массив temp
        for (int i = 4; i < char_frame.length; i++){
            String num = Integer.toBinaryString(i + 1);
            if(i == 7 || i == 15 || i == 31 || i == 63)
                continue;
            if(num.charAt(num.length() - 1) == '1')
                temp[0] = temp[0] ^ Character.getNumericValue(char_frame[i]);
            if(num.length() > 1 && num.charAt(num.length() - 2) == '1')
                temp[1] = temp[1] ^ Character.getNumericValue(char_frame[i]);
            if(i == 4)
                continue;
            if(num.length() > 2 && num.charAt(num.length() - 3) == '1')
                temp[2] = temp[2] ^ Character.getNumericValue(char_frame[i]);
            if(i == 8)
                continue;
            if((num.length() > 3) && (num.charAt(num.length() - 4) == '1'))
                temp[3] = temp[3] ^ Character.getNumericValue(char_frame[i]);
            if(i == 16)
                continue;
            if((num.length() > 4) && (num.charAt(num.length() - 5) == '1'))
                temp[4] = temp[4] ^ Character.getNumericValue(char_frame[i]);
            if(i == 32)
                continue;
            if((num.length() > 5) && (num.charAt(num.length() - 6) == '1'))
                temp[5] = temp[5] ^ Character.getNumericValue(char_frame[i]);
        }
        str1 = "";
        for (int i = 0; i < temp.length; i++){
            str1 += temp[i];
        }
        char_frame[0] = str1.charAt(0);
        char_frame[1] = str1.charAt(1);
        char_frame[3] = str1.charAt(2);
        char_frame[7] = str1.charAt(3);
        char_frame[15] = str1.charAt(4);
        char_frame[31] = str1.charAt(5);
        str = "";
        for (int i = 0; i < char_frame.length; i++){
            str += char_frame[i];
        }
        byte[] temp1 = new byte[frame.length - 8];
        for(int i=7; i<frame.length-1; i++){
            temp1[i-7] = frame[i];
        }
        str1 = "";
        int k = 0;
        str += 0;
        for (int i = 0; i < str.length(); i++){
            str1 += str.charAt(i);
            if(str1.length() == 8){
                frame[k] = (byte)Integer.parseInt(str1, 2);
                k++;
                str1 = "";
            }
        }
        for(int i=8; i<frame.length; i++){
            frame[i] = temp1[i-8];
        }
        return frame;
    }
    public byte[] decryption(byte[] frame){
        String str = "", str1 = "", str2 = "";
        char[] char_frame = new char[63];

        for(int i = 0; i < 8; i++){
            str2 = "";
            str1 = Integer.toBinaryString(frame[i]);
            if (frame[i] < 0){
                int d = 255 + frame[i] + 1;
                str1 = Integer.toBinaryString(d);
            }
            int count = 8 - str1.length();
            for(int j = 0; j < count; j++){
                str2 += "0";
            }
            str += str2 + str1;
        }
        if(Character.getNumericValue(str.charAt(63)) == 1){
            System.out.println("Ошибка");
            errorHead = true;
        }
        else errorHead = false;
        for (int i = 0; i < str.length() - 1; i++){
            char_frame[i] = str.charAt(i);
        }
        int temp[] = new int[]{Character.getNumericValue(char_frame[0]), Character.getNumericValue(char_frame[1]),Character.getNumericValue(char_frame[3]), Character.getNumericValue(char_frame[7]),
                Character.getNumericValue(char_frame[15]), Character.getNumericValue(char_frame[31]), Character.getNumericValue(char_frame[62])};
        for (int i = 2; i < char_frame.length; i++){
            String num = Integer.toBinaryString(i + 1);
            if(num.charAt(num.length() - 1) == '1')
                temp[0] = temp[0] ^ Character.getNumericValue(char_frame[i]);
            if(num.length() > 1 && num.charAt(num.length() - 2) == '1')
                temp[1] = temp[1] ^ Character.getNumericValue(char_frame[i]);
            if(i == 3)
                continue;
            if(num.length() > 2 && num.charAt(num.length() - 3) == '1'){
                temp[2] = temp[2] ^ Character.getNumericValue(char_frame[i]);
            }
            if(i == 7)
                continue;
            if((num.length() > 3) && (num.charAt(num.length() - 4) == '1')){
                temp[3] = temp[3] ^ Character.getNumericValue(char_frame[i]);
            }
            if(i == 15)
                continue;
            if((num.length() > 4) && (num.charAt(num.length() - 5) == '1'))
                temp[4] = temp[4] ^ Character.getNumericValue(char_frame[i]);
            if(i == 31)
                continue;
            if((num.length() > 5) && (num.charAt(num.length() - 6) == '1'))
                temp[5] = temp[5] ^ Character.getNumericValue(char_frame[i]);
        }
        int k = 0;
        for (int i = 0; i < 7; i++){
            if (temp[i] == 1)
                k++;
        }
        if(k == 0 && !errorHead) {
            //System.out.println("Ошибки нет");
        }
        else {
            System.out.println("Ошибка есть");
            errorHead = true;
        }
        str = "";
        for (int i = 0; i < char_frame.length; i++){
            if(i == 0 || i == 1 || i == 3 || i == 7 || i == 15 || i == 31 || i == 63)
                continue;
            str += char_frame[i];
        }
        str1 = "";
        k = 0;
        for (int i = 0; i < str.length(); i++){
            str1 += str.charAt(i);
            if(str1.length() == 8) {
                frame[k] = (byte)Integer.parseInt(str1, 2);
                k++;
                str1 = "";
            }
        }
        for(int i = 7; i < frame.length - 1;){
            frame[i] = frame[++i];
        }
        String str3 = "";
        str = "";
        for(int i = 7; i < 9; i++){
            str2 = Integer.toBinaryString(frame[i]);
            int count = 8 - str2.length();
            for(int j = 0; j < count; j++){
                str3 += 0;
            }
            str += str3 + str2;
            str2 = ""; str3 = "";
        }
        int[] arr = new int[16];
        for(int i=0;i<arr.length;i++){
            if(frame.length == 9 && i > 7){
                arr[i] = 0; continue;
            }
            arr[i] = Character.getNumericValue(str.charAt(i));
        }
        str2 = "";
        str1 = "";
        str3 = "";
        for(int i=9;i<frame.length - 1;i++){
            str2 = Integer.toBinaryString(frame[i]);
            int str2_lenght = 8 - str2.length();
            for(int j=0;j<str2_lenght;j++){
                str3 += "0";
            }
            str1 += str3 + str2;
            str3 = "";
            if((str1.length() == 8 && i == frame.length - 2) || (frame.length == 9)){
                str1 += "00000000";
            }
            if(str1.length() == 16){
                for(int d=0;d<str1.length();d++){
                    arr[d] = (arr[d] ^ Character.getNumericValue(str1.charAt(d)));
                }
                str1 = "";
            }
        }
        str1 = ""; str2 = ""; str3 = "";
        for(int i = 5; i < 7; i++){
            str2 = Integer.toBinaryString(frame[i]);
            int count = 8 - str2.length();
            for(int j = 0; j < count; j++){
                str3 += 0;
            }
            str1 += str3 + str2;
            str2 = ""; str3 = "";
        }

        errorData = false;
        for(int i=0;i<arr.length;i++){
            if(arr[i] != Character.getNumericValue(str1.charAt(i))) {
                System.out.println("Ошибка!!!");
                errorData = true;
                break;
            }
        }
        return frame;
    }
    public byte[] encrypted3(byte[] frame){
        char[] char_frame = new char[29];
        String str = "", str2 = "", str1 = "";
        for(int i = 0; i < 3; i++){
            str2 = "";
            str1 = Integer.toBinaryString(frame[i]);
            if (frame[i] < 0) str1 = str1.substring(24, 32);
            int count = 8 - str1.length();
            for(int j = 0; j < count; j++){
                str2 += "0";
            }
            str += str2 + str1;
        }
        str1 = ""; str2 = "";
        str = '0' + str;
        str = str.substring(0, 1) + '0' + str.substring(1, str.length());
        str = str.substring(0, 3) + '0' + str.substring(3, str.length());
        str = str.substring(0, 7) + '0' + str.substring(7, str.length());
        str = str.substring(0, 15) + '0' + str.substring(15, str.length());
        for (int i = 0; i < str.length(); i++){
            char_frame[i] = str.charAt(i);
        }
        int temp[] = new int[]{Character.getNumericValue(char_frame[2]), Character.getNumericValue(char_frame[2]),Character.getNumericValue(char_frame[4]), Character.getNumericValue(char_frame[8]),
                Character.getNumericValue(char_frame[16])};
        //вычисляем значения 001, 010, 100,... и сохраняем их в массив temp
        for (int i = 4; i < char_frame.length; i++){
            String num = Integer.toBinaryString(i + 1);
            if(i == 7 || i == 15 || i == 31 || i == 63)
                continue;
            if(num.charAt(num.length() - 1) == '1')
                temp[0] = temp[0] ^ Character.getNumericValue(char_frame[i]);
            if(num.length() > 1 && num.charAt(num.length() - 2) == '1')
                temp[1] = temp[1] ^ Character.getNumericValue(char_frame[i]);
            if(i == 4)
                continue;
            if(num.length() > 2 && num.charAt(num.length() - 3) == '1')
                temp[2] = temp[2] ^ Character.getNumericValue(char_frame[i]);
            if(i == 8)
                continue;
            if((num.length() > 3) && (num.charAt(num.length() - 4) == '1'))
                temp[3] = temp[3] ^ Character.getNumericValue(char_frame[i]);
            if(i == 16)
                continue;
            if((num.length() > 4) && (num.charAt(num.length() - 5) == '1'))
                temp[4] = temp[4] ^ Character.getNumericValue(char_frame[i]);
        }
        str1 = "";
        for (int i = 0; i < temp.length; i++){
            str1 += temp[i];
        }
        char_frame[0] = str1.charAt(0);
        char_frame[1] = str1.charAt(1);
        char_frame[3] = str1.charAt(2);
        char_frame[7] = str1.charAt(3);
        char_frame[15] = str1.charAt(4);
        str = "";
        for (int i = 0; i < char_frame.length; i++){
            str += char_frame[i];
        }
        str1 = "";
        int k = 0;
        str += "000";
        for (int i = 0; i < 32; i++){
            str1 += str.charAt(i);
            if(str1.length() == 8){
                frame[k] = (byte)Integer.parseInt(str1, 2);
                k++;
                str1 = "";
            }
        }
        return frame;
    }
    public byte[] decription3(byte[] frame){
        String str = "", str1 = "", str2 = "";
        char[] char_frame = new char[30];

        for(int i = 0; i < 4; i++){
            str2 = "";
            str1 = Integer.toBinaryString(frame[i]);
            if (frame[i] < 0){
                int d = 255 + frame[i] + 1;
                str1 = Integer.toBinaryString(d);
            }
            int count = 8 - str1.length();
            for(int j = 0; j < count; j++){
                str2 += "0";
            }
            str += str2 + str1;
        }
        if(Character.getNumericValue(str.charAt(30)) == 1 || Character.getNumericValue(str.charAt(31)) == 1)
            System.out.println("Ошибка");
        for (int i = 0; i < str.length() - 2; i++){
            char_frame[i] = str.charAt(i);
        }
        int temp[] = new int[]{Character.getNumericValue(char_frame[0]), Character.getNumericValue(char_frame[1]),Character.getNumericValue(char_frame[3]), Character.getNumericValue(char_frame[7]),
                Character.getNumericValue(char_frame[15])};
        for (int i = 2; i < char_frame.length; i++){
            String num = Integer.toBinaryString(i + 1);
            if(num.charAt(num.length() - 1) == '1')
                temp[0] = temp[0] ^ Character.getNumericValue(char_frame[i]);
            if(num.length() > 1 && num.charAt(num.length() - 2) == '1')
                temp[1] = temp[1] ^ Character.getNumericValue(char_frame[i]);
            if(i == 3)
                continue;
            if(num.length() > 2 && num.charAt(num.length() - 3) == '1'){
                temp[2] = temp[2] ^ Character.getNumericValue(char_frame[i]);
            }
            if(i == 7)
                continue;
            if((num.length() > 3) && (num.charAt(num.length() - 4) == '1')){
                temp[3] = temp[3] ^ Character.getNumericValue(char_frame[i]);
            }
            if(i == 15)
                continue;
            if((num.length() > 4) && (num.charAt(num.length() - 5) == '1'))
                temp[4] = temp[4] ^ Character.getNumericValue(char_frame[i]);
        }
        int k = 0;
        for (int i = 0; i < 5; i++){
            if (temp[i] == 1)
                k++;
        }
        if(k == 0) {
            //System.out.println("Ошибки нет");
        }
        else
            System.out.println("Ошибка есть");
        str = "";
        for (int i = 0; i < char_frame.length; i++){
            if(i == 0 || i == 1 || i == 3 || i == 7 || i == 15)
                continue;
            str += char_frame[i];
        }
        str1 = "";
        k = 0;
        for (int i = 0; i < str.length(); i++){
            str1 += str.charAt(i);
            if(str1.length() == 8) {
                frame[k] = (byte)Integer.parseInt(str1, 2);
                k++;
                str1 = "";
            }
        }
        return frame;
    }
}
