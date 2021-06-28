package uicc.hci.test.framework.api_1_hdv_gsr;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.SystemException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationService;
import uicc.hci.services.connectivity.ConnectivityService;
import uicc.hci.services.readermode.ReaderService;

/**
 * Checks, that the method with the following header shall be compliant to its
 * definition in the API <br />
 * <code>public static HCIService getHCIService(short serviceID)throws HCIException, javacard.framework.SystemException</code>
 * <br />
 * This applet is used for the following test scripts:
 * <ul>
 * <li>Test_Api_1_Hdv_Gsr_1
 * <li>Test_Api_1_Hdv_Gsr_2
 * <li>Test_Api_1_Hdv_Gsr_3
 * <li>Test_Api_1_Hdv_Gsr_5
 * <li>Test_Api_1_Hdv_Gsr_6
 * <li>Test_Api_1_Hdv_Gsr_7
 * </ul>
 * 
 */
public class Api_1_Hdv_Gsr_1 extends Applet implements CardEmulationListener {

	/*
	 * Define specific SWs
	 */

	private final static short SW_SERVICE_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 1;
	private final static short SW_SERVICE_NOT_SUPPORTED_AS_EXPECTED = ISO7816.SW_NO_ERROR + (short) 1;
	private final static short SW_SERVICE_ACCESS_NOT_GRANTED = ISO7816.SW_NO_ERROR + (short) 2;

	/*
	 * Define specific INS bytes for HCIDevice tests
	 */

	private final static short INS_HCI_CE_SERVCE = (short) 0x01;
	private final static short INS_HCI_CNN_SERVCE = (short) 0x02;
	private final static short INS_HCI_RM_SERVCE = (short) 0x03;
	private final static short INS_HCI_ILLEGAL_SERVCE = (short) 0x04;

	private Api_1_Hdv_Gsr_1() {
		register();
	}

	/*
	 * Define HCI specific constants
	 */
	private CardEmulationService ceService;
	private ConnectivityService cnnService;
	private ReaderService rmService;

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength)
			throws ISOException {
		new Api_1_Hdv_Gsr_1();
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
		case INS_HCI_CE_SERVCE:
			try {
				ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
				if (ceService == null)
					ISOException.throwIt(ISO7816.SW_WRONG_DATA);
			} catch (HCIException e) {
				checkException(e);
			}
			return;
		case INS_HCI_CNN_SERVCE:
			try {
				cnnService = (ConnectivityService) HCIDevice.getHCIService(HCIDevice.CONNECTIVITY_SERVICE_ID);
				if (cnnService == null)
					ISOException.throwIt(ISO7816.SW_WRONG_DATA);
			} catch (HCIException e) {
				checkException(e);
			}
			return;
		case INS_HCI_RM_SERVCE:
			try {
				rmService = (ReaderService) HCIDevice.getHCIService(HCIDevice.READER_SERVICE_ID);
				if (rmService == null)
					ISOException.throwIt(ISO7816.SW_WRONG_DATA);
			} catch (HCIException e) {
				checkException(e);
			}
			return;
		case INS_HCI_ILLEGAL_SERVCE:
			short illegalService = (short) -1;
			try {
				HCIDevice.getHCIService(illegalService);				
			} catch (SystemException e) {
				if (e.getReason() == SystemException.ILLEGAL_VALUE) 
					return; //expected result
				
			}
			ISOException.throwIt(SW_SERVICE_NOT_SUPPORTED);

		}
		//all "good" cases should have been executed before
		ISOException.throwIt(ISO7816.SW_WRONG_DATA);

	}

	private void checkException(HCIException exp) {

		if (exp.getReason() == HCIException.HCI_SERVICE_NOT_AVAILABLE) {
			ISOException.throwIt(SW_SERVICE_NOT_SUPPORTED_AS_EXPECTED);
		}

		if (exp.getReason() == HCIException.HCI_ACCESS_NOT_GRANTED) {
			ISOException.throwIt(SW_SERVICE_ACCESS_NOT_GRANTED);
		}

		ISOException.throwIt(SW_SERVICE_NOT_SUPPORTED);
	}

	/**
	 * Not used<br />
	 * This method is called by the HCI framework to inform the Listener Object about a specific event and pass the corresponding
	 * HCIMessage to the Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte arg0, HCIMessage arg1) {

	}

}
