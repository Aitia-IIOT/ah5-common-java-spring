package eu.arrowhead.common.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.service.validation.name.InterfaceTemplateNameNormalizer;
import eu.arrowhead.common.service.validation.name.ServiceDefinitionNameNormalizer;
import eu.arrowhead.common.service.validation.name.SystemNameNormalizer;

@ExtendWith(MockitoExtension.class)
public class ServiceCollectorTest {

	//=================================================================================================
	// members

	@InjectMocks
	private ServiceCollector collector;

	@Mock
	private ICollectorDriver driver;

	@Mock
	private ServiceDefinitionNameNormalizer serviceDefNameNormalizer;

	@Mock
	private InterfaceTemplateNameNormalizer interfaceTemplateNameNormalizer;

	@Mock
	private SystemNameNormalizer systemNameNormalizer;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testGetServiceModelServiceDefNull() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> collector.getServiceModel(null, "generic_http", "ProviderName"));

		assertEquals("service definition is empty", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testGetServiceModelServiceDefEmpty() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> collector.getServiceModel("", "generic_http", "ProviderName"));

		assertEquals("service definition is empty", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testGetServiceModelTemplateNameNull() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> collector.getServiceModel("testService", null, "ProviderName"));

		assertEquals("template name is empty", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testGetServiceModelTemplateNameEmpty() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> collector.getServiceModel("testService", "", "ProviderName"));

		assertEquals("template name is empty", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("checkstyle:MagicNumber")
	@Test
	public void testGetServiceModelContextAlreadyContains() {
		final ServiceModel serviceModel = new ServiceModel.Builder()
				.serviceDefinition("testService")
				.version("1.0.0")
				.serviceInterface(new HttpInterfaceModel.Builder("generic_http")
						.accessAddress("localhost")
						.accessPort(1234)
						.basePath("/test")
						.operation("test-operation", new HttpOperationModel("/op", "POST"))
						.build())
				.build();

		final Map<String, Object> context = new HashMap<>();
		context.put("service-model$$testService", serviceModel);
		ReflectionTestUtils.setField(collector, "arrowheadContext", context);

		when(serviceDefNameNormalizer.normalize("testService")).thenReturn("testService");

		final ServiceModel result = collector.getServiceModel("testService", "generic_http", "ProviderName");

		verify(serviceDefNameNormalizer).normalize("testService");
		verify(driver, never()).acquireService(anyString(), anyString(), anyString());

		assertEquals("testService", result.serviceDefinition());
		assertEquals("1.0.0", result.version());
		final HttpInterfaceModel interfaceModel = (HttpInterfaceModel) result.interfaces().get(0);
		assertEquals("localhost", interfaceModel.accessAddresses().get(0));
		assertEquals(1234, interfaceModel.accessPort());
		assertEquals("/test", interfaceModel.basePath());

	}
}