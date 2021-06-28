package uicc.hci.test.services.connectivity.api_2_cns_ste;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.services.connectivity.ConnectivityService;

/**
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>void prepareAndSendTransactionEvent(byte [] aid,<br />
 * &nbsp;&nbsp;&nbsp;short aidOffset,<br />
 * &nbsp;&nbsp;&nbsp;short aidLen,<br />
 * &nbsp;&nbsp;&nbsp;byte[] parameters,<br />
 * &nbsp;&nbsp;&nbsp;short parametersOffset,<br />
 * &nbsp;&nbsp;&nbsp;short parametersLen)<br />
 * throws HCIException<br />
 * &nbsp;&nbsp;&nbsp;java.lang.ArrayIndexOutOfBoundsException, java.lang.NullPointerException</code>
 */
public class Api_2_CNs_Ste_6 extends Applet {

	private static final byte INS_PREPARE_AND_SEND_TRANS_EVT = 0x01;

	private byte[] aid;
	private byte[] params;

	private ConnectivityService cnnService;

	private Api_2_CNs_Ste_6() {
        // JavaCard applet register
		register();

        // HCI listener register - connectivity
		try {
			cnnService = (ConnectivityService) HCIDevice.getHCIService(HCIDevice.CONNECTIVITY_SERVICE_ID);
		} catch (HCIException e) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}

        // initialise the arrays
		aid = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
		params = JCSystem.makeTransientByteArray((short) 10, JCSystem.CLEAR_ON_RESET);
	}

	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_2_CNs_Ste_6();
	}

	public void process(APDU apdu) throws ISOException {
		if (selectingApplet()) {
            return;
        }

		/*
		 * analyze incoming data
		 */

		byte buffer[] = apdu.getBuffer();
		
		aid[0] = (byte) (0xFF & 0xA0);
		aid[1] = (byte) (0xFF & 0x00);
		aid[2] = (byte) (0xFF & 0x00);
		aid[3] = (byte) (0xFF & 0x00);
		aid[4] = (byte) (0xFF & 0x09);
		aid[5] = (byte) (0xFF & 0x01);
		aid[6] = (byte) (0xFF & 0x01);
		aid[7] = (byte) (0xFF & 0x01);
		aid[8] = (byte) (0xFF & 0x01);
		aid[9] = (byte) (0xFF & 0x01);
		aid[10] = (byte) (0xFF & 0x01);
		aid[11] = (byte) (0xFF & 0x01);
		aid[12] = (byte) (0xFF & 0x01);
		aid[13] = (byte) (0xFF & 0x01);
		aid[14] = (byte) (0xFF & 0x01);
		aid[15] = (byte) (0xFF & 0x01);

		params[0] = (byte) (0xFF & 0x01);
		params[1] = (byte) (0xFF & 0x01);
		params[2] = (byte) (0xFF & 0x01);
		params[3] = (byte) (0xFF & 0x01);
		params[4] = (byte) (0xFF & 0x01);
		params[5] = (byte) (0xFF & 0x01);
		params[6] = (byte) (0xFF & 0x01);
		params[7] = (byte) (0xFF & 0x01);
		params[8] = (byte) (0xFF & 0x01);
		params[9] = (byte) (0xFF & 0x01);

		switch (buffer[ISO7816.OFFSET_INS]) {

		case INS_PREPARE_AND_SEND_TRANS_EVT:
            // call the method; we expect this to be successful
			try {
				cnnService.prepareAndSendTransactionEvent(aid,(short) 0,(short) 16, params, (short)0,(short)10);
				return;
			} catch (HCIException e) {
                // in the case of an HCIException, throw 6FXX with SW2 = reason
                ISOException.throwIt((short)(ISO7816.SW_UNKNOWN | (byte)e.getReason()));
			}			
			
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
}
