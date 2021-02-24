package com.nined.userservice.service;

import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nined.userservice.model.JPALanguage;
import com.nined.userservice.repository.LanguageRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for providing all the required methods to interact with the Language
 * entity
 * 
 * @author vijay
 *
 */
@Slf4j
@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    /**
     * Method to find language entity for a given locale
     * 
     * @param locale
     * @return
     */
    public Optional<JPALanguage> findByLocale(String locale) {
        if (StringUtils.isBlank(locale)) {
            return Optional.empty();
        }
        if (log.isDebugEnabled()) {
            log.debug("Finding language entity for a given locale : [{}]", locale);
        }
        return languageRepository.findByLocale(locale);
    }

}
