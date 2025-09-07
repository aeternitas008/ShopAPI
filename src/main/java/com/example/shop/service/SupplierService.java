package com.example.shop.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.shop.dto.AddressDTO;
import com.example.shop.dto.SupplierDTO;
import com.example.shop.exception.NotFoundException;
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
        Supplier supplier = findById(id);
        supplier.setAddress(addressMapper.toEntity(addressDTO));
        return supplierRepository.save(supplier);
    }

    // 3) Удаление поставщика
    public void deleteSupplier(UUID id) {
        existsById(id);
        supplierRepository.deleteById(id);
    }

    // 4) Получение всех поставщиков
    public List<Supplier> getAll(Integer limit, Integer offset) {
        Pageable pageable = (limit == null && offset == null)
                ? Pageable.unpaged()
                : (limit != null && offset != null)
                        ? PageRequest.of(offset, limit)
                        : null;

        if (pageable == null) {
            throw new IllegalArgumentException("Недостаточно параметров для пагинации");
        }
        return supplierRepository.findAll(pageable).getContent();
    }

    // 5) Получение поставщика по id
    public Supplier findById(UUID id) {
        return supplierRepository.findById(id).orElseThrow(
                () -> NotFoundException.forSupplier(id));
    }

    public Supplier updateSupplierAddress(UUID id, AddressDTO addressDTO) {
        Supplier supplier = findById(id);

        supplier.setAddress(addressMapper.toEntity(addressDTO));

        return supplierRepository.save(supplier);
    }

    public void existsById(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw NotFoundException.forSupplier(id);
        }
    }

}