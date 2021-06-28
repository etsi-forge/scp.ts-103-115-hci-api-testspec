package uicc.hci.test.services.readermode.api_2_rmm_srx;


import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.SystemException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.readermode.ReaderListener;
import uicc.hci.services.readermode.ReaderMessage;
import uicc.hci.services.readermode.ReaderService;

public class Api_2_RMm_Srx_2 extends Applet implements ReaderListener {

	/**
	 * INS values to determine which feature to test or verify
	 */
	private static final byte INS_TEST_HCI_DISABLED = 0x01;
	private static final byte INS_TEST_NULLPOINTER = 0x02;
	private static final byte INS_TEST_ARRAYBOUNDS_L = 0x03;
	private static final byte INS_TEST_ARRAYBOUNDS_H = 0x04;
	private static final byte INS_TEST_ILLIGALVALUE = 0x05;
	private static final byte INS_VERIFY_HCI_DISABLED = 0x11;
	private static final byte INS_VERIFY_NULLPOINTER = 0x12;
	private static final byte INS_VERIFY_ARRAYBOUNDS_L = 0x13;
	private static final byte INS_VERIFY_ARRAYBOUNDS_H = 0x14;
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
	private boolean exceptionArrayIndexThrown;
	private boolean exceptionIlligalValueThrown;
	private boolean exceptionNullPointerThrown;

	private ReaderService readerService;

	public Api_2_RMm_Srx_2() {
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
		new Api_2_RMm_Srx_2();
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
		case INS_TEST_NULLPOINTER:
			featureToTest = 2;
			break;
		case INS_TEST_ARRAYBOUNDS_L:
			featureToTest = 3;
			break;
		case INS_TEST_ARRAYBOUNDS_H:
			featureToTest = 4;
			break;
		case INS_TEST_ILLIGALVALUE:
			featureToTest = 5;
			break;
			
		case INS_VERIFY_HCI_DISABLED:
			if (exceptionHciDisabledThrown) {
				ISOException.throwIt((short) (ISO7816.SW_NO_ERROR + 1));
			}
			break;
		case INS_VERIFY_NULLPOINTER:
			if (exceptionNullPointerThrown) {
				ISOException.throwIt((short) (ISO7816.SW_NO_ERROR + 2));
			}
			break;
		case INS_VERIFY_ARRAYBOUNDS_L:
			if (exceptionArrayIndexThrown) {
				ISOException.throwIt((short) (ISO7816.SW_NO_ERROR + 3));
			}
			break;
		case INS_VERIFY_ARRAYBOUNDS_H:
			if (exceptionArrayIndexThrown) {
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
				
				switch (readerMessage.getReceiveBuffer()[0]) {
				// ID 1
				case ReaderMessage.SINGLE_TARGET_STATUS:

					byte timeout = 14;
					byte[] data = new byte[] { (byte) 0x01, (byte) 0x01,
							(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
							(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01 };
					short offset = 0;
					short len = (short) data.length;
					
					switch (featureToTest) {
					case 1:
                                                break;

					case 2:
						data = null;
						break;
					case 3:
						offset = -1;
						break;
						
					case 4:
						len += 1;
						break;
						
					case 5:
						timeout = -2;
						break;
					}

					try {
						readerMessage.prepareAndSendWriteXchgDataCommand(timeout,
								data, offset, len);
					} catch (HCIException e) {
						if (e.getReason() == HCIException.HCI_CURRENTLY_DISABLED) {
							if (featureToTest == 1) {
								exceptionHciDisabledThrown = true;
							}
							return;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						if (featureToTest == 3 || featureToTest == 4) {
							exceptionArrayIndexThrown = true;
						}
						return;
					} catch (SystemException e) {
						if (e.getReason() == SystemException.ILLEGAL_VALUE) {
							if (featureToTest == 5) {
								exceptionIlligalValueThrown = true;
							}
						}
						return;
					} catch (NullPointerException e) {
						if (featureToTest == 2) {
							exceptionNullPointerThrown = true;
						}
						return;
					}
					return;
				}
			}
		}
	}
}
