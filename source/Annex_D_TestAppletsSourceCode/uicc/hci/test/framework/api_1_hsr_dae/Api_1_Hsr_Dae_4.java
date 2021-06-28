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
public class Api_1_Hsr_Dae_4 extends Applet implements ConnectivityListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes for HCIService tests
	 */

	private final static byte INS_EVENT_TM_FAILED = (byte) 0x10;
	private final static byte INS_EVENT_RCP_FAILED = (byte) 0x11;
	private final static byte INS_EVENT_STANDY_BY = (byte) 0x12;

	private static final byte INS_EVENT_DEACTIVATE_VERIFY = (byte) 0x20;

	/*
	 * Local variables 
	 */

	private ConnectivityService cnnService;
	
	/**
	 * bit 0 is set if EVENT_HCI_TRANSMISSION_FAILED is notified
	 * bit 1 is set if EVENT_HCI_RECEPTION_FAILED is notified
	 * bit 2 is set if EVENT_GET_PARAMETER_RESPONSE is notified
	 * bit 3 is set if EVENT_ON_SEND_DATA is notified
	 * bit 4 is set if EVENT_FIELD_OFF is notified
	 */
	private byte verificationByte;

	private Api_1_Hsr_Dae_4() {
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

		verificationByte = 0x00;
	}

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_1_Hsr_Dae_4();
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

		case INS_EVENT_TM_FAILED:

			try {
				cnnService.activateEvent(EVENT_HCI_TRANSMISSION_FAILED);
				cnnService.deactivateEvent(EVENT_HCI_TRANSMISSION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		case INS_EVENT_RCP_FAILED:

			try {
				cnnService.activateEvent(EVENT_HCI_RECEPTION_FAILED);
				cnnService.deactivateEvent(EVENT_HCI_RECEPTION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		case INS_EVENT_STANDY_BY:

			try {
				cnnService.activateEvent(EVENT_STAND_BY);
				cnnService.deactivateEvent(EVENT_STAND_BY);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;



		case INS_EVENT_DEACTIVATE_VERIFY:

			if (verificationByte != 0x00) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

	}

	/**
	 * bit 0 is set if EVENT_HCI_TRANSMISSION_FAILED is notified<br />
	 * bit 1 is set if EVENT_HCI_RECEPTION_FAILED is notified<br />
	 * bit 2 is set if EVENT_STAND_BY is notified<br />
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage message) {
		switch (event) {

		case EVENT_HCI_TRANSMISSION_FAILED:

			verificationByte |= 0x01;

			return;

		case EVENT_HCI_RECEPTION_FAILED:

			verificationByte |= 0x02;

			return;

		case EVENT_STAND_BY:

			verificationByte |= 0x04;

			//fall through
		default:
			return;
		}

	}

}
