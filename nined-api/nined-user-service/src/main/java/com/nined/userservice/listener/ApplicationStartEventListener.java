package com.nined.userservice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.nined.userservice.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * Class hosting methods which needs to be executed once Application ready event is fired
 * 
 * @author vijay
 *
 */
@Slf4j
@Component
public class ApplicationStartEventListener {

    @Autowired
    private UserService userService;


    /**
     * Method to create default provider admin user on server startup, if not exists
     */
    @EventListener(ApplicationReadyEvent.class)
    public void createSuperAdminUser() {
        if (log.isDebugEnabled()) {
            log.debug("Starting to create a provider admin user");
        }
        userService.registerProviderAdminUser();
    }
}
