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
 * the API.<br />
 * <code>void deactivateEvent(byte event) throws HCIException</code>
 */
public class Api_2_CEl_Ocb_6 extends Applet implements CardEmulationListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes for HCIService tests
	 */

	private static final byte INS_EVENT_DEACTIVATE_VERIFY = (byte) 0x20;

	/*
	 * Local variables 
	 */

	private CardEmulationService ceService;
	
	/**
	 * bit 0 is set if EVENT_HCI_TRANSMISSION_FAILED is notified
	 * bit 1 is set if EVENT_HCI_RECEPTION_FAILED is notified
	 * bit 2 is set if EVENT_GET_PARAMETER_RESPONSE is notified
	 * bit 3 is set if EVENT_ON_SEND_DATA is notified
	 * bit 4 is set if EVENT_FIELD_OFF is notified
	 */
	private static byte verificationByte;
	private byte[] sw;
	private byte sentOnce;
	

	private Api_2_CEl_Ocb_6() {
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
			ceService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);
			ceService.activateEvent(EVENT_ON_SEND_DATA);
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
		new Api_2_CEl_Ocb_6();
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
		 * ASSUMPTION: event activation worked
		 */
	

		case INS_EVENT_DEACTIVATE_VERIFY:

			if ((verificationByte & 0x04) == 0x04) {
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
	 * bit 2 is set if EVENT_GET_PARAMETER_RESPONSE is notified<br />
	 * bit 3 is set if EVENT_ON_SEND_DATA is notified<br />
	 * bit 4 is set if EVENT_FIELD_OFF is notified<br />
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
				} else {					
					ceService.deactivateEvent(EVENT_GET_PARAMETER_RESPONSE);
					ceService.deactivateEvent(EVENT_ON_SEND_DATA);
				}
				message.prepareAndSendGetParameterCommand(CardEmulationMessage.PARAM_ID_TYPE_A_CARD_ATQA);
				return;		

		case EVENT_GET_PARAMETER_RESPONSE:

			verificationByte = 0x04;

			//fall through
		default:
			return;
		}

	}

}
