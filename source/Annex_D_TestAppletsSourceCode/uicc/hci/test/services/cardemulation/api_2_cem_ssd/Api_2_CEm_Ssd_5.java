package uicc.hci.test.services.cardemulation.api_2_cem_ssd;

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
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>void prepareAndSendSendDataEvent(byte[] data,
 *                                short offset,
 *                                short len)
 *                                throws HCIException,
 *                                       java.lang.NullPointerException,
 *                                       java.lang.ArrayIndexOutOfBoundsException
 * </code>
 */
public class Api_2_CEm_Ssd_5 extends Applet implements CardEmulationListener {

	
	private final static byte INS_CHECK_FOR_EXCEPTION = 0x01;
	
	/*
	 * Define HCI specific constants
	 */
	private CardEmulationService ceService;
	private byte[] ibuffer;

	/**
	 * Applet tests CardEmulationMessage
	 */
	private Api_2_CEm_Ssd_5() {
		/*
		 * JavaCard applet register
		 */
		register();

		ibuffer = JCSystem.makeTransientByteArray((short) 2, JCSystem.CLEAR_ON_RESET);
		

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
		new Api_2_CEm_Ssd_5();
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

			ibuffer[0] = (byte)0x90;
			ibuffer[1] = 0x00;


			try {
				message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_CURRENTLY_DISABLED) {
					ibuffer[1] = 0x01;
				} else if (e.getReason() == HCIException.HCI_FRAGMENTED_MESSAGE_ONGOING) {
					ibuffer[1] = 0x02;
				} else if (e.getReason() == HCIException.HCI_RESOURCES_NOT_AVAILABLE) {
					ibuffer[1] = 0x04;
				} else {
					ibuffer[1] = (byte)0x80;
				}
			}
			
			//fall through

		case EVENT_GET_PARAMETER_RESPONSE:

			//fall through

		case EVENT_FIELD_OFF:

			//fall through

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
			
		default: 
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
}
