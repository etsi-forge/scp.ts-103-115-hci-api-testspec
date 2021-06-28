package uicc.hci.test.services.readermode.api_2_rmm_rrp;

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
import uicc.hci.services.readermode.ReaderMessage;
import uicc.hci.services.readermode.ReaderService;

public class Api_2_RMm_Rrp_3 extends Applet implements ReaderListener, AppletEvent {
	
	private static final byte INS_HCI_INTERFACE_DISABLED = 0x01;
	private static final short OK_EXCEPTION_DISABLED_RECEIVED = (short) 0x9003;

	
	/**
	 * set to true when a response to a write exchange data message is expected.
	 */
	private static boolean expectHciTransmissionFailed = false;
	
	private ReaderService readerService;
	
	/**
	 * set to true when the HCIException.HCI_CURRENTLY_DISABLED was thrown.
	 */
	private static boolean exceptionDisabledThrown = false;

	public Api_2_RMm_Rrp_3() {
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
		new Api_2_RMm_Rrp_3();
	}

	public void process(APDU apdu) throws ISOException {
		/*
		 * Check for SELECT command
		 */
		if (selectingApplet()){
			readerService.activateEvent(EVENT_TARGET_DISCOVERED);
			return;
    }
		/*
		 * analyze incoming data
		 */

		byte buffer[] = apdu.getBuffer();

		switch (buffer[ISO7816.OFFSET_INS]) {

		case INS_HCI_INTERFACE_DISABLED:
			if (exceptionDisabledThrown) {
				ISOException.throwIt(OK_EXCEPTION_DISABLED_RECEIVED);
			}
		
		}
	}

	public void onCallback(byte event, HCIMessage message) {
		ReaderMessage readerMessage = (ReaderMessage) message;
		
		if (readerMessage.getType() == ReaderMessage.TYPE_EVENT) {
			if (event == EVENT_TARGET_DISCOVERED) {
				
				switch (readerMessage.getReceiveBuffer()[0]) {

				// ID 3
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
				// ID 4
				case ReaderMessage.MULTIPLE_TARGET_STATUS:
					GPCLSystem.setCommunicationInterface(GPCLSystem.GPCL_INTERFACE_ISO14443, false);
					try {
						readerMessage.restartReaderModeProcedure();
					} catch (HCIException e) {
						if (e.getReason() == HCIException.HCI_CURRENTLY_DISABLED) {
							exceptionDisabledThrown  = true;
						}

					}
					return;
				}
			}
		}
		
		if (readerMessage.getType() == ReaderMessage.TYPE_RESPONSE) {
			if (readerMessage.getInstruction() == ReaderMessage.RESP_ANY_E_TIMEOUT) {
				if (expectHciTransmissionFailed) {
					readerMessage.restartReaderModeProcedure();
				}
			}
		}

	}

	
	public void uninstall() {
		GPCLSystem.setCommunicationInterface(GPCLSystem.GPCL_INTERFACE_ISO14443, true);
		
	}
	
}
