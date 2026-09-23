package com.oms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oms.dto.CustomerRequest;
import com.oms.dto.CustomerResponse;
import com.oms.entity.Customer;
import com.oms.exception.BadRequestException;
import com.oms.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_success() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Ravi");
        request.setEmail("ravi@example.com");
        request.setPhone("9999999999");

        when(customerRepository.existsByEmail("ravi@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CustomerResponse response = customerService.createCustomer(request);

        assertEquals(1L, response.getId());
        assertEquals("Ravi", response.getName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_duplicateEmail_throws() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Ravi");
        request.setEmail("ravi@example.com");

        when(customerRepository.existsByEmail("ravi@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> customerService.createCustomer(request));
    }
}
