package uicc.hci.test.framework.api_1_hsr_reg;

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
import uicc.hci.services.connectivity.ConnectivityService;

/**
 * The method with the following header shall be compliant to its definition in the API.
 * <code>void register(HCIListener listener) throws HCIException</code>
 */
public class Api_1_Hsr_Reg_1 extends Applet implements CardEmulationListener {

	/*
	 * Define specific SWs
	 */

	private final static short SW_WRONG_REGISTER_USAGE = ISO7816.SW_UNKNOWN + (short) 1;

	/*
	 * Define specific INS bytes for HCIService tests
	 */

	private final static short INS_REGISTER_CEM_SERVCE = (short) 0x09;
	private final static short INS_REGISTER_CNN_SERVCE = (short) 0x0A;

	private CardEmulationService ceService;
	private ConnectivityService cnnService;

	private Api_1_Hsr_Reg_1() {
		register();
	}

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_1_Hsr_Reg_1();
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

		/*
		 * HCIServce.register()
		 */
		case INS_REGISTER_CEM_SERVCE:
			try {
				ceService = (CardEmulationService) HCIDevice.getHCIService(HCIDevice.CARD_EMULATION_SERVICE_ID);
				ceService.register(this);
			} catch (HCIException e) {
				ISOException.throwIt(SW_WRONG_REGISTER_USAGE);
			}
			return;
			
		case INS_REGISTER_CNN_SERVCE:
			try {
				cnnService = (ConnectivityService) HCIDevice.getHCIService(HCIDevice.CONNECTIVITY_SERVICE_ID);
				cnnService.register(this);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_WRONG_LISTENER_TYPE)
					return;

			}
			ISOException.throwIt(SW_WRONG_REGISTER_USAGE);

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

	}

	/**
	 * Not used
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte arg0, HCIMessage arg1) {

	}

}
