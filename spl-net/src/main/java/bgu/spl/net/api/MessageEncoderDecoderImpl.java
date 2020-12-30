package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder{

    private byte[] bytes = new byte[1<<10];//check how many bites
    private int len=0;
    short opCode;
    int fullOpcode=0;
    int zeroCounter = 0;
    int bytesCounter = 0;
    byte[] twoFirstBytes;


    @Override
    public String decodeNextByte(byte nextByte) {

        //read the opcode
        if(fullOpcode==0) {
            twoFirstBytes[0] = nextByte;
            fullOpcode++;
            bytesCounter++;
        }
        if(fullOpcode==1){
            twoFirstBytes[1] = nextByte;
            fullOpcode++;
            bytesCounter++;
        }

        //know when the message over by the opcode
        else if(fullOpcode == 2) {
            opCode = bytesToShort(twoFirstBytes);
            if (opCode == 1 || opCode == 2 || opCode == 3  ) {
                if (zeroCounter == 2)
                    return popString();
                if (nextByte == '\0')
                    zeroCounter++;
            } else if (opCode == 8 ||opCode == 12){
                if (zeroCounter == 1)
                    return popString();
                if (nextByte == '\0')
                    zeroCounter++;
            } else if (opCode == 4 || opCode == 11) {
                if (bytesCounter == 2)
                    return popString();
                bytesCounter++;
            } else if (opCode == 5 || opCode == 6 || opCode == 7 ||opCode == 9 || opCode == 10 ||opCode == 13) {
                if (bytesCounter == 4)
                    return popString();
                bytesCounter++;
            }
        }
        pushByte(nextByte);
        return null;
    }

    @Override
    public byte[] encode(Object message) {
        return (message+ "\n").getBytes();
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
