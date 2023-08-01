package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@AllArgsConstructor
@RestController()
//@RequestMapping("/api/v1/customer")
public class CustomerController {

    public static final String CUSTOMER_PATH = "/api/v1/customer";
    public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    private  final CustomerService customerService;

    @PatchMapping(CUSTOMER_PATH_ID)
    public ResponseEntity updateCustomerPatchById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer){

        customerService.updatePatchCustomerById(customerId, customer);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity deleteCustomer(@PathVariable("customerId") UUID customerId){

        customerService.deteleCustomerById(customerId);
        return  new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity updateCustomer(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer){

        customerService.updateCustomerById(customerId,customer);
        return  new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    //@RequestMapping(method = RequestMethod.POST)
    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity handleCustomer(@RequestBody CustomerDTO customer){

        CustomerDTO savedCustomer = customerService.saveNewCustomer(customer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location",CUSTOMER_PATH + "/"+savedCustomer.getId().toString());
        return new ResponseEntity(httpHeaders,HttpStatus.CREATED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(){

        System.out.println("In exception handler");

        return ResponseEntity.notFound().build();
    }

   // @RequestMapping(method = RequestMethod.GET)
   @GetMapping(value = CUSTOMER_PATH)
    public List<CustomerDTO> listCustomers(){
        return customerService.listCustomers();
    }

    //@RequestMapping(value = "{idCustomer}", method = RequestMethod.GET)
    @GetMapping(value = CUSTOMER_PATH_ID)
    public CustomerDTO getCustomerById(@PathVariable("customerId") UUID customerId){
        return customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new);
    }

}
