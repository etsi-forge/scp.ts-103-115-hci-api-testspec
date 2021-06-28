package uicc.hci.test.services.readermode.api_2_rmm_sgp;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIMessage;
import uicc.hci.framework.HCIService;
import uicc.hci.services.readermode.ReaderListener;
import uicc.hci.services.readermode.ReaderMessage;
import uicc.hci.services.readermode.ReaderService;


public class Api_2_RMm_Sgp_1 extends Applet implements ReaderListener {

	/**
	 * INS values to determine which feature to test or verify
	 */
	private static final byte INS_GET_TYPE_A_UID = 1;
	private static final byte INS_GET_TYPE_A_ATQA = 2;
	private static final byte INS_GET_TYPE_A_APP_DATA = 3;
	private static final byte INS_GET_TYPE_A_SAK = 4;
	private static final byte INS_GET_TYPE_A_FWI = 5;
	private static final byte INS_GET_TYPE_A_DATA_R_MAX = 6;
	private static final byte INS_GET_TYPE_B_PUPI = 7;
	private static final byte INS_GET_TYPE_B_APP_DATA = 8;
	private static final byte INS_GET_TYPE_B_AFI = 9;
	private static final byte INS_GET_TYPE_B_HIGHER_LAYER_RESPONSE = 0x0A;
	private static final byte INS_GET_TYPE_B_HIGHER_LAYER_DATA = 0x0B;
	
	/**
	 * Keeps the feature to test. Value is set in process() and used in onCallback().
	 */
	private byte featureToTest;
	
	private boolean testSuccess;
	
	private HCIService readerService;

	public Api_2_RMm_Sgp_1() {
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
			readerService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);
			
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
		new Api_2_RMm_Sgp_1();
	}

	@Override
	public void process(APDU apdu) throws ISOException {
		/*
		 * Check for SELECT command
		 */
		if (selectingApplet()) {
			readerService.activateEvent(EVENT_TARGET_DISCOVERED);
			return;
    }
		/*
		 * analyze incoming data
		 */

		byte buffer[] = apdu.getBuffer();
		byte INS = buffer[ISO7816.OFFSET_INS];
		if (INS >= INS_GET_TYPE_A_UID
				&& INS <= INS_GET_TYPE_B_HIGHER_LAYER_DATA) {
			testSuccess = false;
			featureToTest = buffer[ISO7816.OFFSET_INS];
			return;
		} else if (INS == 0x20) {
			buffer[0] = featureToTest;
			buffer[1] = (byte) (testSuccess ? 0x00 : 0x01);
			apdu.setOutgoingAndSend((short) 0, (short) 2); 
		}
	}

	@Override
	public void onCallback(byte event, HCIMessage message) {
		ReaderMessage readerMessage = (ReaderMessage) message;

		if (readerMessage.getType() == ReaderMessage.TYPE_EVENT) {
			if (event == EVENT_TARGET_DISCOVERED) {

				byte instruction = readerMessage.getReceiveBuffer()[0];
				if (instruction == ReaderMessage.SINGLE_TARGET_STATUS) {
					byte featureID;

					switch (featureToTest) {
					case INS_GET_TYPE_A_UID:
						featureID = ReaderMessage.PARAM_ID_TYPE_A_READER_UID;
						break;
					case INS_GET_TYPE_A_ATQA:
						featureID = ReaderMessage.PARAM_ID_TYPE_A_READER_ATQA;
						break;
					case INS_GET_TYPE_A_APP_DATA:
						featureID = ReaderMessage.PARAM_ID_TYPE_A_READER_APPLICATION_DATA;
						break;
					case INS_GET_TYPE_A_SAK:
						featureID = ReaderMessage.PARAM_ID_TYPE_A_READER_SAK;
						break;
					case INS_GET_TYPE_A_FWI:
						featureID = ReaderMessage.PARAM_ID_TYPE_A_READER_FWI;
						break;
					case INS_GET_TYPE_A_DATA_R_MAX:
						featureID = ReaderMessage.PARAM_ID_TYPE_A_READER_DATARATE_MAX;
						break;
					case INS_GET_TYPE_B_PUPI:
						featureID = ReaderMessage.PARAM_ID_TYPE_B_READER_PUPI;
						break;
					case INS_GET_TYPE_B_APP_DATA:
						featureID = ReaderMessage.PARAM_ID_TYPE_B_READER_APPLICATION_DATA;
						break;
					case INS_GET_TYPE_B_AFI:
						featureID = ReaderMessage.PARAM_ID_TYPE_B_READER_AFI;
						break;
					case INS_GET_TYPE_B_HIGHER_LAYER_RESPONSE:
						featureID = ReaderMessage.PARAM_ID_TYPE_B_READER_HIGHER_LAYER_RESPONSE;
						break;
					case INS_GET_TYPE_B_HIGHER_LAYER_DATA:
						featureID = ReaderMessage.PARAM_ID_TYPE_B_READER_HIGHER_LAYER_DATA;
						break;
					default:
						featureID = -1;
					}
	
					readerMessage.prepareAndSendGetParameterCommand(featureID);
				}
			}
		} else if (readerMessage.getType() == ReaderMessage.TYPE_RESPONSE) {
			switch (featureToTest) {
			case INS_GET_TYPE_A_UID:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_A_READER_UID);
						break;
			case INS_GET_TYPE_A_ATQA:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_A_READER_ATQA);
						break;
			case INS_GET_TYPE_A_APP_DATA:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_A_READER_APPLICATION_DATA);
						break;
			case INS_GET_TYPE_A_SAK:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_A_READER_SAK);
						break;
			case INS_GET_TYPE_A_FWI:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_A_READER_FWI);
						break;
			case INS_GET_TYPE_A_DATA_R_MAX:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_A_READER_DATARATE_MAX);
						break;
			case INS_GET_TYPE_B_PUPI:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_B_READER_PUPI);
						break;
			case INS_GET_TYPE_B_APP_DATA:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_B_READER_APPLICATION_DATA);
						break;
			case INS_GET_TYPE_B_AFI:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_B_READER_AFI);
						break;
			case INS_GET_TYPE_B_HIGHER_LAYER_RESPONSE:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_B_READER_HIGHER_LAYER_RESPONSE);
						break;
			case INS_GET_TYPE_B_HIGHER_LAYER_DATA:
				testSuccess = (readerMessage.getReceiveBuffer()[0] 
						== ReaderMessage.PARAM_ID_TYPE_B_READER_HIGHER_LAYER_DATA);
						break;
			}
		}
	}
}
