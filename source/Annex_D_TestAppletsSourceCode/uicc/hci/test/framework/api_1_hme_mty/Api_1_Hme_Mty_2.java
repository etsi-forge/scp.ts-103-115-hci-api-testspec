package uicc.hci.test.framework.api_1_hme_mty;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.connectivity.ConnectivityListener;
import uicc.hci.services.connectivity.ConnectivityService;

/**
 * The method with the following header shall be compliant to its definition in the API.
 * <code>byte getType()</code>
 */
public class Api_1_Hme_Mty_2 extends Applet implements ConnectivityListener {


	/*
	 * Define specific SWs
	 */

	private static final short SW_EVENT_NOT_TRIGGERED = ISO7816.SW_UNKNOWN + (short) 1;
	
	/*
	 * Define specific INS bytes
	 */
	private final static byte INS_CHECK_VB = (byte) 0x01;
	
	/*
	 * Define HCI specific variables
	 */
	private ConnectivityService cnnService;

	/**
	 * byte 0 - HCIMessage.isHeading() result
	 * byte 1 - HCIMessage.isComplete() result
	 * byte 2 - HCIMessage.getType() result
	 * byte 3 - HCIMessage.getInstruction() result
	 * byte 4, 5 - HCIMessage.getReceiveOffset() result
	 * byte 6, 7 - HCIMessage.getReceiveLength() result
	 */
	private byte[] exceptions;

	/**
	 * Applet tests HCIMessage commands
	 */
	private Api_1_Hme_Mty_2() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			cnnService = (ConnectivityService) HCIDevice.getHCIService(HCIDevice.CONNECTIVITY_SERVICE_ID);
			cnnService.register(this);
			cnnService.activateEvent(EVENT_STAND_BY);			
		} catch (HCIException e) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}

		exceptions = new byte[8];
	}

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_1_Hme_Mty_2();
	}

	/**
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage hcimessage) {

		switch (event) {

		case EVENT_STAND_BY:

			exceptions[2] = hcimessage.getType();
			
			// fall through
		default:
			return;
		}

	}

	/**
	 * Not used.<br />
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

		byte buffer[] = apdu.getBuffer();

		switch (buffer[ISO7816.OFFSET_INS]) {

		/*
		 * HCIServce.activateEvent()
		 */

		case INS_CHECK_VB:

			if ((exceptions[2] & 0xFF) != 0x40)
				ISOException.throwIt(SW_EVENT_NOT_TRIGGERED);
			return;
			

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
}
