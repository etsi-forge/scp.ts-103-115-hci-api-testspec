package uicc.hci.test.services.readermode.api_2_rmm_rrp;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.readermode.ReaderListener;
import uicc.hci.services.readermode.ReaderMessage;
import uicc.hci.services.readermode.ReaderService;

public class Api_2_RMm_Rrp_1 extends Applet implements ReaderListener {
	
	/**
	 * set to true when a response to a write exchange data message is expected.
	 */
	private boolean expectHciTransmissionFailed = false;
	private ReaderService readerService;

	public Api_2_RMm_Rrp_1() {
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
			readerService.activateEvent(EVENT_WRITE_EXCHANGE_DATA_RESPONSE);
			
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
		new Api_2_RMm_Rrp_1();
	}

	public void process(APDU apdu) throws ISOException {
	     if (selectingApplet()) {
            readerService.activateEvent(EVENT_TARGET_DISCOVERED);
        }
			return;
	}

	public void onCallback(byte event, HCIMessage message) {
		ReaderMessage readerMessage = (ReaderMessage) message;
		
		if (readerMessage.getType() == ReaderMessage.TYPE_EVENT) {
			if (event == EVENT_TARGET_DISCOVERED) {
				
				switch (readerMessage.getReceiveBuffer()[0]) {
				// ID 1
				case ReaderMessage.MULTIPLE_TARGET_STATUS:
					readerMessage.restartReaderModeProcedure();
					return;

				// ID 2
				case ReaderMessage.SINGLE_TARGET_STATUS:
					byte timeout = -1;
					byte[] data = new byte[] { (byte) 0x01, (byte) 0x01,
							(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
							(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01 };
					short offset = 0;
					short len = (short) data.length;

					expectHciTransmissionFailed = true;

					readerMessage.prepareAndSendWriteXchgDataCommand(timeout,
							data, offset, len);
					return;
				}
			}
		}
		
		if (readerMessage.getType() == ReaderMessage.TYPE_RESPONSE) {
			if (readerMessage.getInstruction() == ReaderMessage.RESP_WR_RF_ERROR) {
				if (expectHciTransmissionFailed) {
					readerMessage.restartReaderModeProcedure();
				}
			}
		}

	}

}
