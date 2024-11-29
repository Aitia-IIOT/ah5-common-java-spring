package eu.arrowhead.common.service.validation.address;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.dto.enums.AddressType;


@SpringBootTest(classes=AddressValidator.class)
public class AddressValidatorTest {
		
	//=================================================================================================
	// members

    @Mock
    private Environment environment;

    @Autowired
    @InjectMocks
    private AddressValidator addressValidator;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
        
    //=================================================================================================
    // methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void validateIPv4Test() {
		assertAll("IPv4 address",
				// valid IPv4
				() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "192.168.2.1");}),
				// invalid IPv4
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "192.168.2.a");}),
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "777.666.555.444");}),
				// IP placeholder
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "0.0.0.0");}),
				// broadcast address
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "255.255.255.255");}),
				// multicast address
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "225.0.0.0");}));
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void validateIPv6Test() {
		assertAll("IPv4 address",
				// valid IPv6
				() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "2001:11b8:96b3:0000:0000:8a2e:0370:7cf4");}),
				// invalid IPv6
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx");}),
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "192.168.2.a");}),
				// unspecified address
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "0000:0000:0000:0000:0000:0000:0000:0000");}),
				// multicast address
				() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "ff01:11b8:96b3:0000:0000:8a2e:0370:7cf4");})
				// anycast -> indistinguishable from other unicast addresses
				);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void validateMACTest() {
		//TODO
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void validateHostNameTest() {
		//TODO
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void detectTypeTest() {
		//TODO
	}
	
    //-------------------------------------------------------------------------------------------------
    @Test
    public void validateAddressSelfAddressingNotAllowedTest() {
    	
    	ReflectionTestUtils.setField(addressValidator, "allowSelfAddressing", false);
    	
    	    	
    	assertAll("don't allow self addressing",
    			// IPv4
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "192.168.2.1");}),
    			() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "127.0.0.1");}),
    			// IPv6
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "2c2c:0000:0000:0000:0000:0000:0000:0001");}),
    			() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "0000:0000:0000:0000:0000:0000:0000:0001");}));
    }
	
	
        
    //-------------------------------------------------------------------------------------------------
    @Test
    public void validateAddressSelfAddressingAllowedTest() {
    	
    	ReflectionTestUtils.setField(addressValidator, "allowSelfAddressing", true);
    	
    	assertAll("allow self addressing",
    			// IPv4
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "192.168.2.1");}),
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "127.0.0.1");}),
    			// IPv6
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "2c2c:0000:0000:0000:0000:0000:0000:0001");}),
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "0000:0000:0000:0000:0000:0000:0000:0001");}));
    }
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void validateAddressNonRoutableAddressingNotAllowedTest() {
		
		ReflectionTestUtils.setField(addressValidator, "allowNonRoutableAddressing", false);
		
    	assertAll("don't allow non routable addressing",
    			// IPv4
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "192.168.2.1");}),
    			() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "169.254.0.1");}),
    			// IPv6
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "2c2c:0000:0000:0000:0000:0000:0000:0001");}),
    			() -> assertThrows(InvalidParameterException.class, () -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "fe80:0000:0000:0000:0000:0000:0000:0001");}));
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void validateAddressNonRoutableAddressingAllowedTest() {
		
		ReflectionTestUtils.setField(addressValidator, "allowNonRoutableAddressing", true);
    	assertAll("allow non routable addressing",
    			// IPv4
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "192.168.2.1");}),
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV4, "169.254.0.1");}),
    			// IPv6
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "2c2c:0000:0000:0000:0000:0000:0000:0001");}),
    			() -> assertDoesNotThrow(() -> {addressValidator.validateNormalizedAddress(AddressType.IPV6, "fe80:0000:0000:0000:0000:0000:0000:0001");}));
	}

}
