package uicc.hci.test.services.connectivity.api_2_cns_sce;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.services.connectivity.ConnectivityService;

/**
 * The method with the following header shall be compliant to its definition in the API.<br />
 * <code>void prepareAndSendConnectivityEvent() throws HCIException</code>
 */
public class Api_2_CNs_Sce_7 extends Applet {
    private static final byte INS_PREPARE_AND_SEND_CONN_EVT = 0x01;

    private ConnectivityService cnnService;

    public Api_2_CNs_Sce_7() {
        // JavaCard applet register
        register();

        // HCI listener register - connectivity
        try {
            cnnService = (ConnectivityService)HCIDevice.getHCIService(HCIDevice.CONNECTIVITY_SERVICE_ID);
        } catch (HCIException e) {
            ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        }
    }

    public static void install(byte bArray[], short bOffset, byte bLength)
        throws ISOException {
        new Api_2_CNs_Sce_7();
    }

    public void process(APDU apdu)
        throws ISOException {
        if (selectingApplet()) {
            return;
        }

        /*
		 * analyze incoming data
		 */

        byte buffer[] = apdu.getBuffer();

        switch (buffer[ISO7816.OFFSET_INS]) {
            case INS_PREPARE_AND_SEND_CONN_EVT:
                // call the method; we expect this to be successful
                try {
                    cnnService.prepareAndSendConnectivityEvent();
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