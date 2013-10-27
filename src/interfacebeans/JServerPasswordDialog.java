/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfacebeans;
import bayes.ApplicationPreferences;
import java.awt.Point;
import java.awt.MouseInfo;
import utilities.Server;
/**
 *
 * @author apple
 */
public class JServerPasswordDialog   extends JPasswordDialog {

    private static  JServerPasswordDialog instance = new  JServerPasswordDialog ();
    private JServerPasswordDialog (){
        super (null, true);
     }

    public static  JServerPasswordDialog getInstance(){
        if (instance == null ){instance = new  JServerPasswordDialog ();}
        instance.setSize(250, 50);
        return instance;
        //return new  JServerPasswordDialog ();
    }


     public static String    getServerPassword(){
        String password                 =   null;

        try{
            JPasswordDialog passwordDialog  =  JServerPasswordDialog.getInstance();
            passwordDialog.setOption(OK);

            Server server                   =  ApplicationPreferences.getCurrentServer();
            String servername               =  server.getName();
            boolean isPassword              =  server.isPassword();
            String  user                    =  server.getUser();

            if (isPassword){

                 if (passwordDialog.isPasswordNeedToBeSet() == true){
                    if (passwordDialog.getOption() == OK);

                    {

                        Point p = MouseInfo.getPointerInfo().getLocation();
                        passwordDialog.setLocation(p);
                        passwordDialog.setUser(user);
                        passwordDialog.setServer(servername);
                        passwordDialog.setVisible (true);
                        passwordDialog.setFocus();
                        password = passwordDialog.getPassword();
                    }

                  // String usernameFromGui =  passwordDialog.getUserName();
                  // if (usernameFromGui != null || usernameFromGui.isEmpty()){
                     //   server.setUser(usernameFromGui);
                   //}
                }
            }
        }
        catch(Exception e){e.printStackTrace();}
        finally{
             return password;
        }


    }
   
}
