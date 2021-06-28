package uicc.hci.test.framework.api_1_hme_min;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import uicc.hci.framework.HCIDevice;
import uicc.hci.framework.HCIException;
import uicc.hci.framework.HCIListener;
import uicc.hci.framework.HCIMessage;
import uicc.hci.services.cardemulation.CardEmulationListener;
import uicc.hci.services.cardemulation.CardEmulationMessage;
import uicc.hci.services.cardemulation.CardEmulationService;

/**
 * The method with the following header shall be compliant to its definition in the API.<br />
 * <code>byte getInstruction()</code>
 */
public class Api_1_Hme_Min_2 extends Applet implements CardEmulationListener {


	/*
	 * Define HCI specific variables
	 */
	private CardEmulationService ceService;
	
    /**
     * byte 0 - HCIMessage.isHeading() result
     * byte 1 - HCIMessage.isComplete() result 
     * byte 2 - HCIMessage.getType() result
     * byte 3 - HCIMessage.getInstruction() result
     * byte 4, 5 - HCIMessage.getReceiveOffset() result
     * byte 6, 7 - HCIMessage.getReceiveLength() result
     */
	private byte[] exceptions; 
        
	private byte sentOnce; 

	/**
	 * Applet tests HCIMessage commands
	 */
	private Api_1_Hme_Min_2() {
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
	        ceService.activateEvent(EVENT_ON_SEND_DATA);
	        ceService.activateEvent(EVENT_GET_PARAMETER_RESPONSE);
		} catch (HCIException e){
			ISOException.throwIt(ISO7816.SW_DATA_INVALID); 
		}	
		
	    exceptions = new byte[10];
	    exceptions[3] = (byte) 0xFF; 
		sentOnce = 0x00;
	    
	}
	
	
	/**
	 * To create an instance of the <code>Applet</code> subclass, the Java Card runtime environment will call this static method first.
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte bArray[], short bOffset, byte bLength)
			throws ISOException {
		new Api_1_Hme_Min_2();
	}
	
	/**
	 * This method is called by the HCI framework to inform the Listener Object about a specific event and pass the corresponding HCIMessage to the Listener Object.
	 * @see HCIListener#onCallback(byte, HCIMessage)
	 */
	public void onCallback(byte event, HCIMessage hcimessage) {
		
		CardEmulationMessage message = (CardEmulationMessage) hcimessage;
		exceptions[8] = (byte) 0x90;
		exceptions[9] = 0;
		
		switch(event){
				
        case EVENT_ON_SEND_DATA:
        	if (sentOnce == 0x00){
        		message.prepareAndSendSendDataEvent(exceptions, (short)8, (short)2);
            	sentOnce = 0x01;
                return;
        	}
        	if (sentOnce == 0x01) { 
        		//message.prepareAndSendSendDataEvent(exceptions, (short)8, (short)2);
        		message.prepareAndSendGetParameterCommand(CardEmulationMessage.PARAM_ID_TYPE_A_CARD_ATQA);       	      
        	}
        	
        	if (sentOnce == 0x02) {
        		message.prepareAndSendSendDataEvent(exceptions, (short)0, (short)10);
        	}
            return;

        case EVENT_GET_PARAMETER_RESPONSE:

        	exceptions[3] = message.getInstruction();
        	sentOnce = 0x02;
        	message.prepareAndSendSendDataEvent(exceptions, (short)8, (short)2);
        	
        	//fall through
        default:
        	return;
		}
		
	}

	/**
	 * Not used.<br /> 
	 *  Called by the Java Card runtime environment to process an incoming APDU command.
	 *  @see Applet#process(APDU)
	 */
	public void process(APDU arg0) throws ISOException {
		
	}
}