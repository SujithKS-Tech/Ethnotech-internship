package com.example.productapi.service;

import com.example.productapi.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class ProductService {

    public List<Product> getProducts() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream inputStream =
                    new ClassPathResource("products.json").getInputStream();

            return mapper.readValue(inputStream,
                    new TypeReference<List<Product>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}