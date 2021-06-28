package uicc.hci.test.services.readermode.api_2_rmm_sgp;

import org.globalplatform.contactless.GPCLSystem;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.AppletEvent;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.SystemException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.readermode.ReaderListener;
import uicc.hci.services.readermode.ReaderMessage;
import uicc.hci.services.readermode.ReaderService;

public class Api_2_RMm_Sgp_3 extends Applet implements ReaderListener, AppletEvent {

	/**
	 * INS values to determine which feature to test or verify
	 */
	private static final byte INS_TEST_HCI_DISABLED = 0x01;
	private static final byte INS_TEST_RECEIVING_HCI_MESSAGE = 0x02;
	private static final byte INS_TEST_ILLIGALVALUE = 0x05;
	private static final byte INS_VERIFY_HCI_DISABLED = 0x11;
	private static final byte INS_VERIFY_RECEIVING_HCI_MESSAGE = 0x12;
	private static final byte INS_VERIFY_ILLIGALVALUE = 0x15;
	
	/**
	 * Keeps the feature to test. Value is set in process() and used in onCallback().
	 */
	private byte featureToTest;

	/**
	 * Variables to remember the thrown exception. Set in onCallback() and evaluated in
	 * process() with INS >= 0x11.
	 */
	private boolean exceptionHciDisabledThrown;
	private boolean exceptionIlligalValueThrown;
	private boolean exceptionReceivingHciThrown;

	private ReaderService readerService;

	public Api_2_RMm_Sgp_3() {
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
		new Api_2_RMm_Sgp_3();
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
		case INS_TEST_HCI_DISABLED:
			featureToTest = 1;
			break;
		case INS_TEST_RECEIVING_HCI_MESSAGE:
			featureToTest = 2;
			break;
		case INS_TEST_ILLIGALVALUE:
			featureToTest = 5;
			break;
			
		case INS_VERIFY_HCI_DISABLED:
			if (exceptionHciDisabledThrown) {
				ISOException.throwIt((short) (ISO7816.SW_NO_ERROR + 3));
			}
			break;
		case INS_VERIFY_RECEIVING_HCI_MESSAGE:
			if (exceptionReceivingHciThrown) {
				ISOException.throwIt((short) (ISO7816.SW_NO_ERROR + 4));
			}
			break;
		case INS_VERIFY_ILLIGALVALUE:
			if (exceptionIlligalValueThrown) {
				ISOException.throwIt((short) (ISO7816.SW_NO_ERROR + 5));
			}
			break;			
		}
	}

	
	public void onCallback(byte event, HCIMessage message) {
		ReaderMessage readerMessage = (ReaderMessage) message;

		if (readerMessage.getType() == ReaderMessage.TYPE_EVENT) {
			if (event == EVENT_TARGET_DISCOVERED) {
				
				switch (readerMessage.getInstruction()) {
				// ID 1
				case ReaderMessage.SINGLE_TARGET_STATUS:
					byte paramID = 0;
					switch (featureToTest) {
					case 1:
						GPCLSystem.setCommunicationInterface(GPCLSystem.GPCL_INTERFACE_ISO14443, false);
						break;
						// fallthrough
					case 2:
						paramID = ReaderMessage.PARAM_ID_TYPE_A_READER_UID;
						break;
					case 5:
						paramID = -1;
						break;
					}

					try {
						readerMessage.prepareAndSendGetParameterCommand(paramID);
					} catch (HCIException e) {
						if (e.getReason() == HCIException.HCI_CURRENTLY_DISABLED) {
							if (featureToTest == 1) {
								exceptionHciDisabledThrown = true;
							}
						} else if (e.getReason() == HCIException.HCI_FRAGMENTED_MESSAGE_ONGOING) {
							if (featureToTest == 2) {
								exceptionReceivingHciThrown = true;
							}
						}
					} catch (SystemException e) {
						if (e.getReason() == SystemException.ILLEGAL_VALUE) {
							if (featureToTest == 5) {
								exceptionIlligalValueThrown = true;
							}
						}
					}
				}
			}
		}
	}

	
	public void uninstall() {
		GPCLSystem.setCommunicationInterface(GPCLSystem.GPCL_INTERFACE_ISO14443, true);
		
	}
}
