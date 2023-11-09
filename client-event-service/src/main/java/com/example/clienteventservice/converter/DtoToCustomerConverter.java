package com.example.clienteventservice.converter;


import com.example.clienteventservice.domain.dto.CustomerDto;
import com.example.clienteventservice.domain.model.Customer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * CustomerDto to Customer converter
 */
@Component
public class DtoToCustomerConverter implements Converter<CustomerDto, Customer> {

    @Override
    public Customer convert(CustomerDto customerDto) {

        return Customer.builder()
                .firstName(customerDto.getFirstName())
                .lastName(customerDto.getLastName())
                .username(customerDto.getUsername())
                .email(customerDto.getEmail())
                .phoneNumber(customerDto.getPhoneNumber())
                .build();
    }

}
