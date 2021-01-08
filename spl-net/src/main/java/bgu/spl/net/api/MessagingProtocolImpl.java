package bgu.spl.net.api;

import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.Message.*;
import bgu.spl.net.impl.Message.Error;

import javax.swing.*;

public class MessagingProtocolImpl implements MessagingProtocol<Message>{

    private Database myData = Database.getInstance();
    private static String myUser= null;
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
                        myUser = userName;
                        response = myData.logIn(userName,password);
                    }
                    else
                        response = new Error(opCode);

                    break;
            }
        }

        if(opCode==4){
            response = myData.logOut(myUser);
            shouldTerminate=true;
        }

       if(opCode == 5||opCode == 6||opCode == 7||opCode == 9||opCode == 10) {
           int courseNum;
           switch (opCode) {
               case 5:
                   courseNum = ((CourseReg) msg).getCourseNum();
                   response = myData.courseReg(myUser, courseNum,myUser);
                   break;
               case 6:
                   courseNum = ((KdamCheck) msg).getCourseNum();
                   response = myData.kdamCheck(myUser, courseNum, myUser);
                   break;
               case 7:
                   courseNum = ((CourseStat) msg).getCourseNum();
                   response = myData.courseStat(courseNum, myUser);
                   break;
               case 9:
                   courseNum = ((IsRegistered) msg).getCourseNum();
                   response = myData.isRegistered(myUser, courseNum,myUser);
                   break;
               case 10:
                   courseNum = ((UnRegister) msg).getCourseNum();
                   response = myData.unRegister(myUser, courseNum,myUser);
                   break;
           }
       }

          if(opCode==8){
              userName = ((StudentStat)msg).getUserName();
              response = myData.studentStat(userName, myUser);
              }

          if(opCode==11){
              response = myData.myCourses(myUser, myUser);
              System.out.println(response+"msg encdnc 78");
          }

        return response;
    }


    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
