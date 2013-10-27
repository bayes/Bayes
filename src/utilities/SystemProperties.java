/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 *
 * @author apple
 */
public class SystemProperties {

    public static String getUserHome(){
        return System.getProperty("user.home").toString();
    }
    public static String getUserDir(){
        return System.getProperty("user.dir").toString();
    }
    public static String getUserName(){

        return System.getProperty("user.name").toString();
    }
    public static String getOsName(){
         return System.getProperty("os.name").toString();
    }
    public static String getOsArch(){
        return System.getProperty("os.arch").toString();
    }
    public static String getSunArchDataModel(){
        return System.getProperty("sun.arch.data.model").toString();
    }
    public static String getOsVersion(){
        return System.getProperty("os.version").toString();
    }
    public static String getJavaSpecification(){
        return System.getProperty("java.specification.version ").toString();
    }
    public static String getHost (){
        String hostName                 = "Unknown";
        try {
            InetAddress thisIp          = InetAddress.getLocalHost();
            hostName                    = thisIp.getHostName() ;
        }
        catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        finally { return hostName;}
   }
    public static void   getSystemProperties(){
        Properties p = System.getProperties ();
        java.util.Enumeration en = p.propertyNames();

        for (; en .hasMoreElements(); ) {


            String propName = (String)en.nextElement();
            String propValue = (String)p.getProperty(propName);
            String s    =   String.format("%-30s = "+ propValue, propName);

            System.out.println(s);
        }
    }
    public static String  getSpecificSystemProperties(){
        Properties p = System.getProperties ();
        java.util.Enumeration en = p.propertyNames();

     
        StringBuilder sb        = new  StringBuilder();
        addProperty(p,  "user.name" , sb);
        addProperty(p,  "user.dir" , sb);
        addProperty(p,  "user.home" , sb);
        addProperty(p,  "os.arch" , sb);
        addProperty(p,  "os.name" , sb);
        addProperty(p,  "os.version" , sb);
        addProperty(p,  "file.separator" , sb);
        addProperty(p,  "file.encoding" , sb);


        addProperty(p,  "sun.arch.data.model" , sb);
        addProperty(p,  "java.home" , sb);
        addProperty(p,  "java.version" , sb);
        addProperty(p,  "java.library.path" , sb);
        addProperty(p,  "java.specification.version" , sb);
        addProperty(p,  "swing.aatext" , sb);

        addProperty(p,  "http.nonProxyHosts" , sb);
        addProperty(p,  "socksNonProxyHosts" , sb);
        addProperty(p,  "ftp.nonProxyHosts" , sb);

        try{
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            
             String displayname = "Display";
             String resolution  =   "";
            // Get size of each screen
            for (int i=0; i<gs.length; i++) {
                DisplayMode dm      = gs[i].getDisplayMode();
                int screenWidth     = dm.getWidth();
                int screenHeight    = dm.getHeight();
                displayname         =   displayname +"_"+ i;
                resolution          =   screenWidth + "x"+    screenHeight ; 
                addProperty( "Resolution for "+displayname , resolution, sb);
            }
         
        
        }
        catch(Exception ex){ex.printStackTrace();}
        
        return sb.toString();


    }
    public static void addProperty(Properties p, String key, StringBuilder sb){
        String value       =   p.getProperty(key );
        addProperty(key, value, sb);

    }
     public static void addProperty( String key, String value, StringBuilder sb){
        String f                =   "%-30s" ;
        String s                =   String.format(f+" = "+ value +"\n", key);
        sb.append(s);

    }


    public static void main (String [] ate){

    // getSystemProperties();
    // System.out.println(getSpecificSystemProperties());
        //int i = System.getProperty("line.separator"  ).codePointAt(0);
       //         char c = System.getProperty("line.separator"  ).charAt(0);
      //  System.out.println("c "+c+  (char)i);
       // getSystemProperties();
        System.out.println(getHost ());
    }
}
