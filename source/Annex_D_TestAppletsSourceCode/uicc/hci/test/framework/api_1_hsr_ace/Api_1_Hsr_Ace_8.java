package uicc.hci.test.framework.api_1_hsr_ace;

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
 * the API. <code>void activateEvent(byte event) throws HCIException</code>
 */
public class Api_1_Hsr_Ace_8 extends Applet implements CardEmulationListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 1;

	/*
	 * Define specific INS bytes
	 */

	private final static byte INS_VERIFY = (byte) 0x01;

	/*
	 * 
	 */

	private CardEmulationService ceService;

	/**
	 * bit 0 is set if EVENT_HCI_TRANSMISSION_FAILED is notified
	 * bit 1 is set if EVENT_HCI_RECEPTION_FAILED is notified
	 * bit 2 is set if EVENT_GET_PARAMETER_RESPONSE is notified
	 * bit 3 is set if EVENT_ON_SEND_DATA is notified
	 * bit 4 is set if EVENT_FIELD_OFF is notified
	 */
	private byte verificationByte;

	private Api_1_Hsr_Ace_8() {
		/*
		 * JavaCard applet register
		 */
		register();

		verificationByte = 0x00;
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
		new Api_1_Hsr_Ace_8();
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
		 * HCIServce.activateEvent()
		 */
		case INS_VERIFY:

			if (verificationByte != 0x01)
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
		
			return;

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

		/*
		 * prepare outgoing data if needed
		 */

		apdu.setOutgoingAndSend((short) 0, (short) 0);

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
	
	public boolean select(){
		verificationByte = 0x01;
		return super.select();
		
	}

}

