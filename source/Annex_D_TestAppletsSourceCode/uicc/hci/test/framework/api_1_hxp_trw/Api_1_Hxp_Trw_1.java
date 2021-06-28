package uicc.hci.test.framework.api_1_hxp_trw;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIException;

/**
 * The method with the following header shall be compliant to its definition in
 * the API.<br />
 * <code>public static void throwIt(short reason)throws HCIException</code>
 */
public class Api_1_Hxp_Trw_1 extends Applet {

	/*
	 * Define specific SWs
	 */

	private final static short SW_WRONG_EXECPTION = ISO7816.SW_UNKNOWN + (short) 1;

	/*
	 * Define specific INS bytes for HCIService tests
	 */

	private static final byte INS_EXP_HCI_ACCESS_NOT_GRANTED 			= (byte) 0x01;
	private static final byte INS_EXP_HCI_CONDITION_NOT_SATISFIED 		= (byte) 0x02;
	private static final byte INS_EXP_HCI_CURRENTLY_DISABLED 			= (byte) 0x03;
	private static final byte INS_EXP_HCI_FRAGMENT_MSG_ONGOING 			= (byte) 0x04;
	private static final byte INS_EXP_HCI_INVALID_LENGTH 				= (byte) 0x05;
	private static final byte INS_EXP_HCI_LISTENER_ALREADY_REGISTERED	= (byte) 0x06;
	private static final byte INS_EXP_HCI_NOT_AVAILABLE 				= (byte) 0x07;
	private static final byte INS_EXP_HCI_RESSOURCE_NOT_AVAILABLE 		= (byte) 0x08;
	private static final byte INS_EXP_HCI_SERVICE_NOT_AVAILABLE 		= (byte) 0x09;
	private static final byte INS_EXP_HCI_WRONG_EVENT_TYPE 				= (byte) 0x0A;
	private static final byte INS_EXP_HCI_WRONG_LISTENER_TYPE 			= (byte) 0x0B;

	/**
	 * Applet tests HCIMessage commands
	 */
	private Api_1_Hxp_Trw_1() {
		/*
		 * JavaCard applet register
		 */
		register();

	}

	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card
	 * runtime environment will call this static method first.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Api_1_Hxp_Trw_1();
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
		case INS_EXP_HCI_ACCESS_NOT_GRANTED:
			try {
				HCIException.throwIt(HCIException.HCI_ACCESS_NOT_GRANTED);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_ACCESS_NOT_GRANTED)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_CONDITION_NOT_SATISFIED:
			try {
				HCIException.throwIt(HCIException.HCI_CONDITIONS_NOT_SATISFIED);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_CONDITIONS_NOT_SATISFIED)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_CURRENTLY_DISABLED:
			try {
				HCIException.throwIt(HCIException.HCI_CURRENTLY_DISABLED);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_CURRENTLY_DISABLED)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_FRAGMENT_MSG_ONGOING:
			try {
				HCIException.throwIt(HCIException.HCI_FRAGMENTED_MESSAGE_ONGOING);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_FRAGMENTED_MESSAGE_ONGOING)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_INVALID_LENGTH:
			try {
				HCIException.throwIt(HCIException.HCI_INVALID_LENGTH);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_INVALID_LENGTH)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_LISTENER_ALREADY_REGISTERED:
			try {
				HCIException.throwIt(HCIException.HCI_LISTENER_ALREADY_REGISTERED);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_LISTENER_ALREADY_REGISTERED)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_NOT_AVAILABLE:
			try {
				HCIException.throwIt(HCIException.HCI_NOT_AVAILABLE);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_NOT_AVAILABLE)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_RESSOURCE_NOT_AVAILABLE:
			try {
				HCIException.throwIt(HCIException.HCI_RESOURCES_NOT_AVAILABLE);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_RESOURCES_NOT_AVAILABLE)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_SERVICE_NOT_AVAILABLE:
			try {
				HCIException.throwIt(HCIException.HCI_SERVICE_NOT_AVAILABLE);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_SERVICE_NOT_AVAILABLE)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_WRONG_EVENT_TYPE:
			try {
				HCIException.throwIt(HCIException.HCI_WRONG_EVENT_TYPE);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_WRONG_EVENT_TYPE)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		case INS_EXP_HCI_WRONG_LISTENER_TYPE:
			try {
				HCIException.throwIt(HCIException.HCI_WRONG_LISTENER_TYPE);
			} catch (HCIException e) {
				if (e.getReason() == HCIException.HCI_WRONG_LISTENER_TYPE)
					return;
			}
			ISOException.throwIt(SW_WRONG_EXECPTION);

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

	}
}
