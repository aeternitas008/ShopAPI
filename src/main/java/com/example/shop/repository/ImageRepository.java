package com.example.shop.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shop.model.Images;

@Repository
public interface ImageRepository extends JpaRepository<Images, UUID> {

}
