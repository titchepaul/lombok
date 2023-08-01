package guru.springframework.spring6restmvc.controller;


import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("localmysql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;


    @Test
    void testCustomerIdNotFound(){
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomerById(UUID.randomUUID());
        });
    }

    @Test
    void testGetById(){
        Customer customer = customerRepository.findAll().get(0);

        CustomerDTO dto = customerController.getCustomerById(customer.getId());

        assertThat(dto).isNotNull();
    }

    @Test
    void testListCustomers(){
        List<CustomerDTO> dtos = customerController.listCustomers();

        assertThat(dtos.size()).isEqualTo(3);
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList(){
        customerRepository.deleteAll();
        List<CustomerDTO> dtos = customerController.listCustomers();

        assertThat(dtos.size()).isEqualTo(0);
    }

}