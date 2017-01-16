import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

public class LeapListener extends Listener{
	private static final Vector vector = new Vector(1.0f, 1.0f, 1.0f);
	public void onInit(Controller controller) {
        LeapStick.gunnerTable.putBoolean("a Initialized?", true);
    }

    public void onConnect(Controller controller) {
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        LeapStick.gunnerTable.putBoolean("b Connected?", true);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        LeapStick.gunnerTable.putBoolean("b Connected?", false);
    }

    public void onExit(Controller controller) {
        LeapStick.gunnerTable.putBoolean("a Initialized?", false);
    }

    public void onFrame(Controller controller) {
        //Get the most recent frame and identify the best hand
        Frame frame = controller.frame();
        Hand hand = frame.hands().frontmost();//the hand that is farthest from the user
        String handType = hand.isLeft() ? "left" : "right";
        double handConfidence = (double)hand.confidence();
        //Get pitch, yaw, and roll in degrees
        double palmPitch = Math.toDegrees((double)hand.direction().pitch());//like measuring an incline
        double palmYaw = Math.toDegrees((double)hand.direction().yaw());//like a boat's heading
        double palmRoll = Math.toDegrees((double)hand.palmNormal().roll());//point palm towards the + or - side of the x axis
        //Redefine points so that they range from -1 to 1
        InteractionBox interactionBox = frame.interactionBox();
        Vector palmPosition = interactionBox.normalizePoint(hand.stabilizedPalmPosition()).times(2f).minus(vector);
        //Redefine axis so that an ordinary XY plane can lay flat on the controller
        double palmX = (double)palmPosition.getX();//.get has positives to the right
        double palmY = -(double)palmPosition.getZ();//.get has positives closer to the user, not very reliable
        double palmHeight = (double)palmPosition.getY();//.get has positives going up
        //Get grabbing and pinching data
        double handGrabStrength = (double)(hand.grabStrength()*hand.grabStrength());
        double handPinchStrength = (double)(hand.pinchStrength()*hand.pinchStrength());
        boolean handGrabbing = handGrabStrength >= .95;//strict grabbing
        boolean handPinching = handPinchStrength > 0.5;//loose pinching
        if (handGrabbing) {handPinching = false;};//cannot pinch while grabbing
        //Get gestures
        String circleDirection = "none";
        double circleDuration = 0.0;
        GestureList gestures = frame.gestures();
        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);
            if (gesture.type() == Gesture.Type.TYPE_CIRCLE) {
            	CircleGesture circle = new CircleGesture(gesture);
                circleDuration = (double)circle.durationSeconds();
            	//Calculate clock direction using the angle between circle normal and pointable
                if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/2) {
                    //Clockwise if angle is less than 90 degrees
                    circleDirection = "clockwise";
                }else {
                    circleDirection = "counterclockwise";
                }
            }
        }
        
        
        LeapStick.gunnerTable.putNumber("c Hand count", frame.hands().count());
        LeapStick.gunnerTable.putString("d Current hand", handType);
        LeapStick.gunnerTable.putNumber("e Tracking confidence", handConfidence);
        LeapStick.gunnerTable.putNumber("f Palm pitch", palmPitch);
        LeapStick.gunnerTable.putNumber("g Palm yaw", palmYaw);
        LeapStick.gunnerTable.putNumber("h Palm roll", palmRoll);
        LeapStick.gunnerTable.putNumber("i Palm x", palmX);
        LeapStick.gunnerTable.putNumber("j Palm y", palmY);
        LeapStick.gunnerTable.putNumber("k Palm height", palmHeight);
        LeapStick.gunnerTable.putBoolean("l Grabbing?", handGrabbing);
        LeapStick.gunnerTable.putBoolean("m Pinching?", handPinching);
        LeapStick.gunnerTable.putString("n Circle gesture", circleDirection);
        LeapStick.gunnerTable.putNumber("o Circle duration", circleDuration);
    }
}
