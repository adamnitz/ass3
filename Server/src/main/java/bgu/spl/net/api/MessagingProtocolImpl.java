package bgu.spl.net.api;

import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.Message.*;
import bgu.spl.net.impl.Message.Error;

import javax.swing.*;

public class MessagingProtocolImpl implements MessagingProtocol<Message>{

    private Database myData = Database.getInstance();
    private String myUser= null;
    private Message response = null;
    private boolean shouldTerminate = false;

    @Override
    public Message process(Message msg) {
        int opCode = msg.getOpCode();
        String userName = "";
        String password = "";
        if(opCode==1||opCode==2||opCode== 3){
            switch (opCode){
                case 1:
                    userName = ((AdminReg)msg).getUserName();
                    password = ((AdminReg)msg).getPassword();
                    response = myData.adminReg(userName,password);
                    break;
                case 2:
                    userName = ((StudentReg)msg).getUserName();
                    password = ((StudentReg)msg).getPassword();
                    response = myData.studentReg(userName,password);
                    break;
                case 3:
                    userName = ((LogIn)msg).getUserName();
                    password = ((LogIn)msg).getPassword();

                    if(myUser==null){
                        // if(myUser!=null){
                    //if(!myData.findUser(myUser).isLogIn())
                        response = myData.logIn(userName,password);
                       if(response instanceof Ack){
                           myUser = userName;
                       }
                    }
                    else
                        response = new Error(opCode);

                    break;
            }
        }

        if(opCode==4){
            if(myUser!=null) {
                response = myData.logOut(myUser);
                shouldTerminate = true;
            }
            else
                response = new Error(opCode);
        }

       if(opCode == 5||opCode == 6||opCode == 7||opCode == 9||opCode == 10) {
           int courseNum;
           switch (opCode) {
               case 5:
                   if(myUser!=null) {
                       courseNum = ((CourseReg) msg).getCourseNum();
                       response = myData.courseReg(myUser, courseNum, myUser);
                   }
                   else
                       return new Error(opCode);
                   break;
               case 6:
                   if (msg instanceof Error)
                       return msg;
                   if(myUser!=null) {
                       courseNum = ((KdamCheck) msg).getCourseNum();
                       response = myData.kdamCheck(myUser, courseNum, myUser);
                   }
                   else
                       return new Error(opCode);
                   break;
               case 7:
                   courseNum = ((CourseStat) msg).getCourseNum();
                   response = myData.courseStat(courseNum, myUser);
                   break;
               case 9:
                   if(myUser!=null) {
                       courseNum = ((IsRegistered) msg).getCourseNum();
                       response = myData.isRegistered(myUser, courseNum, myUser);
                   }
                   else
                       return new Error(opCode);
                   break;
               case 10:
                   if(myUser!=null) {
                       courseNum = ((UnRegister) msg).getCourseNum();
                       response = myData.unRegister(myUser, courseNum, myUser);
                   }
                   else
                       return new Error(opCode);
                   break;
           }
       }

          if(opCode==8){
              if(myUser!=null){
              userName = ((StudentStat)msg).getUserName();
              response = myData.studentStat(userName, myUser);
              }
              else
                  return new Error(opCode);
              }

          if(opCode==11){
              if(myUser!=null)
                 response = myData.myCourses(myUser, myUser);
              else
                  return new Error(opCode);
          }

        return response;
    }


    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
