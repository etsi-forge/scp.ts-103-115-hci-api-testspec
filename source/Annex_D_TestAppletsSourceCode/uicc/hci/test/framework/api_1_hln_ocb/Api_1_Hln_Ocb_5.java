package uicc.hci.test.framework.api_1_hln_ocb;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationMessage;
import uicc.hci.services.cardemulation.CardEmulationService;
import uicc.toolkit.ToolkitConstants;
import uicc.toolkit.ToolkitException;

/**
 * The method with the following header shall be compliant to its definition in
 * the API. <code>void onCallback(byte event, HCIMessage message)</code>
 */
public class Api_1_Hln_Ocb_5 extends Applet implements CardEmulationListener {


	/*
	 * local variables
	 */

	private CardEmulationService ceService;
	
	private byte[] unmodifiedArray;
	static final byte ORIGINAL_VALUE = (byte) 0x00;
	static final byte NEW_VALUE = (byte) 0xFF;

	private byte[] ibuffer;

	private Api_1_Hln_Ocb_5() {
		/*
		 * JavaCard applet register
		 */
		register();

		ibuffer = JCSystem.makeTransientByteArray((short) 5, JCSystem.CLEAR_ON_RESET);
		
		unmodifiedArray = new byte[1];
	    unmodifiedArray[0] = ORIGINAL_VALUE;

		/*
		 * HCI listener register
		 */
		try {
			ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
			ceService.register(this);
			ceService.activateEvent(EVENT_ON_SEND_DATA);
		} catch (HCIException e) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}

	}

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_1_Hln_Ocb_5();
	}

	/**
	 * Called by the Java Card runtime environment to process an incoming APDU
	 * command.
	 * 
	 * @see Applet#process(APDU)
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

		ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);



	}

	/**
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage hcimessage) {
		
		CardEmulationMessage message = (CardEmulationMessage) hcimessage;

		switch (event) {

		case EVENT_ON_SEND_DATA:
			
			ibuffer[0] = (byte) 0x90;
			ibuffer[1] = 0x00;
			
			if (message.selectingMessage()){
				message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
				return;
			}
				
				
				JCSystem.beginTransaction();			      
			    unmodifiedArray[0] = NEW_VALUE;

				message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);			
			}	
		

		return;		

	}
	
	public void processToolkit(short event) throws ToolkitException {
	      if (event == ToolkitConstants.EVENT_UNRECOGNIZED_ENVELOPE)
	      {
	            if (unmodifiedArray[0] == ORIGINAL_VALUE)
	            {
	            	ISOException.throwIt(ISO7816.SW_NO_ERROR);
	            }
	            else
	            {
	            	ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
	            }
	      }
	}


}