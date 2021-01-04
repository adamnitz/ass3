package bgu.spl.net.api;

import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.Message.Message;

public class MessagingProtocolImpl implements MessagingProtocol<String{

    private Database myData = Database.getInstance();
    private String myUser= null;
    private Message response = null;
    private boolean shouldTerminate = false;

    @Override
    public Message process(String msg) {
        int opCode = (msg.charAt(0) + msg.charAt(1));
        String userName = "";
        String password = "";
        int counter=2;
        if(opCode==1||opCode==2||opCode== 3){
            while(msg.charAt(counter)!='0' && counter<msg.length())
                counter ++;
            userName = msg.substring(2,counter);
            counter=counter+2;
            int firstCharOfPass=counter;
            while(msg.charAt(counter)!='0' && counter<msg.length())
                counter ++;
            password = msg.substring(firstCharOfPass,counter);
            switch (opCode){
                case 1:
                    response = myData.adminReg(userName,password);
                    break;
                case 2:
                    response = myData.studentReg(userName,password);
                    break;
                case 3:
                    response = myData.logIn(userName,password);
                    myUser = userName;
                    break;
            }
        }
        if(opCode==4){
            response = myData.logOut(myUser);
            shouldTerminate=true;
        }


       if(opCode == 5||opCode == 6||opCode == 7||opCode == 9||opCode == 10){
           int courseNum = Integer.parseInt(msg.substring(2,msg.length()));
            switch(opCode){
                case 5: response = myData.courseReg(myUser,courseNum);//todo: check when test if myuser is not null
                    break;
                case 6: response = myData.kdamCheck(myUser,courseNum);
                    break;
                case 7: response = myData.courseStat(courseNum);
                    break;
                case 9: response = myData.isRegistered(myUser,courseNum);
                    break;
                case 10: response =myData.unRegister(myUser,courseNum);

                    break;
            }

          if(opCode==8){
              counter=0;//todo: what about function ? find username+find password
              while(msg.charAt(counter)!='0' && counter<msg.length())
                  counter ++;
              userName = msg.substring(2,counter);
              switch (opCode){
                  case 8:response = myData.studentStat(userName);
                        break;
              }
          }
          if(opCode==11){
              response = myData.myCourses(myUser);
          }
       }


        return response;
    }


    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
