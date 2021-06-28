package uicc.hci.test.framework.api_1_hsr_ace;

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

/**
 * TODO REVIEW IMPLEMENTATION <br />
 * The method with the following header shall be compliant to its definition in
 * the API. <code>void activateEvent(byte event) throws HCIException</code>
 */
public class Api_1_Hsr_Ace_5 extends Applet implements CardEmulationListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_EVENT_NOT_TRIGGERED = ISO7816.SW_UNKNOWN + (short) 1;

	/*
	 * Define specific INS bytes
	 */
	private final static byte INS_CHECK_VB = (byte) 0x01;

	/*
	 * 
	 */

	private CardEmulationService ceService;
	private byte[] ibuffer;

	/**
	 * bit 0 is set if EVENT_FIELD_OFF is notified
	 */
	private byte[] verificationByte;

	private Api_1_Hsr_Ace_5() {
		/*
		 * JavaCard applet register
		 */
		register();
		
		ibuffer = JCSystem.makeTransientByteArray((short) 2, JCSystem.CLEAR_ON_RESET);

		verificationByte = new byte[1];
		verificationByte[0] = 0x00;
		/*
		 * HCI listener register
		 */
		try {
			ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
			ceService.register(this);
			ceService.activateEvent(EVENT_ON_SEND_DATA);
			ceService.activateEvent(EVENT_FIELD_OFF);
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
		new Api_1_Hsr_Ace_5();
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

		case INS_CHECK_VB:

			if ((verificationByte[0] & 0x01) != 0x01)
				ISOException.throwIt(SW_EVENT_NOT_TRIGGERED);
			return;
			

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}


	}
	
	public void deselect() {
		verificationByte[0] |= 0x02;
		super.deselect();
	}

	/**
	 * bit 0 is set if EVENT_FIELD_OFF is notified<br />
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

			message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
				return;

		
		case EVENT_FIELD_OFF:
			if ((verificationByte[0] & 0x02) == 0x00) //assure, that deselect hasn't been called yet
				verificationByte[0] |= 0x01;
			ceService.deactivateEvent(EVENT_ON_SEND_DATA);

			//fall through
		default:
			return;
		}

	}

}
