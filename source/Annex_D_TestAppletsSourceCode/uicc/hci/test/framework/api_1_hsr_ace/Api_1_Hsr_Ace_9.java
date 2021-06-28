package uicc.hci.test.framework.api_1_hsr_ace;

import org.globalplatform.contactless.GPCLSystem;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.AppletEvent;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.readermode.ReaderListener;
import uicc.hci.services.readermode.ReaderService;

/**
 * The method with the following header shall be compliant to its definition in
 * the API. <code>void activateEvent(byte event) throws HCIException</code>
 */
public class Api_1_Hsr_Ace_9 extends Applet implements ReaderListener, AppletEvent {

	private static final byte INS_HCI_DISABLED = 0x01;
	private static final short SW_UNKNOWN_PROP = (short) (ISO7816.SW_UNKNOWN + 1);
	
	private ReaderService rdrService;

	private Api_1_Hsr_Ace_9() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			rdrService = (ReaderService) HCIDevice.getHCIService(HCIDevice.READER_SERVICE_ID);
			rdrService.register(this);
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
		new Api_1_Hsr_Ace_9();
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

		case INS_HCI_DISABLED: 
			
			GPCLSystem.setCommunicationInterface(GPCLSystem.GPCL_INTERFACE_ISO14443, false);
			
			try {				
				rdrService.activateEvent(EVENT_TARGET_DISCOVERED);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_CURRENTLY_DISABLED)
					return;
			}
			ISOException.throwIt(SW_UNKNOWN_PROP);
		
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	public void onCallback(byte event, HCIMessage hcimessage) {
		// void
	}
	
	public void uninstall() {
		GPCLSystem.setCommunicationInterface(GPCLSystem.GPCL_INTERFACE_ISO14443, true);
		
	}

}
