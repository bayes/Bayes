
/*
 * BayesApp.java
 */
package bayes;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class BayesApp extends SingleFrameApplication {

    BayesView bayesView;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        bayesView = new BayesView(this);
        show(bayesView);


    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
        //root.setSize(1200, 900);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        bayes.BayesManager.shutDownApplication();
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of BayesApp
     */
    public static BayesApp getApplication() {
        return Application.getInstance(BayesApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        System.setProperty("swing.aatext", "true");
        System.out.println("Maximum Requested Memory = " + Runtime.getRuntime().maxMemory());
        if (args != null) {
            for (String string : args) {
                System.out.println(string);
            }
        }
        ApplicationPreferences.setDefaultServer(args);
        Properties2Preferences.convertProperties2Preferences();

        launch(BayesApp.class, args);
    }
}
