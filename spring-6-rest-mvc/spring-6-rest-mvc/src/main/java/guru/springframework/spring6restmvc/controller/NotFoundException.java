package guru.springframework.spring6restmvc.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Value Not Found")
public class NotFoundException extends  RuntimeException{

}
