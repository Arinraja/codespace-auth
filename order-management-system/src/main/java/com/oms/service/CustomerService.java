package com.oms.service;

import com.oms.dto.CustomerRequest;
import com.oms.dto.CustomerResponse;
import com.oms.entity.Customer;
import com.oms.exception.BadRequestException;
import com.oms.exception.ResourceNotFoundException;
import com.oms.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Customer already exists with email: " + request.getEmail());
        }

        Customer customer = new Customer(request.getName(), request.getEmail(), request.getPhone());
        Customer saved = customerRepository.save(customer);
        log.info("Created customer id={} email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long id) {
        return toResponse(findCustomerOrThrow(id));
    }

    public Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone());
    }
}
