package uicc.hci.test.framework.api_1_hsr_dae;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationService;

/**
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>void deactivateEvent(byte event) throws HCIException</code>
 */
public class Api_1_Hsr_Dae_1 extends Applet implements CardEmulationListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes for HCIService tests
	 */

	private final static byte INS_EVENT_TM_FAILED = (byte) 0x10;
	private final static byte INS_EVENT_RCP_FAILED = (byte) 0x11;
	private final static byte INS_EVENT_GET_PARAM = (byte) 0x12;
	private final static byte INS_EVENT_FIELD_OFF = (byte) 0x13;
	private final static byte INS_WRONG_EVENT = (byte) 0x16;

	private static final byte INS_EVENT_DEACTIVATE_NON_ACTIV = (byte) 0x15;

	/*
	 * Local variables 
	 */

	private CardEmulationService ceService;

	private Api_1_Hsr_Dae_1() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
			ceService.register(this);
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
		new Api_1_Hsr_Dae_1();
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
		 * ASSUMPTION: event activation worked, otherwise different SWs in case
		 * of HCIException for activateEvent() and deactivateEvent() must be
		 * used
		 */
		case INS_EVENT_TM_FAILED:

			try {
				ceService.activateEvent(EVENT_HCI_TRANSMISSION_FAILED);
				ceService.deactivateEvent(EVENT_HCI_TRANSMISSION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		case INS_EVENT_RCP_FAILED:

			try {
				ceService.activateEvent(EVENT_HCI_RECEPTION_FAILED);
				ceService.deactivateEvent(EVENT_HCI_RECEPTION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		case INS_EVENT_GET_PARAM:

			try {
				ceService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);
				ceService.deactivateEvent(EVENT_GET_PARAMETER_RESPONSE);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		case INS_EVENT_FIELD_OFF:

			try {
				ceService.activateEvent(EVENT_FIELD_OFF);
				ceService.deactivateEvent(EVENT_FIELD_OFF);
			} catch (HCIException e) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

			return;

		case INS_EVENT_DEACTIVATE_NON_ACTIV: // option: should be extended for
												// all events

			try {
				ceService.deactivateEvent(EVENT_ON_SEND_DATA);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_WRONG_EVENT_TYPE)
					return;
			}
			ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);

		case INS_WRONG_EVENT:

			try {
				ceService.deactivateEvent((byte) 0x02); //ReaderListener.EVENT_WRITE_EXCHANGE_DATA_RESPONSE);
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
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage message) {

	}

}
