/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author apple
 */
public class DisplayText {
public static JFrame frame                      = new JFrame();

   public static void popupMessage(Object message){


         final String text  = message.toString();
         boolean isEDT      =EventQueue.isDispatchThread() ;

         if (isEDT){popupMessageOnEDT (text);}

         else {
           EventQueue.invokeLater(new Runnable()
		{
			public void run(){popupMessageOnEDT (text);}
		});
         }
    }
   public static void popupMessageOnEDT(Object message){


         String text  = message.toString();
                JOptionPane.showMessageDialog(
                                frame ,
                                text,
                                "Message",
                                JOptionPane.INFORMATION_MESSAGE);

    }


   public static void popupWarningMessage(Object message){


         final String text  = message.toString();
         boolean isEDT      =EventQueue.isDispatchThread() ;

         if (isEDT){popupMessageOnEDT (text);}

         else {
           EventQueue.invokeLater(new Runnable()
		{
			public void run(){popupWarningMessageOnEDT (text);}
		});
         }
    }
   private static void popupWarningMessageOnEDT(Object message){


         String text  = message.toString();
                JOptionPane.showMessageDialog(
                                 frame,
                                text,
                                "Message",
                                JOptionPane.WARNING_MESSAGE);

    }


   public static void  popupErrorMessage(Object message){


         final String text  = message.toString();
         boolean isEDT      =EventQueue.isDispatchThread() ;

         if (isEDT){popupErroressageOnEDT(text);}

         else {
           EventQueue.invokeLater(new Runnable()
		{
			public void run(){popupErroressageOnEDT (text);}
		});
         }
    }
   private static void  popupErroressageOnEDT(Object message){


         String text  = message.toString();
                JOptionPane.showMessageDialog(
                                frame,
                                text,
                                "Message",
                                JOptionPane.ERROR_MESSAGE);

    }


   public static String  popupUserInput(Object message, String title){
       return  popupUserInput(null, message, title);
}
   public static String  popupUserInput(Component comp, Object message, String title){
     String s = (String)JOptionPane.showInputDialog(
                    comp,
                    message,
                    title,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null
                    );

       return s;
}


   public static boolean popupDialog(String text){

         boolean shoudProceed = true;
         int n = JOptionPane.showConfirmDialog(
                    new  javax.swing.JFrame(),
                    text,
                    "Message",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);


         if (n  ==  JOptionPane.NO_OPTION ){shoudProceed = false;}

         return shoudProceed;
    }

   public static boolean popupYesNoDialog(String text, String title, boolean defProoced ){
     Object[] options = { "OK", "CANCEL" };
     int initOpt        =   (defProoced)? 0:1;

     int out    =   JOptionPane.showOptionDialog(
                        null,
                        text,
                        title,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[initOpt]);


       
     if(out  ==  0 ){
        return true;
     }
    else {
        return false;
     }
   }

   public static void main(String ares []){
        boolean out = popupYesNoDialog("text", "title", false);
        System.exit(0);
   }
}
