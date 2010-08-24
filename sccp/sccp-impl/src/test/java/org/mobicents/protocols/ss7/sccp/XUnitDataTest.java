/**
 * 
 */
package org.mobicents.protocols.ss7.sccp;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;

import java.util.Arrays;

import org.mobicents.protocols.ss7.sccp.impl.parameter.GT0100;
import org.mobicents.protocols.ss7.sccp.impl.parameter.ImportanceImpl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.ProtocolClassImpl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.SegmentationImpl;
import org.mobicents.protocols.ss7.sccp.impl.ud.XUnitDataImpl;
import org.mobicents.protocols.ss7.sccp.parameter.GlobalTitle;
import org.mobicents.protocols.ss7.sccp.parameter.Importance;
import org.mobicents.protocols.ss7.sccp.parameter.ProtocolClass;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.sccp.parameter.Segmentation;
import org.mobicents.protocols.ss7.sccp.ud.XUnitData;


import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.protocols.ss7.indicator.NatureOfAddress;
import org.mobicents.protocols.ss7.indicator.NumberingPlan;
import org.mobicents.protocols.ss7.sccp.impl.parameter.AddressFactoryImpl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.GTFactory;
import static org.junit.Assert.*;

/**
 * @author baranowb
 *
 */
public class XUnitDataTest {
    private AddressFactoryImpl factory = new AddressFactoryImpl();
    
    private static final GlobalTitle _CALLING_PARTY_GLOBAL_TITLE = GTFactory.getInstance(0, NumberingPlan.ISDN_TELEPHONY, NatureOfAddress.NATIONAL, "1111111");//new GT0100(0, 1, 1, 1, "1111111");
    private static SccpAddress _CALLING_PARTY = null;//new SccpAddressImpl(1, 1, 4, 1, 1234, 1, _CALLING_PARTY_GLOBAL_TITLE);
    private static final GlobalTitle _CALLED_PARTY_GLOBAL_TITLE = GTFactory.getInstance(0, NumberingPlan.ISDN_TELEPHONY, NatureOfAddress.NATIONAL, "22222222");//new GT0100(1, 1, 4, 2, "22222222");
    private static SccpAddress _CALLED_PARTY = null;//new SccpAddressImpl(1, 1, 4, 1, 10922, 2, _CALLED_PARTY_GLOBAL_TITLE);
    private static final byte[] _DATA = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
    private static final byte[] _SEGMENTATION_REF = new byte[]{10, 11, 12, 45};
    private static final Segmentation _SEGMENTATION = new SegmentationImpl(true, false, (byte) 1, _SEGMENTATION_REF);
    private static final Importance _IMPORTANCE = new ImportanceImpl((byte) 4);
    private static final byte _HOPE_COUTNER = 14;
    private static final ProtocolClass _PROTOCOL_CLASS = new ProtocolClassImpl();
    

    static {
        _PROTOCOL_CLASS.setMsgHandling(1);
        _PROTOCOL_CLASS.setpClass(1);
    }

    @Before
    public void setUp() {
        _CALLING_PARTY = factory.getAddress(_CALLING_PARTY_GLOBAL_TITLE, 0);
        _CALLED_PARTY = factory.getAddress(_CALLED_PARTY_GLOBAL_TITLE, 0);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEncodeDecode() throws Exception {


        XUnitData testObject = new XUnitDataImpl(_HOPE_COUTNER, _PROTOCOL_CLASS, _CALLED_PARTY, _CALLING_PARTY, _DATA);
        ByteArrayOutputStream bas = new ByteArrayOutputStream(1);
        testObject.encode(bas);
        byte[] encoded = bas.toByteArray();
        assertEquals("MT indicator must has wrong value.", XUnitData._MT, encoded[0]);

        //we loose MT. 
        byte[] toDecode = new byte[encoded.length - 1];
        System.arraycopy(encoded, 1, toDecode, 0, toDecode.length);
        XUnitData testObjectDecoded = new XUnitDataImpl();
        testObjectDecoded.decode(new ByteArrayInputStream(toDecode));

        assertEquals("Protocol Class does not equal.", _PROTOCOL_CLASS, testObjectDecoded.getpClass());
        assertEquals("Called Party does not equal.", _CALLED_PARTY.getGlobalTitle().getDigits(), testObjectDecoded.getCalledParty().getGlobalTitle().getDigits());
        assertEquals("Calling Party does not equal.", _CALLING_PARTY.getGlobalTitle().getDigits(), testObjectDecoded.getCallingParty().getGlobalTitle().getDigits());
        //assertEquals("Data does not equal.",_DATA, testObjectDecoded.getData());
        assertTrue(Arrays.equals(_DATA, testObjectDecoded.getData()));
    //There is no optional param here.

    }

    public void testEncodeDecodeWithOneOptional() throws Exception {


        XUnitData testObject = new XUnitDataImpl(_HOPE_COUTNER, _PROTOCOL_CLASS, _CALLED_PARTY, _CALLING_PARTY, _DATA);
        testObject.setImportance(_IMPORTANCE);
        ByteArrayOutputStream bas = new ByteArrayOutputStream(1);
        testObject.encode(bas);
        byte[] encoded = bas.toByteArray();
        assertEquals("MT indicator must has wrong value.", XUnitData._MT, encoded[0]);

        //we loose MT. 
        byte[] toDecode = new byte[encoded.length - 1];
        System.arraycopy(encoded, 1, toDecode, 0, toDecode.length);
        XUnitData testObjectDecoded = new XUnitDataImpl();

        testObjectDecoded.decode(new ByteArrayInputStream(toDecode));

        assertEquals("Protocol Class does not equal.", _PROTOCOL_CLASS, testObjectDecoded.getpClass());
        assertEquals("Called Party does not equal.", _CALLED_PARTY, testObjectDecoded.getCalledParty());
        assertEquals("Calling Party does not equal.", _CALLING_PARTY, testObjectDecoded.getCallingParty());
        assertTrue(Arrays.equals(_DATA, testObjectDecoded.getData()));
        assertNotNull("Importance must not be null", testObjectDecoded.getImportance());
        assertEquals("Importance does not match. ", _IMPORTANCE, testObjectDecoded.getImportance());
    }
//	
    public void testEncodeDecodeWithBothOptional() throws Exception {
        XUnitData testObject = new XUnitDataImpl(_HOPE_COUTNER, _PROTOCOL_CLASS, _CALLED_PARTY, _CALLING_PARTY, _DATA);
        testObject.setImportance(_IMPORTANCE);
        testObject.setSegmentation(_SEGMENTATION);
        ByteArrayOutputStream bas = new ByteArrayOutputStream(1);
        testObject.encode(bas);
        byte[] encoded = bas.toByteArray();
        assertEquals("MT indicator must has wrong value.", XUnitData._MT, encoded[0]);

        //we loose MT. 
        byte[] toDecode = new byte[encoded.length - 1];
        System.arraycopy(encoded, 1, toDecode, 0, toDecode.length);
        XUnitData testObjectDecoded = new XUnitDataImpl();

        testObjectDecoded.decode(new ByteArrayInputStream(toDecode));

        assertEquals("Protocol Class does not equal.", _PROTOCOL_CLASS, testObjectDecoded.getpClass());
        assertEquals("Called Party does not equal.", _CALLED_PARTY, testObjectDecoded.getCalledParty());
        assertEquals("Calling Party does not equal.", _CALLING_PARTY, testObjectDecoded.getCallingParty());
        assertTrue(Arrays.equals(_DATA, testObjectDecoded.getData()));
        assertNotNull("Importance must not be null", testObjectDecoded.getImportance());
        assertEquals("Importance does not match. ", _IMPORTANCE, testObjectDecoded.getImportance());
        assertNotNull("Segmentation must not be null", testObjectDecoded.getSegmentation());
        assertEquals("Segmentation does not match. ", _SEGMENTATION, testObjectDecoded.getSegmentation());

    }

    public void testRealDecode() throws Exception {


        //This is real msg dump :)
        byte[] b = new byte[]{(byte) 0x01, (byte) 0x0f, (byte) 0x04, (byte) 0x0f, (byte) 0x1a, (byte) 0xac, (byte) 0x0b, (byte) 0x12, (byte) 0x92, (byte) 0x00, (byte) 0x11, (byte) 0x04, (byte) 0x97, (byte) 0x20, (byte) 0x73, (byte) 0x00, (byte) 0x72, (byte) 0x01, (byte) 0x0b, (byte) 0x12, (byte) 0x92, (byte) 0x00, (byte) 0x11, (byte) 0x04, (byte) 0x97, (byte) 0x20, (byte) 0x73, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x92, (byte) 0x62, (byte) 0x81, (byte) 0x8f, (byte) 0x48, (byte) 0x04, (byte) 0x22, (byte) 0x00, (byte) 0x01, (byte) 0x04, (byte) 0x6b, (byte) 0x1e, (byte) 0x28, (byte) 0x1c, (byte) 0x06, (byte) 0x07, (byte) 0x00, (byte) 0x11, (byte) 0x86, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0xa0, (byte) 0x11, (byte) 0x60, (byte) 0x0f, (byte) 0x80, (byte) 0x02, (byte) 0x07, (byte) 0x80, (byte) 0xa1, (byte) 0x09, (byte) 0x06, (byte) 0x07, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x32, (byte) 0x01, (byte) 0x6c, (byte) 0x80, (byte) 0xa1, (byte) 0x63, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x30, (byte) 0x5b, (byte) 0x80, (byte) 0x01, (byte) 0x08, (byte) 0x82, (byte) 0x08, (byte) 0x84, (byte) 0x90, (byte) 0x97, (byte) 0x20, (byte) 0x83, (byte) 0x20, (byte) 0x68, (byte) 0x06, (byte) 0x83, (byte) 0x07, (byte) 0x03, (byte) 0x13, (byte) 0x09, (byte) 0x32, (byte) 0x26, (byte) 0x59, (byte) 0x18, (byte) 0x85, (byte) 0x01, (byte) 0x0a, (byte) 0x8a, (byte) 0x08, (byte) 0x84, (byte) 0x93, (byte) 0x97, (byte) 0x20, (byte) 0x73, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0xbb, (byte) 0x05, (byte) 0x80, (byte) 0x03, (byte) 0x80, (byte) 0x90, (byte) 0xa3, (byte) 0x9c, (byte) 0x01, (byte) 0x0c, (byte) 0x9f, (byte) 0x32, (byte) 0x08, (byte) 0x52, (byte) 0x00, (byte) 0x07, (byte) 0x32, (byte) 0x01, (byte) 0x56, (byte) 0x04, (byte) 0xf2, (byte) 0xbf, (byte) 0x35, (byte) 0x03, (byte) 0x83, (byte) 0x01, (byte) 0x11, (byte) 0x9f, (byte) 0x36, (byte) 0x05, (byte) 0x99, (byte) 0x88, (byte) 0x1d, (byte) 0x00, (byte) 0x01, (byte) 0x9f, (byte) 0x37, (byte) 0x07, (byte) 0x91, (byte) 0x97, (byte) 0x20, (byte) 0x73, (byte) 0x00, (byte) 0x02, (byte) 0xf1, (byte) 0x9f, (byte) 0x39, (byte) 0x08, (byte) 0x02, (byte) 0x90, (byte) 0x11, (byte) 0x82, (byte) 0x02, (byte) 0x15, (byte) 0x23, (byte) 0x61, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x01, (byte) 0x06, (byte) 0x00, (byte) 0x21, (byte) 0x7c};

        XUnitData x = new XUnitDataImpl();
        x.decode(new ByteArrayInputStream(b));
    }
}
