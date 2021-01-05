package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.Database;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.MessagingProtocolImpl;
import bgu.spl.net.impl.Message.Message;
import bgu.spl.net.srv.Server;
import bgu.spl.net.srv.BlockingConnectionHandler;
import java.io.IOException;


public class mainTpc {

    public static void main(String[] args){

        Database database= Database.getInstance();

        try(Server<Message> srv = Server.threadPerClient(Integer.parseInt(args[0]), () -> new MessagingProtocolImpl(), () -> new MessageEncoderDecoderImpl()))
        {
            srv.serve();
        }

        catch (IOException e) {
                    System.out.println("IOException in mainTpc");
        }

        database.initialize("/.Courses.txt");

    }


}