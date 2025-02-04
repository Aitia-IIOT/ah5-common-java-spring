package eu.arrowhead.common.service.validation.address;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AddressNormalizer.class)
public class AddressNormalizerTest {

	//=================================================================================================
	// members

	@Autowired
	private AddressNormalizer addressNormalizer;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void normalizeInvalidAddressTest() {
		// If the address is not MAC, IPv4, IPv6 or hybrid, the normalizer shouldn't change it (besides trim and toLowerCase).
		assertAll("Invalid address",
				// incorrect number of separators
				() -> assertEquals("a1:a1:a1:a1:a1a1", addressNormalizer.normalize("a1:a1:a1:a1:a1a1")),
				// mixed separators
				() -> assertEquals("a1-a1:a1.a1-a1:a1", addressNormalizer.normalize("a1-a1:a1.a1-a1:a1")),
				() -> assertEquals("128.0.0.11:a:a:a:a:a", addressNormalizer.normalize("128.0.0.11:a:a:a:a:a")),
				// no separator character
				() -> assertEquals("a1a1a1a1a1a1", addressNormalizer.normalize("\tA1A1A1A1A1A1\n")),
				// invalid characters
				() -> assertEquals("ä1:a1:a1:a1:a1:a1", addressNormalizer.normalize("ä1:a1:a1:a1:a1:a1")),
				// looks like IPv6, but contains more than one duplicated colons
				() -> assertEquals("2001::96b3::8a2e:0370:7cf4", addressNormalizer.normalize("2001::96b3::8a2e:0370:7cf4")));

	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void normalizeMACTest() {
		assertAll("MAC address",
				// raw address was uppercase and contained whitespace characters
				() -> assertEquals("ab:cd:ef:11:22:33", addressNormalizer.normalize(" \n\tAB:CD:EF:11:22:33 \r")),
				// change dashes to colons
				() -> assertEquals("ab:cd:ef:11:22:33", addressNormalizer.normalize(" \n\tAB-CD-EF-11-22-33 \r")));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void normalizeIPv4Test() {
		// remove whitespaces
		assertEquals("192.168.0.1", addressNormalizer.normalize(" \n\t192.168.0.1 \r"));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void normalizeIPv6Test() {
		assertAll("IPv6 address",
				// remove whitespaces, to lowercase
				() -> assertEquals("2001:11b8:96b3:0000:0000:8a2e:0370:7cf4", addressNormalizer.normalize("\n \t2001:11B8:96B3:0000:0000:8A2E:0370:7Cf4\r ")),
				// add leading zeros
				() -> assertEquals("2001:00b8:85a3:0000:0000:9b3f:0000:0004", addressNormalizer.normalize("2001:b8:85a3:0:0:9b3f:0:4")),
				// convert double colons
				() -> assertEquals("2001:00b8:85a3:0000:0000:9b3f:0000:0004", addressNormalizer.normalize("2001:00b8:85a3::9b3f:0000:0004")),
				// normalize loopback address
				() -> assertEquals("0000:0000:0000:0000:0000:0000:0000:0001", addressNormalizer.normalize("::1")),
				// normalize unspecified address
				() -> assertEquals("0000:0000:0000:0000:0000:0000:0000:0000", addressNormalizer.normalize("::")));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void normalizeIPv6IPv4HybridTest() {
		assertAll("IPv6-IPv4 hybrid address",
				// remove whitespaces, to lowercase, convert to IPv6
				() -> assertEquals("0000:0000:0000:0000:0000:ffff:c000:0280", addressNormalizer.normalize("\r \n::FFFF:192.0.2.128  ")));
	}
}
