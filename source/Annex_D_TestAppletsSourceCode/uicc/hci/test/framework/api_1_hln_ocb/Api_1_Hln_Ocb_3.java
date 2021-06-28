package uicc.hci.test.framework.api_1_hln_ocb;

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
public class Api_1_Hln_Ocb_3 extends Applet implements CardEmulationListener {
	
	/*
	 * local variables
	 */

	private CardEmulationService ceService;
	
	private byte sentOnce; 
	private byte[] sw;


	private Api_1_Hln_Ocb_3() {
		/*
		 * JavaCard applet register
		 */
		register();
		
		sw = new byte[2];
		sentOnce = 0x00;
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
		new Api_1_Hln_Ocb_3();
	}

	/**
	 * Not used.<br />
	 * Called by the Java Card runtime environment to process an incoming APDU
	 * command.
	 * 
	 * @see Applet#process(APDU)
	 */
	public void process(APDU apdu) throws ISOException {

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
				message.prepareAndSendGetParameterCommand(CardEmulationMessage.PARAM_ID_TYPE_A_CARD_ATQA);
			}
		
			return;
			
		case EVENT_GET_PARAMETER_RESPONSE:
//			if (!(message.getType() == CardEmulationMessage.TYPE_RESPONSE && message.getInstruction() == CardEmulationMessage.RESP_ANY_OK))
//				ISOException.throwIt(SW_NOT_ANY_OK);
			message.prepareAndSendSendDataEvent(sw, (short) 0, (short)2);

		}

		return;		

	}

}
