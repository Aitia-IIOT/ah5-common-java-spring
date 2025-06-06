package eu.arrowhead.common.http.filter.authentication;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.security.SecurityUtilities;
import eu.arrowhead.common.service.normalization.NormalizationMode;
import eu.arrowhead.common.service.validation.cloud.CloudIdentifierNormalizer;
import eu.arrowhead.common.service.validation.name.SystemNameNormalizer;

public class CertificateFilterTest {

	//=================================================================================================
	// members

	private final CertificateFilter filter = new CertificateFilterTestHelper(); // this is the trick

	private SystemNameNormalizer systemNameNormalizer;
	private CloudIdentifierNormalizer cloudIdentifierNormalizer;
	private ApplicationContext appContext;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalMissingCert() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/test/");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Unauthenticated access attempt: http://localhost/test", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalInvalidCertType() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("device.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "device.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		request.setRequestURI("/test/");
		final Throwable ex = assertThrows(ForbiddenException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Unauthorized access: http://localhost/test, invalid certificate type: DEVICE", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalNotInTheCloud() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		request.setRequestURI("/test/");

		ReflectionTestUtils.setField(filter, "appContext", appContext);
		final Map<String, Object> context = Map.of(Constants.SERVER_COMMON_NAME, "ServiceRegistry.TestCloud.Aitia.arrowhead.eu");
		ReflectionTestUtils.setField(filter, "arrowheadContext", context);

		final Throwable ex = assertThrows(ForbiddenException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Unauthorized access: http://localhost/test, from foreign cloud", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalOkSystem() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		request.setRequestURI("/test/");

		ReflectionTestUtils.setField(filter, "appContext", appContext);
		ReflectionTestUtils.setField(filter, "systemNameNormalizer", systemNameNormalizer);
		final Map<String, Object> context = Map.of(Constants.SERVER_COMMON_NAME, "ServiceRegistry.Rubin.Aitia.arrowhead.eu");
		ReflectionTestUtils.setField(filter, "arrowheadContext", context);

		final Throwable ex = assertThrows(RuntimeException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));

		assertAll("doFilterInternal - ok (system)",
				() -> assertEquals("OK", ex.getMessage()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM)),
				() -> assertEquals("Test", request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)),
				() -> assertFalse((boolean) request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalOkSysOp() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("management.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "management.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		request.setRequestURI("/test/");

		ReflectionTestUtils.setField(filter, "appContext", appContext);
		ReflectionTestUtils.setField(filter, "systemNameNormalizer", systemNameNormalizer);
		final Map<String, Object> context = Map.of(Constants.SERVER_COMMON_NAME, "ServiceRegistry.Rubin.Aitia.arrowhead.eu");
		ReflectionTestUtils.setField(filter, "arrowheadContext", context);

		final Throwable ex = assertThrows(RuntimeException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));

		assertAll("doFilterInternal - ok (sysop)",
				() -> assertEquals("OK", ex.getMessage()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM)),
				() -> assertEquals("Management", request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)),
				() -> assertTrue((boolean) request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)));
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@BeforeEach
	private void init() {
		if (systemNameNormalizer == null) {
			systemNameNormalizer = new SystemNameNormalizer();
			ReflectionTestUtils.setField(systemNameNormalizer, "normalizationMode", NormalizationMode.EXTENDED);
		}

		if (cloudIdentifierNormalizer == null) {
			cloudIdentifierNormalizer = new CloudIdentifierNormalizer();
			ReflectionTestUtils.setField(cloudIdentifierNormalizer, "systemNameNormalizer", systemNameNormalizer);
		}

		initAppContext();
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("checkstyle:MethodLength")
	private void initAppContext() {
		if (appContext == null) {
			appContext = new ApplicationContext() {
				@SuppressWarnings("unchecked")
				@Override
				public <T> T getBean(final Class<T> requiredType) throws BeansException {
					if (requiredType.equals(CloudIdentifierNormalizer.class)) {
						return (T) cloudIdentifierNormalizer;
					}
					return null;
				}

				@Override
				public <T> T getBean(final Class<T> requiredType, final Object... args) throws BeansException {
					return null;
				}

				@Override
				public Environment getEnvironment() {
					return null;
				}

				@Override
				public boolean containsBeanDefinition(final String beanName) {
					return false;
				}

				@Override
				public int getBeanDefinitionCount() {
					return 0;
				}

				@Override
				public String[] getBeanDefinitionNames() {
					return null;
				}

				@Override
				public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType, final boolean allowEagerInit) {
					return null;
				}

				@Override
				public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType, final boolean allowEagerInit) {
					return null;
				}

				@Override
				public String[] getBeanNamesForType(final ResolvableType type) {
					return null;
				}

				@Override
				public String[] getBeanNamesForType(final ResolvableType type, final boolean includeNonSingletons, final boolean allowEagerInit) {
					return null;
				}

				@Override
				public String[] getBeanNamesForType(final Class<?> type) {
					return null;
				}

				@Override
				public String[] getBeanNamesForType(final Class<?> type, final boolean includeNonSingletons, final boolean allowEagerInit) {
					return null;
				}

				@Override
				public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException {
					return null;
				}

				@Override
				public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons, final boolean allowEagerInit) throws BeansException {
					return null;
				}

				@Override
				public String[] getBeanNamesForAnnotation(final Class<? extends Annotation> annotationType) {
					return null;
				}

				@Override
				public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) throws BeansException {
					return null;
				}

				@Override
				public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType) throws NoSuchBeanDefinitionException {
					return null;
				}

				@Override
				public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType, final boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
					return null;
				}

				@Override
				public <A extends Annotation> Set<A> findAllAnnotationsOnBean(final String beanName, final Class<A> annotationType, final boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
					return null;
				}

				@Override
				public Object getBean(final String name) throws BeansException {
					return null;
				}

				@Override
				public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
					return null;
				}

				@Override
				public Object getBean(final String name, final Object... args) throws BeansException {
					return null;
				}

				@Override
				public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType) {
					return null;
				}

				@Override
				public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType) {
					return null;
				}

				@Override
				public boolean containsBean(final String name) {
					return false;
				}

				@Override
				public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
					return false;
				}

				@Override
				public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
					return false;
				}

				@Override
				public boolean isTypeMatch(final String name, final ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
					return false;
				}

				@Override
				public boolean isTypeMatch(final String name, final Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
					return false;
				}

				@Override
				public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
					return null;
				}

				@Override
				public Class<?> getType(final String name, final boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
					return null;
				}

				@Override
				public String[] getAliases(final String name) {
					return null;
				}

				@Override
				public BeanFactory getParentBeanFactory() {
					return null;
				}

				@Override
				public boolean containsLocalBean(final String name) {
					return false;
				}

				@Override
				public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
					return null;
				}

				@Override
				public String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException {
					return null;
				}

				@Override
				public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) throws NoSuchMessageException {
					return null;
				}

				@Override
				public void publishEvent(final Object event) {
				}

				@Override
				public Resource[] getResources(final String locationPattern) throws IOException {
					return null;
				}

				@Override
				public Resource getResource(final String location) {
					return null;
				}

				@Override
				public ClassLoader getClassLoader() {
					return null;
				}

				@Override
				public String getId() {
					return null;
				}

				@Override
				public String getApplicationName() {
					return null;
				}

				@Override
				public String getDisplayName() {
					return null;
				}

				@Override
				public long getStartupDate() {
					return 0;
				}

				@Override
				public ApplicationContext getParent() {
					return null;
				}

				@Override
				public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
					return null;
				}
			};
		}
	}

	//-------------------------------------------------------------------------------------------------
	private KeyStore initializeTestKeyStore(final String fileName) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keystore = KeyStore.getInstance("pkcs12");
		try (InputStream input = new FileInputStream(ResourceUtils.getFile("src/test/resources/certs/" + fileName))) {
			keystore.load(input, "123456".toCharArray());
		}

		return keystore;
	}
}