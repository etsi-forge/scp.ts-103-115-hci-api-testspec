package uicc.hci.test.framework.api_1_hdv_isa;


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
 * <code>public static byte isHCIServiceAvailable(short serviceID)</code>
 */
public class Api_1_Hdv_Isa_2 extends Applet implements CardEmulationListener {
	
	
	/*
	 * Define specific INS bytes for HCIDevice tests
	 */	
	private final static short INS_CEM_SERVICE_AVAILABLE = (short) 0x06;

	
	
	private Api_1_Hdv_Isa_2() {		
		
		register();
	
	}
	
	
	
	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card runtime environment will call this static method first.
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength)
			throws ISOException {
		new Api_1_Hdv_Isa_2();

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
		 */
		
		byte buffer[] = apdu.getBuffer();
		
		
	    switch (buffer[ISO7816.OFFSET_INS]) {

	      case INS_CEM_SERVICE_AVAILABLE:
	    	  
	    	  buffer[2] = HCIDevice.isHCIServiceAvailable(HCIDevice.CARD_EMULATION_SERVICE_ID);	    	  
	    	  break;
	    	  
	      default: 
	    	  ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
	    }
	    
	    apdu.setOutgoingAndSend((short)2, (short)1);
		
	}


	/**
	 * Not used
	 * This method is called by the HCI framework to inform the Listener Object about a specific event and pass the corresponding HCIMessage to the Listener Object.
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte arg0, HCIMessage arg1) {
		
	}

	
}






