package uicc.hci.test.framework.api_1_hln_ocb;

import javacard.framework.AID;
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
 * the API. <code>void onCallback(byte event, HCIMessage message)</code>
 */
public class Api_1_Hln_Ocb_4 extends Applet implements CardEmulationListener {


	private CardEmulationService ceService;
	private byte[] expectedContextAID = {(byte) 0xA0, 0x00, 0x00, 0x00, 0x09, 0x00, 0x05, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte)0xFF, (byte) 0x89, 0x21, 0x11, 0x04, 0x02};
	private AID realAID;

	private byte[] ibuffer;

	private Api_1_Hln_Ocb_4() {
		/*
		 * JavaCard applet register
		 */
		register();

		ibuffer = JCSystem.makeTransientByteArray((short) 5, JCSystem.CLEAR_ON_RESET);

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
		new Api_1_Hln_Ocb_4();
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
	public void onCallback(byte event, HCIMessage hcimessage) {
		
		CardEmulationMessage message = (CardEmulationMessage) hcimessage;

		switch (event) {

		case EVENT_ON_SEND_DATA:
			
			ibuffer[0] = (byte) 0x90;
			ibuffer[1] = 0x00;
			
			if (message.selectingMessage()){
				message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
				return;
			}
			
//			switch (hcimessage.getReceiveBuffer()[1]) {
//			case (byte) 0xA4:
//				message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
//				return;
//
//			default:
				short offset = hcimessage.getReceiveOffset();
				offset++;
				
				if ((hcimessage.getReceiveBuffer()[offset]!=0x01)&(hcimessage.getReceiveBuffer()[offset]!=0x02)){
					ibuffer[0] = (byte) 0x6D;
				}
				
				realAID = JCSystem.getAID();
				if ((hcimessage.getReceiveBuffer()[offset]==0x01) && !realAID.equals(expectedContextAID, (short) 0, (byte) expectedContextAID.length))
				{
				    // AIDs not match -> test failed
					ibuffer[0] = (byte) 0x6A;
					ibuffer[1] = (byte) 0x80;
					
				}
				
				if ((hcimessage.getReceiveBuffer()[offset]==0x02) && (JCSystem.getPreviousContextAID()!= null)) {
					 // test failed
					ibuffer[0] = (byte) 0x6A;
					ibuffer[1] = (byte) 0x80;
				}
				
				
				message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
				return;		
			}
	
//		}

		return;		

	}

}