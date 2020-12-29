package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder{
    @Override

    private byte[] bytes = new byte[1<<10];//check how many bites
    private int len=0;
    short opCode;
    int fullOpcode=0;
    int zeroCounter = 0;
    int bytesCounter = 0;

    public String decodeNextByte(byte nextByte) {
        //read the opcode
        if(fullOpcode==0) {
            opCode = nextByte;
            fullOpcode++;
            bytesCounter++;
        }
        if(fullOpcode==1){
            opCode = (short) (opCode+nextByte);
            fullOpcode++;
            bytesCounter++;
        }

        //know when the message over by the opcode
        else if(fullOpcode == 2) {
            if (opCode == 1) {
                if (zeroCounter == 2)
                    return popString();
                if (nextByte == '\0')
                    zeroCounter++;
            } else if (opCode == 2) {
                if (zeroCounter == 2)
                    return popString();
                if (nextByte == '\0')
                    zeroCounter++;
            } else if (opCode == 3) {
                if (zeroCounter == 2)
                    return popString();
                if (nextByte == '\0')
                    zeroCounter++;
            } else if (opCode == 4) {
                if (bytesCounter == 2)
                    return popString();
                bytesCounter++;
            } else if (opCode == 5) {
                if (bytesCounter == 4)
                    return popString();
                bytesCounter++;
            }
            else if (opCode == 6) {
                if (bytesCounter == 4)
                    return popString();
                bytesCounter++;
            }
            else if(opCode==7){
                if (bytesCounter == 4)
                    return popString();
                bytesCounter++;
            } else if (opCode == 8) {
                if(zeroCounter==1)
                    return popString();
                if (nextByte == '\0')
                    zeroCounter++;
            } else if(opCode == 9){
                if (bytesCounter == 4)
                    return popString();
                bytesCounter++;
            } else if(opCode == 10) {
                if (bytesCounter == 4)
                    return popString();
                bytesCounter++;
            }
            else if(opCode == 11) {
                if (bytesCounter == 2)
                    return popString();
                bytesCounter++;
            }else if (opCode == 12) {
                if(zeroCounter==1)
                    return popString();
                if (nextByte == '\0')
                    zeroCounter++;
            } else if(opCode == 13) {
                if (bytesCounter == 4)
                    return popString();
                bytesCounter++;
            }

        }
        pushByte(nextByte);
        return null;
    }


    @Override
    public byte[] encode(String message) {
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

}
