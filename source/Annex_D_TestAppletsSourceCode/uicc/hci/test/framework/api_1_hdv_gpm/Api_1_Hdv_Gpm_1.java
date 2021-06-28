package uicc.hci.test.framework.api_1_hdv_gpm;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;

/**
 * The method with the following header shall be compliant to its definition in the API.<br />
 * <code>public static byte getPowerMode()</code>
 *
 */
public class Api_1_Hdv_Gpm_1 extends Applet implements CardEmulationListener {
	
	 // Define specific SWs
	
	
	/**
	 * Status word: Full power mode 
	 */
	private final static short SW_FULL_POWER_MODE = ISO7816.SW_NO_ERROR + (short) 3;

	/**
	 * Status word: Low power mode 
	 */
	private final static short SW_LOW_POWER_MODE = ISO7816.SW_NO_ERROR + (short) 4;

	/**
	 * Status word: Power mode could't be retrieved
	 */
	private final static short SW_POWER_MODE_COULD_NOT_BE_RETRIEVED = ISO7816.SW_NO_ERROR + (short) 5;
	
	
	 // Define specific INS bytes for HCIDevice tests
	/**
	 * Instruction byte: Retrieve power mode
	 */
	private final static short INS_HELPER = (short) 0x01;
	private final static short INS_RETRIEVE_POWER_MODE = (short) 0x05;

	/**
	 * Default constructor, registers the applet.
	 */
	private Api_1_Hdv_Gpm_1() {		
		register();
	}
	
	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card runtime environment will call this static method first.
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength)
			throws ISOException {
		new Api_1_Hdv_Gpm_1();
	}

	/**
	 *  Called by the Java Card runtime environment to process an incoming APDU command.
	 *  @see Applet#process(APDU)
	 */
	public void process(APDU apdu) throws ISOException {
		/*
		 * Check for SELECT command
		 */
		if (selectingApplet())
			return;

		
		byte buffer[] = apdu.getBuffer();

		/*
		 * main switch
		 */
	
	    switch (buffer[ISO7816.OFFSET_INS]) {


	      case INS_HELPER:	    	  
	    	  return;	
	      case INS_RETRIEVE_POWER_MODE:	    	  
	    	  ISOException.throwIt(checkPowerMode(HCIDevice.getPowerMode()));	    	  
	    	  

	      default: 
	    	  ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
	    }
				

	}

	/**
	 * Checks the power mode an determine the according status word.  
	 * @param powerMode Value, taken from {@link HCIDevice#getPowerMode()}
	 * @return The according status word.
	 */
	private short checkPowerMode(byte powerMode){
		switch (powerMode) {
	      case HCIDevice.FULL_POWER_MODE: 	    	  
	        return SW_FULL_POWER_MODE;
	      case HCIDevice.LOW_POWER_MODE: 	    	  
	        return SW_LOW_POWER_MODE;
	      case (-1):     	  
	        return SW_POWER_MODE_COULD_NOT_BE_RETRIEVED;
	       
	      default: 
	    	  return ISO7816.SW_UNKNOWN;
		}
	}

	/**
	 * Not used
	 * This method is called by the HCI framework to inform the Listener Object about a specific event and pass the corresponding HCIMessage to the Listener Object.
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte arg0, HCIMessage arg1) {
		
	}

}






