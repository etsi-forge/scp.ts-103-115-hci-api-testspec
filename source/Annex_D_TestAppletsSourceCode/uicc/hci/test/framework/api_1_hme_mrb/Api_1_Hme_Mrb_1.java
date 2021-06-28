package uicc.hci.test.framework.api_1_hme_mrb;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationMessage;
import uicc.hci.services.cardemulation.CardEmulationService;

/**
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>byte[] getReceiveBuffer()</code>
 */
public class Api_1_Hme_Mrb_1 extends Applet implements CardEmulationListener {

	/*
	 * Define HCI specific variables
	 */
	private CardEmulationService ceService;

	private short offset;
	private byte[] ibuffer;
	private byte sentOnce; 

	/**
	 * Applet tests HCIMessage commands
	 */
	private Api_1_Hme_Mrb_1() {
		/*
		 * JavaCard applet register
		 */
		register();

		ibuffer = JCSystem.makeTransientByteArray((short) 11, JCSystem.CLEAR_ON_RESET);
		sentOnce = 0x00;
		
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
		new Api_1_Hme_Mrb_1();
	}

	public void onCallback(byte event, HCIMessage hcimessage) {

		CardEmulationMessage message = (CardEmulationMessage) hcimessage;

		switch (event) {

		case EVENT_ON_SEND_DATA:
			ibuffer[9] = (byte) 0x90;
			ibuffer[10] = 0x00;
			
        	if (sentOnce == 0x00) {
				sentOnce = 0x01;
				message.prepareAndSendSendDataEvent(ibuffer, (short) 9, (short) 2);
				return;
        	}

			if (!message.isComplete()) { //TC ID 2
					hcimessage.getReceiveBuffer();
					message.prepareAndSendSendDataEvent(ibuffer, (short) 9, (short) 2);
					return;
				}
	
				offset = hcimessage.getReceiveOffset();

				Util.arrayCopy(hcimessage.getReceiveBuffer(), offset, ibuffer, (short) 0, (short) 9);
				message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 11);

			
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
	public void process(APDU arg0) throws ISOException {
	}
}
