package com.example.shop.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.shop.dto.AddressDTO;
import com.example.shop.dto.SupplierDTO;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.exception.SupplierNotFoundException;
import com.example.shop.mapper.AddressMapper;
import com.example.shop.mapper.SupplierMapper;
import com.example.shop.model.Supplier;
import com.example.shop.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final AddressMapper addressMapper;

    // 1) Добавление клиента
    public Supplier addSupplier(SupplierDTO supplierDto) {
        Supplier supplier = supplierMapper.toEntity(supplierDto);
        return supplierRepository.save(supplier);
    }

    // 2) Изменение адреса
    public Supplier updateAddress(UUID id, AddressDTO addressDTO) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow();
        supplier.setAddress(addressMapper.toEntity(addressDTO));
        return supplierRepository.save(supplier);
    }

    // 3) Удаление поставщика
    public void deleteSupplier(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw new SupplierNotFoundException(id);
        }
        supplierRepository.deleteById(id);
    }

    // 4) Получение всех поставщиков
    public List<Supplier> getAll(Integer limit, Integer offset) {
        if (limit != null && offset != null) {
            Pageable pageable = PageRequest.of(offset, limit);
            Page<Supplier> page = supplierRepository.findAll(pageable);
            return page.getContent();
        }
        // @TODO null ???
        return supplierRepository.findAll();
    }

    // 5) Получение поставщика по id
    public Supplier getById(UUID id) {
        return supplierRepository.findById(id).orElseThrow();
    }

    public Supplier updateSupplierAddress(UUID id, AddressDTO addressDTO) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow();

        supplier.setAddress(addressMapper.toEntity(addressDTO));

        return supplierRepository.save(supplier);
    }

    public Supplier getSupplierById(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> NotFoundException.forSupplier(id));
    }

    public void existsById(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw NotFoundException.forSupplier(id);
        }
    }

    public List<Supplier> getAllSuppliersValidated(Integer limit, Integer offset) {
        if (limit != null && limit <= 0) {
            throw new BadRequestException("Limit должен быть > 0");
        }
        if (offset != null && offset < 0) {
            throw new BadRequestException("Offset должен быть >= 0");
        }

        List<Supplier> suppliers = getAll(limit, offset);
        if (suppliers.isEmpty()) {
            throw new NotFoundException("Поставщики не найдены");
        }

        return suppliers;
    }
}