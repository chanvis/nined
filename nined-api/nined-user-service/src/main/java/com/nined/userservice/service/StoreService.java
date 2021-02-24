package com.nined.userservice.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nined.userservice.dto.StoreDto;
import com.nined.userservice.model.JPAStore;
import com.nined.userservice.repository.StoreRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for providing all the required methods to interact with the Store
 * entity
 * 
 * @author vijay
 *
 */
@Slf4j
@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Method to find Store entity for a given store id
     * @param paging 
     * 
     * @param storeId
     * @param active - used for checking entity active flag
     * @return
     */
    public Optional<JPAStore> findByStoreId(Long storeId, boolean active) {
        if (storeId == null) {
            return Optional.empty();
        }
        if (log.isInfoEnabled()) {
            log.info("Finding store entity for a given store id : [{}]", storeId);
        }
        Optional<JPAStore> jpaStoreOpt = storeRepository.findById(storeId);
        if (active && jpaStoreOpt.isPresent() && !jpaStoreOpt.get().isActive()) {
            return Optional.empty();
        }
        return jpaStoreOpt;
    }

    /**
     * Method to find client entity for a given client name
     * 
     * @param storeName
     * @param organization
     * @param active - used for checking entity active flag
     * @return
     */
    public Optional<JPAStore> findByStoreName(String storeName,
            boolean active) {
        if (log.isInfoEnabled()) {
            log.info("Finding client entity for given store name [{}]",
                    storeName);
        }
        Optional<JPAStore> jpaStoreOpt = storeRepository.findBy(storeName);
        if (active && jpaStoreOpt.isPresent() && !jpaStoreOpt.get().isActive()) {
            return Optional.empty();
        }
        return jpaStoreOpt;
    }

    /**
     * 
     * Method to convert DTO object to entity object
     * 
     * @param clientDto
     * @return
     */
    private JPAStore convertToEntity(StoreDto clientDto) {
        return modelMapper.map(clientDto, JPAStore.class);
    }

    /**
     * Method to convert entity object to DTO object
     * 
     * @param jpaStore
     * @return
     */
    private StoreDto convertToDTO(JPAStore jpaStore) {
        return modelMapper.map(jpaStore, StoreDto.class);
    }

}
