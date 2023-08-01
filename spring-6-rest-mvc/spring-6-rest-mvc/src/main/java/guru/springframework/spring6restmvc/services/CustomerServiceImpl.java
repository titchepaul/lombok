package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, CustomerDTO> mapCustomer;

    public CustomerServiceImpl(){
        this.mapCustomer = new HashMap<>();

        CustomerDTO customer1 = CustomerDTO.builder()
                .customerName("Polo")
                .version(1)
                .id(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        CustomerDTO customer2 = CustomerDTO.builder()
                .customerName("Susan")
                .version(2)
                .id(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        CustomerDTO customer3 = CustomerDTO.builder()
                .customerName("Adolph")
                .version(3)
                .id(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        mapCustomer.put(customer1.getId(),customer1);
        mapCustomer.put(customer2.getId(),customer2);
        mapCustomer.put(customer3.getId(),customer3);
    }
    @Override
    public List<CustomerDTO> listCustomers() {
       return new ArrayList<>(mapCustomer.values());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
       log.debug("T'es dans la methode customerId "+ id.toString());
       return Optional.of(mapCustomer.get(id));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customer) {
        CustomerDTO savedCustomer = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName(customer.getCustomerName())
                .version(452)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        mapCustomer.put(savedCustomer.getId(),savedCustomer);
        return  savedCustomer;
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customer) {
        CustomerDTO existing = mapCustomer.get(customerId);
        existing.setCustomerName(customer.getCustomerName());
        return Optional.of(existing);
//        existing.setVersion(customer.getVersion());
//        existing.setCreatedDate(customer.getCreatedDate());
//        existing.setLastModifiedDate(customer.getLastModifiedDate());

        //mapCustomer.put(existing.getId(),existing);
    }

    @Override
    public Boolean deteleCustomerById(UUID customerId) {

        mapCustomer.remove(customerId);
        return  true;
    }

    @Override
    public Optional<CustomerDTO> updatePatchCustomerById(UUID customerId, CustomerDTO customer) {
        CustomerDTO existing = mapCustomer.get(customerId);

        if(StringUtils.hasText(customer.getCustomerName())){
            existing.setCustomerName(customer.getCustomerName());
        }
        return  Optional.of(existing);
    }
}
