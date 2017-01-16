import java.io.IOException;

import com.leapmotion.leap.Controller;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
/**NOTE!!!
 * Testing shows that a good control scheme may involve pitch, yaw, and roll while not making a fist.
 * X, Y and height would be used while making a fist, as this yields higher accuracy.
 * Also remember that Y values transition smoothly and slowly, while everything else is snappy.
 * Pinching could be used for any other function, but it can only be activated while not grabbing.
**/
public class LeapStick {
	public static NetworkTable gunnerTable;
	public static void main(String[] args) {
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress("10.42.56.2");
		NetworkTable.globalDeleteAll();
		gunnerTable = NetworkTable.getTable("gunnerTable");
        //Create a listener and controller
		Controller leapGun = new Controller();
		//Allow background frame processing
		leapGun.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
        LeapListener leapGunListener = new LeapListener();
        //Have the listener receive events from the controller
        leapGun.addListener(leapGunListener);

        //Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Remove the listener when done
        leapGun.removeListener(leapGunListener);
    }
}
