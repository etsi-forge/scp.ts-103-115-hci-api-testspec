package uicc.hci.test.framework.api_1_hsr_gen;

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
 * TODO check, if WRONG_EVENT_TYPE will be specified as TC ID 7 or remove it from implementation<br />
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>boolean getEventNotificationStatus(byte event) throws HCIException</code>
 */
public class Api_1_Hsr_Gen_2 extends Applet implements ConnectivityListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_EVENT_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 1;
	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes
	 */

	private final static byte INS_EVENT_TM_FAILED = (byte) 0x10;
	private final static byte INS_EVENT_RCP_FAILED = (byte) 0x11;
	private final static byte INS_EVENT_STAND_BY = (byte) 0x12;

	private final static byte INS_EVENT_STATUS_CHANGED = (byte) 0x15;
	
	private final static byte INS_WRONG_EVENT = (byte) 0x16;

	/*
	 * Local variables
	 */

	private ConnectivityService cnnService;

	private Api_1_Hsr_Gen_2() {
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
		new Api_1_Hsr_Gen_2();
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
		 * HCIServce.register()
		 */

		/*
		 * HCIServce.getEventNotification()
		 */
		case INS_EVENT_TM_FAILED:
			try {
				if (cnnService.getEventNotificationStatus(EVENT_HCI_TRANSMISSION_FAILED))
					ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			return;

		case INS_EVENT_RCP_FAILED:
			try {
				if (cnnService.getEventNotificationStatus(EVENT_HCI_RECEPTION_FAILED)) 
					ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);				
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			return;

		case INS_EVENT_STAND_BY:
			try {
				if (cnnService.getEventNotificationStatus(EVENT_STAND_BY)) 
					ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			return;


		case INS_EVENT_STATUS_CHANGED:
			try {
				cnnService.activateEvent(EVENT_STAND_BY);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (!cnnService.getEventNotificationStatus(EVENT_STAND_BY)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;

		case INS_WRONG_EVENT:
			try {
				cnnService.getEventNotificationStatus((byte) 0x02);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_WRONG_EVENT_TYPE) 
					return;				
			}
			ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);

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
