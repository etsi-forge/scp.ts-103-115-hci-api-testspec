package uicc.hci.test.services.cardemulation.api_2_cel_ocb;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationMessage;
import uicc.hci.services.cardemulation.CardEmulationService;

/**
 * The method with the following header shall be compliant to its definition in
 * the API. <code>void onCallback(byte event, HCIMessage message)</code>
 */
public class Api_2_CEl_Ocb_3 extends Applet implements CardEmulationListener {

	private final static byte INS_EVENT_ACTIVATE_GET_PARAM_RESP_VF = (byte) 0x22;
	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;
	
	/*
	 * local variables
	 */

	private CardEmulationService ceService;
	private static byte sentOnce; 
	private byte[] sw;
	private static byte verificationByte;
	

	private Api_2_CEl_Ocb_3() {
		/*
		 * JavaCard applet register
		 */
		register();

		verificationByte = 0x00;

		sentOnce = 0x00;
		
		sw = new byte[2];
		/*
		 * HCI listener register
		 */
		try {
			ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
			ceService.register(this);
			ceService.activateEvent(EVENT_ON_SEND_DATA);
			ceService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);			
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
		new Api_2_CEl_Ocb_3();
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

		case INS_EVENT_ACTIVATE_GET_PARAM_RESP_VF:

			if ((verificationByte & 0x04) != 0x04) {
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);
			}

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
	public void onCallback(byte event, HCIMessage hciMessage) {
		CardEmulationMessage message = (CardEmulationMessage) hciMessage;
		sw[0] = (byte) 0x90;
		sw[1] = 0;
		
		switch (event) {
		case EVENT_ON_SEND_DATA:
			if (sentOnce == 0x00) {
				sentOnce = 0x01;
				message.prepareAndSendSendDataEvent(sw, (short) 0, (short)2);
				return;								
			}
			message.prepareAndSendGetParameterCommand(CardEmulationMessage.PARAM_ID_TYPE_B_CARD_ATQB);
			ceService.deactivateEvent(EVENT_ON_SEND_DATA);
			return;
		case EVENT_GET_PARAMETER_RESPONSE:
			verificationByte |= 0x04;
			message.prepareAndSendSendDataEvent(sw, (short) 0, (short)2);

		default: 
			return;	
			}	

	}

}
