package eu.arrowhead.common.init;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

import javax.naming.ConfigurationException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.collector.ServiceCollector;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.http.ArrowheadHttpService;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.mqtt.MqttController;
import eu.arrowhead.common.security.SecurityUtilities;

@SuppressWarnings("checkstyle:MagicNumber")
@ExtendWith(MockitoExtension.class)
public class ApplicationInitListenerTest {

	//=================================================================================================
	// members

	@InjectMocks
	private TestApplicationInitListener listener;

	@Mock
	private SystemInfo sysInfo;

	@Mock
	private ServiceCollector serviceCollector;

	@Mock
	private ArrowheadHttpService arrowheadHttpService;

	@Mock
	private MqttController mqttController;

	@Mock
	private Helper helper;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventInvalidServerConfiguration() {
		when(sysInfo.getSystemName()).thenReturn("TestProvider");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(false);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.CERTIFICATE);

		final Throwable ex = assertThrows(ConfigurationException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(2)).isSslEnabled();
		verify(sysInfo, times(2)).getAuthenticationPolicy();

		assertEquals("Authentication policy cannot be 'CERTIFICATE' while SSL is disabled", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventKeystoreTypeEmpty() {
		ReflectionTestUtils.setField(listener, "arrowheadContext", Map.of("server.standalone.mode", true));
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);

		when(sysInfo.getSystemName()).thenReturn("TestProvider");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("");

		assertFalse((boolean) ReflectionTestUtils.getField(listener, "standaloneMode"));

		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo).getAuthenticationPolicy();
		verify(sysInfo).getSslProperties();
		verify(propsMock).getKeyStoreType();

		assertTrue((boolean) ReflectionTestUtils.getField(listener, "standaloneMode"));
		assertEquals("server.ssl.key-store-type is not defined", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventKeystoreNull() {
		ReflectionTestUtils.setField(listener, "arrowheadContext", Map.of());
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);

		when(sysInfo.getSystemName()).thenReturn("TestProvider");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(null);

		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo).getAuthenticationPolicy();
		verify(sysInfo, times(2)).getSslProperties();
		verify(propsMock).getKeyStoreType();
		verify(propsMock).getKeyStore();

		assertEquals("server.ssl.key-store is not defined", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventKeystoreNotExists() {
		ReflectionTestUtils.setField(listener, "arrowheadContext", Map.of());
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystoreMock = Mockito.mock(Resource.class);

		when(sysInfo.getSystemName()).thenReturn("TestProvider");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystoreMock);
		when(keystoreMock.exists()).thenReturn(false);

		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo).getAuthenticationPolicy();
		verify(sysInfo, times(3)).getSslProperties();
		verify(propsMock).getKeyStoreType();
		verify(propsMock, times(2)).getKeyStore();
		verify(keystoreMock).exists();

		assertEquals("server.ssl.key-store file is not found", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventKeystorePasswordNull() {
		ReflectionTestUtils.setField(listener, "arrowheadContext", Map.of());
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystoreMock = Mockito.mock(Resource.class);

		when(sysInfo.getSystemName()).thenReturn("TestProvider");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystoreMock);
		when(keystoreMock.exists()).thenReturn(true);
		when(propsMock.getKeyStorePassword()).thenReturn(null);

		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo).getAuthenticationPolicy();
		verify(sysInfo, times(4)).getSslProperties();
		verify(propsMock).getKeyStoreType();
		verify(propsMock, times(2)).getKeyStore();
		verify(keystoreMock).exists();
		verify(propsMock).getKeyStorePassword();

		assertEquals("server.ssl.key-store-password is not defined", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventKeyAliasEmpty() {
		ReflectionTestUtils.setField(listener, "arrowheadContext", Map.of());
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystoreMock = Mockito.mock(Resource.class);

		when(sysInfo.getSystemName()).thenReturn("TestProvider");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystoreMock);
		when(keystoreMock.exists()).thenReturn(true);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("");

		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo).getAuthenticationPolicy();
		verify(sysInfo, times(5)).getSslProperties();
		verify(propsMock).getKeyStoreType();
		verify(propsMock, times(2)).getKeyStore();
		verify(keystoreMock).exists();
		verify(propsMock).getKeyStorePassword();
		verify(propsMock).getKeyAlias();

		assertEquals("server.ssl.key-alias is not defined", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventKeyPasswordNull() {
		ReflectionTestUtils.setField(listener, "arrowheadContext", Map.of());
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystoreMock = Mockito.mock(Resource.class);

		when(sysInfo.getSystemName()).thenReturn("TestProvider");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystoreMock);
		when(keystoreMock.exists()).thenReturn(true);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("alias");
		when(propsMock.getKeyPassword()).thenReturn(null);

		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo).getAuthenticationPolicy();
		verify(sysInfo, times(6)).getSslProperties();
		verify(propsMock).getKeyStoreType();
		verify(propsMock, times(2)).getKeyStore();
		verify(keystoreMock).exists();
		verify(propsMock).getKeyStorePassword();
		verify(propsMock).getKeyAlias();
		verify(propsMock).getKeyPassword();

		assertEquals("server.ssl.key-password is not defined", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventServerCertificateNotFound() {
		ReflectionTestUtils.setField(listener, "arrowheadContext", Map.of());
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystore = new ClassPathResource("certs/ConsumerAuthorization.p12");

		when(sysInfo.getSystemName()).thenReturn("ConsumerAuthorization");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystore);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("alias");
		when(propsMock.getKeyPassword()).thenReturn("123456");

		final Throwable ex = assertThrows(ServiceConfigurationError.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo).getAuthenticationPolicy();
		verify(sysInfo, times(10)).getSslProperties();
		verify(propsMock, times(2)).getKeyStoreType();
		verify(propsMock, times(3)).getKeyStore();
		verify(propsMock, times(2)).getKeyStorePassword();
		verify(propsMock, times(2)).getKeyAlias();
		verify(propsMock).getKeyPassword();

		assertEquals("Cannot find server certificate in the specified key store", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventServerPrivateKeyNotAccessible() {
		final Map<Object, Object> context = new HashMap<>();
		ReflectionTestUtils.setField(listener, "arrowheadContext", context);
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystore = new ClassPathResource("certs/ConsumerAuthorization.p12");

		when(sysInfo.getSystemName()).thenReturn("ConsumerAuthorization");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystore);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("ConsumerAuthorization.TestCloud.Company.arrowhead.eu");
		when(propsMock.getKeyPassword()).thenReturn("123456");

		assertTrue(context.isEmpty());

		try (MockedStatic<SecurityUtilities> secUtilMock = Mockito.mockStatic(SecurityUtilities.class)) {
			secUtilMock.when(() -> SecurityUtilities.getCertificateFromKeyStore(any(KeyStore.class), eq("ConsumerAuthorization.TestCloud.Company.arrowhead.eu"))).thenCallRealMethod();
			secUtilMock.when(() -> SecurityUtilities.getPrivateKeyFromKeyStore(any(KeyStore.class), eq("ConsumerAuthorization.TestCloud.Company.arrowhead.eu"), eq("123456"))).thenReturn(null);

			final Throwable ex = assertThrows(ServiceConfigurationError.class,
					() -> listener.onApplicationEvent(null));

			assertEquals(2, context.size());
			assertTrue(context.containsKey("server.certificate"));
			assertTrue(context.containsKey("server.public.key"));

			verify(sysInfo).getSystemName();
			verify(sysInfo).getServerPort();
			verify(sysInfo, times(3)).isSslEnabled();
			verify(sysInfo).getAuthenticationPolicy();
			verify(sysInfo, times(12)).getSslProperties();
			verify(propsMock, times(2)).getKeyStoreType();
			verify(propsMock, times(3)).getKeyStore();
			verify(propsMock, times(2)).getKeyStorePassword();
			verify(propsMock, times(3)).getKeyAlias();
			verify(propsMock, times(2)).getKeyPassword();
			secUtilMock.verify(() -> SecurityUtilities.getCertificateFromKeyStore(any(KeyStore.class), eq("ConsumerAuthorization.TestCloud.Company.arrowhead.eu")));
			secUtilMock.verify(() -> SecurityUtilities.getPrivateKeyFromKeyStore(any(KeyStore.class), eq("ConsumerAuthorization.TestCloud.Company.arrowhead.eu"), eq("123456")));

			assertEquals("Cannot find private key in the specified key store", ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventNotArrowheadServerCertificate() {
		final Map<Object, Object> context = new HashMap<>();
		ReflectionTestUtils.setField(listener, "arrowheadContext", context);
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystore = new ClassPathResource("certs/wrong.p12");

		when(sysInfo.getSystemName()).thenReturn("wrong");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.CERTIFICATE);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystore);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("wrong.rubin.aitia.arrowhead.eu");
		when(propsMock.getKeyPassword()).thenReturn("123456");

		final Throwable ex = assertThrows(AuthException.class,
				() -> listener.onApplicationEvent(null));

		assertEquals(3, context.size());
		assertTrue(context.containsKey("server.private.key"));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo, times(2)).getAuthenticationPolicy();
		verify(sysInfo, times(12)).getSslProperties();
		verify(propsMock, times(2)).getKeyStoreType();
		verify(propsMock, times(3)).getKeyStore();
		verify(propsMock, times(2)).getKeyStorePassword();
		verify(propsMock, times(3)).getKeyAlias();
		verify(propsMock, times(2)).getKeyPassword();

		assertEquals("Server certificate is not compliant with the Arrowhead certificate structure, common name and profile type not found", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventNotSystemCertificate() {
		final Map<Object, Object> context = new HashMap<>();
		ReflectionTestUtils.setField(listener, "arrowheadContext", context);
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystore = new ClassPathResource("certs/testoperator.p12");

		when(sysInfo.getSystemName()).thenReturn("testoperator");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.CERTIFICATE);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystore);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("testoperator.testcloud.company.arrowhead.eu");
		when(propsMock.getKeyPassword()).thenReturn("123456");

		final Throwable ex = assertThrows(AuthException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo, times(2)).getAuthenticationPolicy();
		verify(sysInfo, times(12)).getSslProperties();
		verify(propsMock, times(2)).getKeyStoreType();
		verify(propsMock, times(3)).getKeyStore();
		verify(propsMock, times(2)).getKeyStorePassword();
		verify(propsMock, times(3)).getKeyAlias();
		verify(propsMock, times(2)).getKeyPassword();

		assertEquals("Server certificate is not compliant with the Arrowhead certificate structure, invalid profile type: OPERATOR", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventNotWrongCN() {
		final Map<Object, Object> context = new HashMap<>();
		ReflectionTestUtils.setField(listener, "arrowheadContext", context);
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystore = new ClassPathResource("certs/wrongcn.p12");

		when(sysInfo.getSystemName()).thenReturn("TestSystem");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.CERTIFICATE);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystore);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("TestSystem.Something.TestCloud.Company.arrowhead.eu");
		when(propsMock.getKeyPassword()).thenReturn("123456");

		final Throwable ex = assertThrows(AuthException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo, times(2)).getAuthenticationPolicy();
		verify(sysInfo, times(12)).getSslProperties();
		verify(propsMock, times(2)).getKeyStoreType();
		verify(propsMock, times(3)).getKeyStore();
		verify(propsMock, times(2)).getKeyStorePassword();
		verify(propsMock, times(3)).getKeyAlias();
		verify(propsMock, times(2)).getKeyPassword();

		assertEquals("Server CN (TestSystem.Something.TestCloud.Company.arrowhead.eu) is not compliant with the Arrowhead certificate structure, since it does not have 5 parts", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventStandAloneOk() {
		final Map<Object, Object> context = new HashMap<>();
		context.put("server.standalone.mode", true);

		ReflectionTestUtils.setField(listener, "arrowheadContext", context);
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystore = new ClassPathResource("certs/ConsumerAuthorization.p12");

		when(sysInfo.getSystemName()).thenReturn("ConsumerAuthorization");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.CERTIFICATE);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystore);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("ConsumerAuthorization.TestCloud.Company.arrowhead.eu");
		when(propsMock.getKeyPassword()).thenReturn("123456");
		when(sysInfo.isMqttApiEnabled()).thenReturn(false);
		doNothing().when(helper).customInitCheck();

		assertDoesNotThrow(() -> listener.onApplicationEvent(null));

		assertEquals(5, context.size());
		assertEquals("ConsumerAuthorization.TestCloud.Company.arrowhead.eu", context.get("server.common.name"));

		verify(sysInfo).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo, times(2)).getAuthenticationPolicy();
		verify(sysInfo, times(12)).getSslProperties();
		verify(propsMock, times(2)).getKeyStoreType();
		verify(propsMock, times(3)).getKeyStore();
		verify(propsMock, times(2)).getKeyStorePassword();
		verify(propsMock, times(3)).getKeyAlias();
		verify(propsMock, times(2)).getKeyPassword();
		verify(sysInfo).isMqttApiEnabled();
		verify(helper).customInitCheck();
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventServiceRegistryOk() {
		final Map<Object, Object> context = new HashMap<>();

		ReflectionTestUtils.setField(listener, "arrowheadContext", context);
		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystore = new ClassPathResource("certs/ServiceRegistry.p12");

		when(sysInfo.getSystemName()).thenReturn("ServiceRegistry");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystore);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("ServiceRegistry.TestCloud.Company.arrowhead.eu");
		when(propsMock.getKeyPassword()).thenReturn("123456");
		when(sysInfo.isMqttApiEnabled()).thenReturn(false);
		doNothing().when(helper).customInitCheck();

		assertDoesNotThrow(() -> listener.onApplicationEvent(null));

		verify(sysInfo, times(2)).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(3)).isSslEnabled();
		verify(sysInfo, times(2)).getAuthenticationPolicy();
		verify(sysInfo, times(12)).getSslProperties();
		verify(propsMock, times(2)).getKeyStoreType();
		verify(propsMock, times(3)).getKeyStore();
		verify(propsMock, times(2)).getKeyStorePassword();
		verify(propsMock, times(3)).getKeyAlias();
		verify(propsMock, times(2)).getKeyPassword();
		verify(sysInfo).isMqttApiEnabled();
		verify(helper).customInitCheck();
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventServiceRegistryForbidden() {
		final Map<Object, Object> context = new HashMap<>();
		ReflectionTestUtils.setField(listener, "arrowheadContext", context);

		when(sysInfo.getSystemName()).thenReturn("ConsumerAuthorization");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(false);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.OUTSOURCED);
		when(sysInfo.getAuthenticatorLoginDelay()).thenReturn(1L);
		when(serviceCollector.getServiceModel("systemDiscovery", "generic_http", "ServiceRegistry")).thenThrow(new ForbiddenException("test forbidden"));

		final Throwable ex = assertThrows(ForbiddenException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo, times(2)).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(4)).isSslEnabled();
		verify(sysInfo, times(3)).getAuthenticationPolicy();
		verify(sysInfo).getAuthenticatorLoginDelay();
		verify(serviceCollector).getServiceModel("systemDiscovery", "generic_http", "ServiceRegistry");

		assertEquals("test forbidden", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testOnApplicationEventServiceRegistryAuthException() {
		final Map<Object, Object> context = new HashMap<>();
		ReflectionTestUtils.setField(listener, "arrowheadContext", context);

		final SSLProperties propsMock = Mockito.mock(SSLProperties.class);
		final Resource keystore = new ClassPathResource("certs/ConsumerAuthorization.p12");

		when(sysInfo.getSystemName()).thenReturn("ConsumerAuthorization");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(true);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(sysInfo.getSslProperties()).thenReturn(propsMock);
		when(propsMock.getKeyStoreType()).thenReturn("pkcs12");
		when(propsMock.getKeyStore()).thenReturn(keystore);
		when(propsMock.getKeyStorePassword()).thenReturn("123456");
		when(propsMock.getKeyAlias()).thenReturn("ConsumerAuthorization.TestCloud.Company.arrowhead.eu");
		when(propsMock.getKeyPassword()).thenReturn("123456");
		when(serviceCollector.getServiceModel("systemDiscovery", "generic_https", "ServiceRegistry")).thenThrow(new AuthException("test auth"));

		final Throwable ex = assertThrows(AuthException.class,
				() -> listener.onApplicationEvent(null));

		verify(sysInfo, times(2)).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(4)).isSslEnabled();
		verify(sysInfo, times(3)).getAuthenticationPolicy();
		verify(sysInfo, times(12)).getSslProperties();
		verify(propsMock, times(2)).getKeyStoreType();
		verify(propsMock, times(3)).getKeyStore();
		verify(propsMock, times(2)).getKeyStorePassword();
		verify(propsMock, times(3)).getKeyAlias();
		verify(propsMock, times(2)).getKeyPassword();
		verify(sysInfo, never()).getAuthenticatorLoginDelay();
		verify(serviceCollector).getServiceModel("systemDiscovery", "generic_https", "ServiceRegistry");

		assertEquals("test auth", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	@Disabled // TODO: remove this
	public void testOnApplicationEventRegisterToServiceRegistryOk() {
		final Map<Object, Object> context = new HashMap<>();
		ReflectionTestUtils.setField(listener, "arrowheadContext", context);

		final ServiceModel systemDiscoverySM = new ServiceModel.Builder()
				.serviceDefinition("serviceDiscovery")
				.version("5.0.0")
				.serviceInterface(new HttpInterfaceModel.Builder("generic_http")
						.accessAddress("localhost")
						.accessPort(8443)
						.basePath("/serviceregistry/system-discovery")
						.operation("register", new HttpOperationModel("/register", "POST"))
						.build())
				.build();

		when(sysInfo.getSystemName()).thenReturn("ConsumerAuthorization");
		when(sysInfo.getServerPort()).thenReturn(12345);
		when(sysInfo.isSslEnabled()).thenReturn(false);
		when(sysInfo.getAuthenticationPolicy()).thenReturn(AuthenticationPolicy.DECLARED);
		when(serviceCollector.getServiceModel("systemDiscovery", "generic_http", "ServiceRegistry")).thenReturn(systemDiscoverySM);
		doNothing().when(arrowheadHttpService).consumeService("systemDiscovery", "revoke", "ServiceRegistry", Void.class);
		
		// TODO: continue from line 272

		assertDoesNotThrow(() -> listener.onApplicationEvent(null));

		verify(sysInfo, times(2)).getSystemName();
		verify(sysInfo).getServerPort();
		verify(sysInfo, times(4)).isSslEnabled();
		verify(sysInfo, times(3)).getAuthenticationPolicy();
		verify(serviceCollector).getServiceModel("systemDiscovery", "generic_http", "ServiceRegistry");
		verify(arrowheadHttpService).consumeService("systemDiscovery", "revoke", "ServiceRegistry", Void.class);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testCheckServiceRegistryConnectionConnectProblem() {
		when(serviceCollector.getServiceModel("systemDiscovery", "generic_http", "ServiceRegistry")).thenThrow(new ArrowheadException("connection problem"));

		final Throwable ex = assertThrows(ArrowheadException.class,
				() -> ReflectionTestUtils.invokeMethod(listener, "checkServiceRegistryConnection", false, 1, 1));

		verify(serviceCollector, times(2)).getServiceModel("systemDiscovery", "generic_http", "ServiceRegistry");

		assertEquals("connection problem", ex.getMessage());
	}

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	@Service
	private static final class Helper {

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public void customInitCheck() {
		};

		//-------------------------------------------------------------------------------------------------
		public void customDestroyCheck() {
		};
	}

	//-------------------------------------------------------------------------------------------------
	@Service
	private static final class TestApplicationInitListener extends ApplicationInitListener {

		//=================================================================================================
		// members

		@Autowired
		private Helper helper;

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		@Override
		protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
			super.customInit(event);
			helper.customInitCheck();
		}

		//-------------------------------------------------------------------------------------------------
		@Override
		protected void customDestroy() {
			helper.customDestroyCheck();
			super.customDestroy();
		}
	}
}