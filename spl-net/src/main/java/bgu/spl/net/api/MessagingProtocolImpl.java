package bgu.spl.net.api;

import bgu.spl.net.impl.Database;
import bgu.spl.net.impl.Message.*;

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
           int courseNum;

           switch(opCode){
                case 5:
                    courseNum = ((CourseReg)msg).getCourseNum();
                    response = myData.courseReg(myUser,courseNum);//todo: check when test if myuser is not null
                    break;
                case 6:
                    courseNum = ((KdamCheck)msg).getCourseNum();
                    response = myData.kdamCheck(myUser,courseNum);
                    break;
                case 7:
                    courseNum = ((CourseStat)msg).getCourseNum();
                    response = myData.courseStat(courseNum);
                    break;
                case 9:
                    courseNum = ((IsRegistered)msg).getCourseNum();
                    response = myData.isRegistered(myUser,courseNum);
                    break;
                case 10:
                    courseNum = ((UnRegister)msg).getCourseNum();
                    response =myData.unRegister(myUser,courseNum);
                    break;
            }

          if(opCode==8){
              userName = ((StudentStat)msg).getUserName();
              response = myData.studentStat(userName);
              }
          }
          if(opCode==11){
              response = myData.myCourses(myUser);
          }

        return response;
    }


    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
