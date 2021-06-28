package uicc.hci.test.services.cardemulation.api_2_cem_sgp;

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
 * <code>void prepareAndSendGetParameterCommand(byte paramID) throws HCIException</code>
 */
public class Api_2_CEm_Sgp_14 extends Applet implements CardEmulationListener {

	/*
	 * Define HCI specific constants
	 */
	private CardEmulationService ceService;
	private byte[] exceptions;
	private byte sentOnce;

	/**
	 * Applet tests CardEmulationMessage
	 */
	private Api_2_CEm_Sgp_14() {
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
			ceService.activateEvent(EVENT_ON_SEND_DATA);
		} catch (HCIException e) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		

		sentOnce = 0x00;

		exceptions = JCSystem.makeTransientByteArray((short) 3, JCSystem.CLEAR_ON_RESET);
	}

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_2_CEm_Sgp_14();
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
		exceptions[1] = (byte) 0x90;
		exceptions[2] = 0;

		switch (event) {

		case EVENT_ON_SEND_DATA:
			
        	if (sentOnce == 0x00) {
				sentOnce = 0x01;
				message.prepareAndSendSendDataEvent(exceptions, (short) 1, (short)2);
				return;
			}

			ceService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);

			try {
				message.prepareAndSendGetParameterCommand(CardEmulationMessage.PARAM_ID_TYPE_A_CARD_ATQA);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_CURRENTLY_DISABLED) {
					exceptions[0] |= 0x01;
				} else if (e.getReason() == HCIException.HCI_FRAGMENTED_MESSAGE_ONGOING) {
					exceptions[0] |= 0x02;
				} else if (e.getReason() == HCIException.HCI_RESOURCES_NOT_AVAILABLE) {
					exceptions[0] |= 0x04;
				} else {
					exceptions[0] |= 0x80;
				}
			}
			message.prepareAndSendSendDataEvent(exceptions, (short) 0, (short) 1);
			return;

		case EVENT_GET_PARAMETER_RESPONSE:

			exceptions[0] = (byte) 0x07;

			// fall through

		case EVENT_FIELD_OFF:

			// fall through

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
	public void process(APDU arg0) throws ISOException {

		/*
		 * Check for SELECT command
		 */
		if (selectingApplet())
			return;
	
	}
}
