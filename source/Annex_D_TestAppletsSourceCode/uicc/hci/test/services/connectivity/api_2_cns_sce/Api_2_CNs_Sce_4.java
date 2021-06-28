package uicc.hci.test.services.connectivity.api_2_cns_sce;

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
import uicc.toolkit.ProactiveHandler;
import uicc.toolkit.ProactiveHandlerSystem;
import uicc.toolkit.ToolkitException;

/**
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>void prepareAndSendConnectivityEvent() throws HCIException</code>
 */
public class Api_2_CNs_Sce_4 extends Applet implements CardEmulationListener {

	/*
	 * Define HCI specific constants
	 */
	private CardEmulationService ceService;
	private byte[] ibuffer;

	/**
	 * Applet tests CardEmulationMessage
	 */
	private Api_2_CNs_Sce_4() {
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
		new Api_2_CNs_Sce_4();
	}

	/**
	 * Not used.<br />
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage hcimessage) {
						
			ibuffer[0] = (byte) 0x90;
			ibuffer[1] = 0x00;
			
		/*
		 * analyze incoming data
		 */

		CardEmulationMessage message = (CardEmulationMessage) hcimessage;
		
		if (message.selectingMessage()) {
			message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
			return;
		}

		switch (event) {

		case EVENT_ON_SEND_DATA:
			
			
			ProactiveHandler proactiveHandler;
			try {
				proactiveHandler = ProactiveHandlerSystem.getTheHandler();
			} catch (ToolkitException e) {
				message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
				return;
			}
			
			if (proactiveHandler != null) {
				ibuffer[0] = (byte) 0x6A;
				ibuffer[1] = (byte) 0x80;
			} 
			
			message.prepareAndSendSendDataEvent(ibuffer, (short) 0, (short) 2);
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
		if (selectingApplet())
			return;
	}
}
