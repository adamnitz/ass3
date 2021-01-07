package bgu.spl.net.api;

import bgu.spl.net.impl.Message.*;
import bgu.spl.net.impl.Message.Error;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10];//check how many bites
    private int len = 0;
    short opCode;
    int fullOpcode = 0;
    int zeroCounter = 0;
    int bytesCounter = 0;
    byte[] twoFirstBytes = new byte[2];



    @Override
    public Message decodeNextByte(byte nextByte) {

        String msgAsStr = "";
        pushByte(nextByte);

        //read the opcode
        if (fullOpcode == 0) {
            twoFirstBytes[0] = nextByte;
            fullOpcode++;
           // bytesCounter++;

        }
        else if (fullOpcode == 1) {
            twoFirstBytes[1] = nextByte;
            fullOpcode++;
           // bytesCounter++;
        }
      else if (fullOpcode == 2) {
            opCode = bytesToShort(twoFirstBytes);
            System.out.println("opCode" + opCode);

            if (opCode == 1 || opCode == 2 || opCode == 3) {
                if (zeroCounter == 1 &&nextByte == '\0' ) {
                    msgAsStr = popString();
                    zeroCounter++;
                    return strToMsg(opCode, msgAsStr);
                }
                if (nextByte == '\0') {
                    zeroCounter++;
                }
            } else if (opCode == 8 || opCode == 12) {
                if (zeroCounter == 1) {
                    msgAsStr = popString();
                    return strToMsg(opCode, msgAsStr);
                }
                if (nextByte == '\0')
                    zeroCounter++;
            } else if (opCode == 4 || opCode == 11) {
                if (len == 2) {
                    msgAsStr = popString();
                    return strToMsg(opCode, msgAsStr);
                }
               // bytesCounter++;
            } else if (opCode == 5 || opCode == 6 || opCode == 7 || opCode == 9 || opCode == 10 ) {
                System.out.println("in else if");
                if (len == 4) {
                    msgAsStr = popString();
                    System.out.println(msgAsStr);
                    return strToMsg(opCode, msgAsStr);
                }
               // bytesCounter++;
            }

        }
        //pushByte(nextByte);
        //bytesCounter++;
        return null;

    }


    public Message strToMsg(short opCode, String msg) {
        String userName = "";
        String password = "";
        int counter = 2;
        if (opCode == 1 || opCode == 2 || opCode == 3) {
            while (counter < msg.length() && msg.charAt(counter) != '\0' )
                counter++;
            userName = msg.substring(2, counter);
            counter = counter +1;
            int firstCharOfPass = counter;
            while (counter < msg.length() && msg.charAt(counter) != '\0' ){
                counter++;
            }
            password = msg.substring(firstCharOfPass, counter);
            switch (opCode) {
                case 1:
                    Message adminReg = new AdminReg(opCode, userName, password);
                    return adminReg;
                case 2:
                    Message studentReg = new StudentReg(opCode, userName, password);
                    return studentReg;
                case 3:
                    Message logIn = new LogIn(opCode, userName, password);
                    return logIn;
            }
        } else if (opCode == 4) {
            Message logOut = new LogOut(opCode);
            return logOut;
        } else if (opCode == 5 || opCode == 6 || opCode == 7 || opCode == 9 || opCode == 10) {
            //System.out.println("msg1 " + msg.substring(2,3));
           // System.out.println("msg2 " + msg.substring(2,4));
            int courseNum = Integer.parseInt(msg.substring(2));
           // System.out.println("courseNum" + courseNum);
            switch (opCode) {
                case 5:
                    Message courseReg = new CourseReg(opCode, courseNum);
                    return courseReg;
                case 6:
                    Message kdamCheck = new KdamCheck(opCode, courseNum);
                    return kdamCheck;
                case 7:
                    Message courseStat = new CourseStat(opCode, courseNum);
                    return courseStat;
                case 9:
                    Message isRegistered = new IsRegistered(opCode, courseNum);
                    return isRegistered;
                case 10:
                    Message unRegister = new UnRegister(opCode, courseNum);
                    return unRegister;
            }
        }else if(opCode==8){
            counter=0;
            while(msg.charAt(counter)!='0' && counter<msg.length())
                counter ++;
            userName = msg.substring(2,counter);
            Message studentStat = new StudentStat(opCode, userName);
            return studentStat;
        }
        else if (opCode == 11) {
            Message myCourses = new MyCourses(opCode);
            return myCourses;
        }
        return null;
    }

    public byte[] encode(Message message) {
        short opCodeMsg;
        String string="";

        short opCodeAck=12;
        short opCodeErr=13;

        byte [] bytesToClient = null;
        byte [] opCodeArr;
        byte [] opCodeMsgArr;
        if(message instanceof Ack){
            opCodeArr = shortToBytes(opCodeAck);
            opCodeMsg = (short)(((Ack) message).getOpCode());
            opCodeMsgArr =  shortToBytes(opCodeMsg);
            bytesToClient = mergeBytes(opCodeArr, opCodeMsgArr);
            if(opCode == 6){
                string = string + ((Ack) message).getMyCourses();
                bytesToClient = mergeBytes(bytesToClient, string.getBytes());
            }
            if(opCode == 7 || opCode == 8 || opCode == 9){
                string = string + ((Ack) message).getData();
                bytesToClient = mergeBytes(bytesToClient, string.getBytes());

            }
        }
        else if(message instanceof Error){
            opCodeArr = shortToBytes(opCodeErr);
            opCodeMsg =  (short)(((Error) message).getOpCode());
            opCodeMsgArr =  shortToBytes(opCodeMsg);
            bytesToClient = mergeBytes(opCodeArr, opCodeMsgArr);
        }

        System.out.println("");
        for(int i=0 ;i <bytesToClient.length; i++){
            System.out.print(bytesToClient[i] + ", ");
        }
         return bytesToClient;
    }


    public byte[] mergeBytes(byte [] arr1,byte [] arr2 ){
        int len = arr1.length + arr2.length;
        byte [] merge = new byte[len];
        int i=0;
        for(int j=0 ;j<arr1.length; j++){
            merge[i] = arr1[j];
            i++;
        }

        for(int j=0; j< arr2.length; j++){
            merge[i]=arr2[j];
            i++;
        }

        return merge;
    }

    public void pushByte(byte nextByte){
        if(len>=bytes.length){
            bytes = Arrays.copyOf(bytes, len*2);
        }

        bytes[len++] = nextByte;
    }

    public String popString(){
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len=0;
        return result;
    }

    public short bytesToShort(byte [] byteArr){
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

}
