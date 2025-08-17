package com.example.shop.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shop.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findByNameAndSurname(String name, String surname);

}