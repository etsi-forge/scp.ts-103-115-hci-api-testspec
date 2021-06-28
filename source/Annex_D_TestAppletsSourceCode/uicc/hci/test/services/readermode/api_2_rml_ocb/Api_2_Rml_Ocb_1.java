package uicc.hci.test.services.readermode.api_2_rml_ocb;

import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.readermode.ReaderListener;
import uicc.hci.services.readermode.ReaderMessage;
import uicc.hci.services.readermode.ReaderService;
import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class Api_2_Rml_Ocb_1 extends Applet implements ReaderListener {

	/**
	 * INS values
	 */
	private static final byte INS_HCI_ACTIVATE_TARGET_DICOVERED = 0x10;
	
	private ReaderService readerService;

	public Api_2_Rml_Ocb_1() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			readerService = (ReaderService) HCIDevice.getHCIService(HCIDevice.READER_SERVICE_ID);
			readerService.register(this);
			
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
		new Api_2_Rml_Ocb_1();
	}

	@Override
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

		case INS_HCI_ACTIVATE_TARGET_DICOVERED:
		}
	}

	@Override
	public void onCallback(byte event, HCIMessage message) {
		ReaderMessage readerMessage = (ReaderMessage) message;
		
		if (readerMessage.getType() == ReaderMessage.TYPE_EVENT) {
			if (event == EVENT_TARGET_DISCOVERED) {
				byte timeout = -1;
				byte[] data = new byte[] { (byte) 0x00, (byte) 0x01,
						(byte) 0x00, (byte) 0x00 };
				short offset = 0;
				short len = (short) data.length;

				readerService.activateEvent(EVENT_WRITE_EXCHANGE_DATA_RESPONSE);
				readerMessage.prepareAndSendWriteXchgDataCommand(timeout,
						data, offset, len);
			} else if (event == EVENT_WRITE_EXCHANGE_DATA_RESPONSE) {
				readerService.activateEvent(EVENT_HCI_TRANSMISSION_FAILED);
			} else if (event == EVENT_HCI_TRANSMISSION_FAILED) {
				readerService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);				
				readerMessage.prepareAndSendGetParameterCommand(ReaderMessage.PARAM_ID_TYPE_A_READER_ATQA);
			}
		}
	}

}
