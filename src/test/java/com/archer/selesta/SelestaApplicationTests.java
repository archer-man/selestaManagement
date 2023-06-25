package com.archer.selesta;

import com.archer.selestaManagement.CrudExampleApplication;
import com.archer.selestaManagement.controllers.ComponentController;
import com.archer.selestaManagement.entity.Component;
import com.archer.selestaManagement.repository.ComponentsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = CrudExampleApplication.class)
class SelestaApplicationTests {

	/*@Autowired
	private MockWebServiceClient client;*/

	@MockBean
	private ComponentsRepository compRepo;
	@MockBean
	ComponentController compContrl;

	@Test
	void givenXmlRequest_whenServiceInvoked_thenValidResponse() throws IOException {
		List <Component> list = new ArrayList<Component>();


		Component component = new Component( "Диод кремниевый", "LL4148", "0.15А", "100В", "SOD-80", "", BigDecimal.valueOf(5.0));
		when(compRepo.findById(1670)).thenReturn(Optional.of(component));
		list.add(component);
		when(compContrl.searchComponent(List.of("Диод кремниевый"))).thenReturn((List<Component>) component);

		StringSource request = new StringSource(
				"<bd:getProductRequest xmlns:bd='http://baeldung.com/spring-boot-web-service'>" +
						"<bd:id>1</bd:id>" +
						"</bd:getProductRequest>"
		);

		StringSource expectedResponse = new StringSource(
				"<bd:getProductResponse xmlns:bd='http://baeldung.com/spring-boot-web-service'>" +
						"<bd:product>" +
						"<bd:id>1</bd:id>" +
						"<bd:name>Product 1</bd:name>" +
						"</bd:product>" +
						"</bd:getProductResponse>"
		);

		/*client.sendRequest(withPayload(request))
				.andExpect(noFault())
				.andExpect(validPayload(new ClassPathResource("webservice/products.xsd")))
				.andExpect(payload(expectedResponse))
				.andExpect(xpath("/bd:getProductResponse/bd:product[1]/bd:name", NAMESPACE_MAPPING)
						.evaluatesTo("Product 1"));
	}*/
}
	@Test
	void contextLoads() {
	}

}
