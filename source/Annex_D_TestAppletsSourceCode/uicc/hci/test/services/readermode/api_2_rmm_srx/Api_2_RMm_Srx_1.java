package uicc.hci.test.services.readermode.api_2_rmm_srx;

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

public class Api_2_RMm_Srx_1 extends Applet implements ReaderListener {

    /*
     * INS values to determine which feature to test.
     */
    private static final byte INS_TEST_COMMAND_CASE_1 = 0x01;
    private static final byte INS_TEST_COMMAND_CASE_2 = 0x02;
    private static final byte INS_TEST_COMMAND_CASE_3 = 0x03;
    private static final byte INS_TEST_COMMAND_CASE_4 = 0x04;
    private static final byte INS_TEST_COMMAND_SET_TIMEOUT_0 = 0x05;
    private static final byte INS_TEST_COMMAND_SET_TIMEOUT_5 = 0x06;
    private static final byte INS_TEST_COMMAND_SET_TIMEOUT_14 = 0x07;

    /**
	 * Indicates the feature to test. Value is set in process() and used in onCallback().
	 */
	private byte featureToTest;

    private ReaderService readerService;

	public Api_2_RMm_Srx_1() {
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
		new Api_2_RMm_Srx_1();
	}

	@Override 
	public void process(APDU apdu) throws ISOException {
        if (selectingApplet())
        {
            readerService.activateEvent(EVENT_TARGET_DISCOVERED);
            return;
        }

        // check which feature to test
        byte buffer[] = apdu.getBuffer();
        switch (buffer[ISO7816.OFFSET_INS])
        {
            case INS_TEST_COMMAND_CASE_1:
                featureToTest = 1;
                break;
            case INS_TEST_COMMAND_CASE_2:
                featureToTest = 2;
                break;
            case INS_TEST_COMMAND_CASE_3:
                featureToTest = 3;
                break;
            case INS_TEST_COMMAND_CASE_4:
                featureToTest = 4;
                break;
            case INS_TEST_COMMAND_SET_TIMEOUT_0:
                featureToTest = 5;
                break;
            case INS_TEST_COMMAND_SET_TIMEOUT_5:
                featureToTest = 6;
                break;
            case INS_TEST_COMMAND_SET_TIMEOUT_14:
                featureToTest = 7;
                break;
        }
    }

	@Override
	public void onCallback(byte event, HCIMessage message) {
		ReaderMessage readerMessage = (ReaderMessage) message;

		if (readerMessage.getType() == ReaderMessage.TYPE_EVENT) {
			if (event == EVENT_TARGET_DISCOVERED) {
				
				switch (readerMessage.getReceiveBuffer()[0]) {
				// ID 1
				case ReaderMessage.SINGLE_TARGET_STATUS:
                    // default to -1 unless modified below
					byte timeout = -1;
					byte[] data;
                    switch (featureToTest) {
                        case 1:
                            // ISO/IEC 7816-3, case 1: 00 01 00 00
                            data = new byte[]{(byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00};
                            break;
                        case 2:
                            // ISO/IEC 7816-3, case 2: 80 02 01 02 00
                            data = new byte[]{(byte)0x80, (byte)0x02, (byte)0x01, (byte)0x02, (byte)0x00};
                            break;
                        case 3:
                            // ISO/IEC 7816-3, case 3: A0 03 FE FF 02 3F 00
                            data = new byte[]{(byte)0xA0, (byte)0x03, (byte)0xFE, (byte)0xFF, (byte)0x02, (byte)0x3F, (byte)0x00};
                            break;
                        case 4:
                            // ISO/IEC 7816-3, case 4: 00 04 00 00 XX 01 ... FF 00
                            data = new byte[254];
                            data[0] = (byte)0x00;
                            data[1] = (byte)0x04;
                            data[2] = (byte)0x00;
                            data[3] = (byte)0x00;
                            short commandDataLength = 0xF8;
                            data[4] = (byte)commandDataLength;
                            for (short commandDataIndex = 0; commandDataIndex < commandDataLength; commandDataIndex++) {
                                short value = (short)(commandDataIndex + 1);
                                short dataIndex = (short)(5 + commandDataIndex);
                                data[dataIndex] = (byte)value;
                            }
                            data[253] = (byte)0x00;
                        break;
                        case 5:
                            timeout = 0;
                            // data = '00 05 00 00 02 3F 00 00'
                            data = createTimingTestAPDU((byte)0x05);
                            break;
                        case 6:
                            timeout = 5;
                            // data = '00 06 00 00 02 3F 00 00'
                            data = createTimingTestAPDU((byte)0x06);
                            break;
                        case 7:
                            timeout = 14;
                            // data = '00 07 00 00 02 3F 00 00'
                            data = createTimingTestAPDU((byte)0x07);
                            break;
                        default:
                            // invalid value for feature
                            return;
                    }

                    short offset = 0;
                    short len = (short)data.length;
					readerMessage.prepareAndSendWriteXchgDataCommand(timeout, data, offset, len);
					return;
				}
			}
		}
	}

    /**
     * Creates a representative APDU for the timeout sub-cases.
     */
    private static byte[] createTimingTestAPDU(byte ins)
    {
        return new byte[]{(byte)0x00, ins, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x3F, (byte)0x00, (byte)0x00};
    }
}
