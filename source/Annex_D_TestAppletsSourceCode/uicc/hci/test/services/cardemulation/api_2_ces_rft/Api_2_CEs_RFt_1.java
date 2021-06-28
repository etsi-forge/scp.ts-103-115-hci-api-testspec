package uicc.hci.test.services.cardemulation.api_2_ces_rft;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationService;

/**
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>byte getCardRFType()</code>
 */
public class Api_2_CEs_RFt_1 extends Applet implements CardEmulationListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_TYPE_A_RF = ISO7816.SW_NO_ERROR + (short) 1;
	private static final short SW_TYPE_B_RF = ISO7816.SW_NO_ERROR + (short) 2;
	private static final short SW_TYPE_B_PRIM_RF = ISO7816.SW_NO_ERROR + (short) 3;
	private static final short SW_TYPE_F_RF = ISO7816.SW_NO_ERROR + (short) 4;
	private static final short SW_TYPE_COULD_NOT_BE_RETRIEVED = ISO7816.SW_UNKNOWN + (short) 7;
	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes for HCIService tests
	 */

	private static final byte INS_RETRIEVE_CARD_RF_TYPE = (byte) 0x01;

	/*
	 * 
	 */

	private CardEmulationService ceService;

	private Api_2_CEs_RFt_1() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
			ceService.register(this);
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
		new Api_2_CEs_RFt_1();
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

		case INS_RETRIEVE_CARD_RF_TYPE:

			ISOException.throwIt(checkCardType(ceService.getCardRFType()));

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

	}

	private short checkCardType(byte cardType) {
		switch (cardType) {
		case CardEmulationService.TYPE_A_CARD_RF:
			return SW_TYPE_A_RF;
		case CardEmulationService.TYPE_B_CARD_RF:
			return SW_TYPE_B_RF;
		case CardEmulationService.TYPE_B_PRIM_CARD_RF:
			return SW_TYPE_B_PRIM_RF;
		case CardEmulationService.TYPE_F_CARD_RF:
			return SW_TYPE_F_RF;
		case (-1):
			return SW_TYPE_COULD_NOT_BE_RETRIEVED;

		default:
			return SW_METHOD_NOT_SUPPORTED;
		}
	}

	/**
	 * Not used<br />
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte arg0, HCIMessage arg1) {

	}

}
