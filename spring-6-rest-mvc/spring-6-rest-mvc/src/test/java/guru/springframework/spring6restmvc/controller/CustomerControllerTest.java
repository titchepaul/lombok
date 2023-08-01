package guru.springframework.spring6restmvc.controller;


import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
Spring MockMVC allows you to test the controller interactions
 in a servlet context without the application running in a application server.
 */

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    CustomerServiceImpl customerServiceImpl;

    @Autowired
    MockMvc mockMvc ;

    @MockBean
    CustomerService customerService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerArgumentCaptor;


    @Test
    void getCustomerByIdNotFound() throws Exception{

        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    void testPatchCustomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.listCustomers().get(0);

        Map<String,Object> customerMap = new HashMap<>();
        customerMap.put("customerName","New Name");

        mockMvc.perform(patch(CustomerController.CUSTOMER_PATH + "/" + customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerMap)))
                .andExpect(status().isNoContent());

        verify(customerService).updatePatchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());
        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(customerMap.get("customerName")).isEqualTo(customerArgumentCaptor.getValue().getCustomerName());
    }

    @Test
    void testDeleteCustomer() throws Exception {

        CustomerDTO customer = customerServiceImpl.listCustomers().get(0);

        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH + "/" + customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService).deteleCustomerById(uuidArgumentCaptor.capture());
        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue()); //pas obligé
        //verify(customerService).deteleCustomerById(any(UUID.class));
    }

    @Test
    void testUpdateCustomer() throws Exception {

        CustomerDTO customer = customerServiceImpl.listCustomers().get(0);

        mockMvc.perform(put(CustomerController.CUSTOMER_PATH+ "/" + customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(any(UUID.class),any(CustomerDTO.class)); //check ce que j'attends en retour
    }

    @Test
    void testCreateCustomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.listCustomers().get(0);
        customer.setId(null);
        customer.setVersion(null);

        given(customerService.saveNewCustomer(any(CustomerDTO.class))).willReturn(customerServiceImpl.listCustomers().get(1));

        mockMvc.perform(post(CustomerController.CUSTOMER_PATH )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));


    }
    @Test
    public void testGetListCustomers() throws Exception {

        given(customerService.listCustomers()).willReturn(customerServiceImpl.listCustomers());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }
    @Test
    public void testGetCustomerById() throws Exception {

        CustomerDTO customer = customerServiceImpl.listCustomers().get(0);
        given(customerService.getCustomerById(customer.getId())).willReturn(Optional.of(customer));

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH+ "/" + customer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(customer.getId().toString())))
                .andExpect(jsonPath("$.customerName", is(customer.getCustomerName())))
                .andExpect(jsonPath("$.version",is(customer.getVersion())));
    }
}
