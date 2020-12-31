package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.MessagingProtocolImpl;
import bgu.spl.net.impl.Message.Message;
import bgu.spl.net.srv.Server;

import java.io.IOException;

public class mainReactor {
    public static void main(String[] args){
        try(Server<Message> srv = Server.reactor(3, Integer.parseInt(args[0]),() -> new MessagingProtocolImpl(), () -> new MessageEncoderDecoderImpl()))
        {
            srv.serve();
        }
        catch (IOException e) {
            System.out.println("IOException in mainTpc");
        }


    }
}
