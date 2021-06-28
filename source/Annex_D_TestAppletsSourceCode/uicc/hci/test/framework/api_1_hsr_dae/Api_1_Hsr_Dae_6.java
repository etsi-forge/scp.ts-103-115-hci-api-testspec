package uicc.hci.test.framework.api_1_hsr_dae;

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
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>void deactivateEvent(byte event) throws HCIException</code>
 */
public class Api_1_Hsr_Dae_6 extends Applet implements ConnectivityListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes for HCIService tests
	 */

	private final static byte INS_EVENT_STAND_BY = (byte) 0x12;

	/*
	 * 
	 */

	private ConnectivityService cnnService;


	private Api_1_Hsr_Dae_6() {
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
		new Api_1_Hsr_Dae_6();
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

		byte buffer[] = apdu.getBuffer();

		switch (buffer[ISO7816.OFFSET_INS]) {

		/*
		 * HCIServce.deactivateEvent()
		 */
		case INS_EVENT_STAND_BY:

			try {
				cnnService.deactivateEvent(EVENT_STAND_BY);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_WRONG_EVENT_TYPE) 
					return;				
			}
			ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);



		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}


	}

	/**
	 * Not used.<br />
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage message) {

		
	}

}
