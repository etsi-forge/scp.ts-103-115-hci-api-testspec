package uicc.hci.test.framework.api_1_hsr_gen;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.readermode.ReaderListener;
import uicc.hci.services.readermode.ReaderService;

/**
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>boolean getEventNotificationStatus(byte event) throws HCIException</code>
 */
public class Api_1_Hsr_Gen_3 extends Applet implements ReaderListener {

	/*
	 * Define specific SWs
	 */

	private static final short SW_EVENT_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 1;
	private static final short SW_METHOD_NOT_SUPPORTED = ISO7816.SW_UNKNOWN + (short) 2;

	/*
	 * Define specific INS bytes
	 */
	// evt notification status true
	private final static byte INS_EVENT_T_WRT_EXCH_DATA = (byte) 0x21;
	private final static byte INS_EVENT_T_TARG_DISC = (byte) 0x22;
	private final static byte INS_EVENT_T_PARAM_RESP = (byte) 0x23;
	private final static byte INS_EVENT_T_RCP_FAILED = (byte) 0x24;
	private final static byte INS_EVENT_T_TM_FAILED = (byte) 0x25;
	
	// evt notification status false
	private final static byte INS_EVENT_F_TM_FAILED = (byte) 0x10;
	private final static byte INS_EVENT_F_PARAM_RESP = (byte) 0x11;
	private final static byte INS_EVENT_F_WRT_EXCH_DATA = (byte) 0x12;
	private final static byte INS_EVENT_F_TARG_DISC = (byte) 0x13;
	private final static byte INS_EVENT_F_RCP_FAILED = (byte) 0x14;

	/*
	 * Define specific INS bytes for HCIService tests
	 */
	private final static byte INS_WRONG_EVENT = (byte) 0x17;

	/*
	 * Local variables
	 */

	private ReaderService rdrService;

	private Api_1_Hsr_Gen_3() {
		/*
		 * JavaCard applet register
		 */
		register();

		/*
		 * HCI listener register
		 */
		try {
			rdrService = (ReaderService) HCIDevice.getHCIService(HCIDevice.READER_SERVICE_ID);
			rdrService.register(this);
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
		new Api_1_Hsr_Gen_3();
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

		/*
		 * HCIServce.getEventNotification()
		 */
		case INS_EVENT_T_TM_FAILED:
			try {
				rdrService.activateEvent(EVENT_HCI_TRANSMISSION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (!rdrService.getEventNotificationStatus(EVENT_HCI_TRANSMISSION_FAILED)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;

		case INS_EVENT_T_RCP_FAILED:
			try {
				rdrService.activateEvent(EVENT_HCI_RECEPTION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (!rdrService.getEventNotificationStatus(EVENT_HCI_RECEPTION_FAILED)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;

		case INS_EVENT_T_WRT_EXCH_DATA:
			try {
				rdrService.activateEvent(EVENT_WRITE_EXCHANGE_DATA_RESPONSE);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (!rdrService.getEventNotificationStatus(EVENT_WRITE_EXCHANGE_DATA_RESPONSE)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;

		case INS_EVENT_T_TARG_DISC:
			try {
				rdrService.activateEvent(EVENT_TARGET_DISCOVERED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (!rdrService.getEventNotificationStatus(EVENT_TARGET_DISCOVERED)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;
			
		case INS_EVENT_T_PARAM_RESP:
			try {
				rdrService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (!rdrService.getEventNotificationStatus(EVENT_GET_PARAMETER_RESPONSE)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;
			
		case INS_EVENT_F_TM_FAILED:
			try {
				rdrService.activateEvent(EVENT_HCI_TRANSMISSION_FAILED);
				rdrService.deactivateEvent(EVENT_HCI_TRANSMISSION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (rdrService.getEventNotificationStatus(EVENT_HCI_TRANSMISSION_FAILED)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;

		case INS_EVENT_F_RCP_FAILED:
			try {
				rdrService.activateEvent(EVENT_HCI_RECEPTION_FAILED);
				rdrService.deactivateEvent(EVENT_HCI_RECEPTION_FAILED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (rdrService.getEventNotificationStatus(EVENT_HCI_RECEPTION_FAILED)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;

		case INS_EVENT_F_WRT_EXCH_DATA:
			try {
				rdrService.activateEvent(EVENT_WRITE_EXCHANGE_DATA_RESPONSE);
				rdrService.deactivateEvent(EVENT_WRITE_EXCHANGE_DATA_RESPONSE);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (rdrService.getEventNotificationStatus(EVENT_WRITE_EXCHANGE_DATA_RESPONSE)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;

		case INS_EVENT_F_TARG_DISC:
			try {
				rdrService.activateEvent(EVENT_TARGET_DISCOVERED);
				rdrService.deactivateEvent(EVENT_TARGET_DISCOVERED);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (rdrService.getEventNotificationStatus(EVENT_TARGET_DISCOVERED)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;
			
		case INS_EVENT_F_PARAM_RESP:
			try {
				rdrService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);
				rdrService.deactivateEvent(EVENT_GET_PARAMETER_RESPONSE);
			} catch (HCIException e) {
				ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);
			}
			if (rdrService.getEventNotificationStatus(EVENT_GET_PARAMETER_RESPONSE)) 
				ISOException.throwIt(SW_METHOD_NOT_SUPPORTED);			
			return;
		case INS_WRONG_EVENT:
			try {
				rdrService.getEventNotificationStatus((byte) 0x84);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_WRONG_EVENT_TYPE) 
					return;				
			}
			ISOException.throwIt(SW_EVENT_NOT_SUPPORTED);

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

	}

	/**
	 * Not used.<br />
	 * This method is called by the HCI framework to inform the Listener Object
	 * about a specific event and pass the corresponding HCIMessage to the
	 * Listener Object.
	 * 
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage message) {

	}

}
