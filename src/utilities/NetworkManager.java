/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author apple
 */
public class NetworkManager {
  private NetworkManager(){};

  public void          recordNetworkVariables (){
        try {
            InetAddress thisIp              = InetAddress.getLocalHost();
            String serverHostName           = thisIp.getHostName() ;
        }
        catch (UnknownHostException ex) {
            ex.printStackTrace();

        }
   }
}
