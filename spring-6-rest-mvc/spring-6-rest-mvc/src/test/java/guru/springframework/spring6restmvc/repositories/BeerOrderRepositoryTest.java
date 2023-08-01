package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


//@DataJpaTest
@SpringBootTest
@ActiveProfiles("localmysql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BeerOrderRepositoryTest {


    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CustomerRepository customerRepository;

    Customer testCustomer;
    Beer testBeer;

    @BeforeEach
    void setUp(){
        testCustomer = customerRepository.findAll().get(0);
        testBeer = beerRepository.findAll().get(0);
    }

    @Transactional
    @Test
    void testBeerOrders(){
        BeerOrder beerOrder = BeerOrder.builder()
                .customerRef("Test order")
                .customer(testCustomer)
                .beerOrderShipment(BeerOrderShipment.builder()
                        .trackingNumber("135r")
                        .build())
                .build();

        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
        //BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);

        System.out.println(savedBeerOrder.getCustomerRef());
    }


}