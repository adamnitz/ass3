package bgu.spl.net.api;

import bgu.spl.net.impl.Message.*;
import bgu.spl.net.impl.Message.Error;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
>
public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10];//check how many bites
    private int len = 0;
    short opCode;
    int fullOpcode = 0;
    int zeroCounter = 0;
    int bytesCounter = 0;
    byte[] twoFirstBytes;


    @Override
    public Message decodeNextByte(byte nextByte) {

        String msgAsStr = "";

        //read the opcode
        if (fullOpcode == 0) {
            twoFirstBytes[0] = nextByte;
            fullOpcode++;
            bytesCounter++;
        }
        if (fullOpcode == 1) {
            twoFirstBytes[1] = nextByte;
            fullOpcode++;
            bytesCounter++;
        } else if (fullOpcode == 2) {
            opCode = bytesToShort(twoFirstBytes);
            if (opCode == 1 || opCode == 2 || opCode == 3) {
                if (zeroCounter == 2) {
                    msgAsStr = popString();
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
                if (bytesCounter == 2) {
                    msgAsStr = popString();
                    return strToMsg(opCode, msgAsStr);
                }
                bytesCounter++;
            } else if (opCode == 5 || opCode == 6 || opCode == 7 || opCode == 9 || opCode == 10 || opCode == 13) {
                if (bytesCounter == 4) {
                    msgAsStr = popString();
                    return strToMsg(opCode, msgAsStr);
                }
                bytesCounter++;
            }

            pushByte(nextByte);
            return null;
        }
    }


    public Message strToMsg(short opCode, String msg) {
        String userName = "";
        String password = "";
        int counter = 2;
        if (opCode == 1 || opCode == 2 || opCode == 3) {
            while (msg.charAt(counter) != '0' && counter < msg.length())
                counter++;
            userName = msg.substring(2, counter);
            counter = counter + 2;
            int firstCharOfPass = counter;
            while (msg.charAt(counter) != '0' && counter < msg.length())
                counter++;
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
            int courseNum = Integer.parseInt(msg.substring(2));
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

    }



    @Override
    public byte[] encode(Message message) {
        String string="";

        if(message instanceof Ack){
            string = string + 12 + ((Ack) message).getOpCode();
            if(opCode == 6){
                string = string + ((Ack) message).getMyCourses();
            }
            if(opCode == 7 || opCode == 8 || opCode == 9){
                string = string + ((Ack) message).getData();
            }

        }
        else if(message instanceof Error){
            string = string+ 13 + ((Error) message).getOpCode();
        }

         return string.getBytes();
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

}
