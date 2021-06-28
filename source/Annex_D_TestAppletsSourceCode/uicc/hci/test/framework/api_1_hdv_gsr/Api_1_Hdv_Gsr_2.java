package uicc.hci.test.framework.api_1_hdv_gsr;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;

/**
 * Checks, that the method with the following header shall be compliant to its definition in the API <br />
 * <code>public static HCIService getHCIService(short serviceID)throws HCIException, javacard.framework.SystemException</code><br />
 * This applet is used for the following test scripts:
 * <ul> 
 * <li>Test_Api_1_Hdv_Gsr_4
 * </ul>
 *
 */
public class Api_1_Hdv_Gsr_2 extends Applet implements CardEmulationListener {
	
	/*
	 * Define specific SWs
	 */
	
	private final static short SW_SERVICE_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 1;

	
	
	/*
	 * Define specific INS bytes
	 */
	
	private final static byte INS_HCI_NO_SERVCE = (byte) 0x01;

	
    private boolean isNoServiceBeforeRegister;


	private Api_1_Hdv_Gsr_2() {		
		
		isNoServiceBeforeRegister = false;
		
		try {
			if (HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID) == null) {
				isNoServiceBeforeRegister = true; 
			} 
		} catch (HCIException e){
			ISOException.throwIt(SW_SERVICE_NOT_SUPPORTED);
		}
		
		
		register();
	}

	public static void install(byte bArray[], short bOffset, byte bLength)
			throws ISOException {
		new Api_1_Hdv_Gsr_2();
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
				
		/*
		 * analyze incoming data
		 * get CLA, INS, P1, P2, Lc 
		 */
		
		byte buffer[] = apdu.getBuffer();
		apdu.setIncomingAndReceive();

		
	    switch (buffer[ISO7816.OFFSET_INS]) {
	      case INS_HCI_NO_SERVCE: 

	    	  if (isNoServiceBeforeRegister)
	    		  return; //expected result

	    	  ISOException.throwIt(SW_SERVICE_NOT_SUPPORTED);	    	        

	      default: 
	    	  ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
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
