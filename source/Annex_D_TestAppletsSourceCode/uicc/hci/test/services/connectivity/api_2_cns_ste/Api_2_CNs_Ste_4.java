package uicc.hci.test.services.connectivity.api_2_cns_ste;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.connectivity.ConnectivityListener;
import uicc.hci.services.connectivity.ConnectivityService;

/**
 * TODO review!<br/>
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
public class Api_2_CNs_Ste_4 extends Applet implements ConnectivityListener {


	private static final byte INS_SEND_DATA_WRONG_CONDITIONS = 0x03;
	
	private byte[] aid;
	private byte[] params;

	/*
	 * Define HCI specific constants
	 */
	private ConnectivityService cnnService;

	/**
	 * Applet tests wrong conditions for connectivity service
	 */
	private Api_2_CNs_Ste_4() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			cnnService = (ConnectivityService) HCIDevice.getHCIService(HCIDevice.CONNECTIVITY_SERVICE_ID);
			cnnService.register(this);
		} catch (HCIException e) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		
		aid = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
		params = JCSystem.makeTransientByteArray((short) 10, JCSystem.CLEAR_ON_RESET);

	}

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_2_CNs_Ste_4();
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

	}

	/**
	 * Not used.<br />
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

			
		case INS_SEND_DATA_WRONG_CONDITIONS:
			try {
				cnnService.prepareAndSendTransactionEvent(aid,(short) 0,(short) 16, params, (short)0,(short)10);
				return;
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_CONDITIONS_NOT_SATISFIED)
					return;
			}	
			ISOException.throwIt(ISO7816.SW_WRONG_DATA);
							

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
}

