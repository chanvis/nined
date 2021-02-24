package com.nined.userservice.service;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.nined.userservice.constants.UserConstants;
import com.nined.userservice.dto.UserDetailsDto;

import lombok.extern.slf4j.Slf4j;

/**
 * A service class to handle fetching data using custom queries
 * 
 * @author vijay
 *
 */
@Slf4j
@Service
@PropertySource("classpath:queries.properties")
public class DatabaseQueryService {

    private static final String SPACE = " ";

    @Autowired
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Value("${query.findUsersByClient}")
    private String findUsersByClientQuery;

    @Value("${query.findUsersByClient.filterByRole}")
    private String findUsersByClientFilterByRole;

    @Value("${query.findUsersByClient.filterByActive}")
    private String findUsersByClientFilterByActive;

    /**
     * Method to
     * 
     * @param storeId
     * @param roleName
     * @param active
     * @return
     */
    public List<UserDetailsDto> findAllUsers(Long storeId, String roleName, boolean active) {
        if (storeId == null) {
            return Collections.emptyList();
        }
        if (log.isInfoEnabled()) {
            log.info("Getting all the users for given store [{}], role [{}], active flag [{}]",
                    storeId, roleName, active);
        }
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append(findUsersByClientQuery);
        if (StringUtils.isNotBlank(roleName)) {
            sbQuery.append(SPACE).append(findUsersByClientFilterByRole);
        }
        if (active) {
            sbQuery.append(SPACE).append(findUsersByClientFilterByActive);
        }

        if (log.isDebugEnabled()) {
            log.debug("Query :: findAllUsers :: [{}]", sbQuery);
        }
        // set query params
        MapSqlParameterSource paramSource =
                new MapSqlParameterSource(UserConstants.STORE_ID, storeId)
                        .addValue(UserConstants.ROLE_NAME, roleName);

        return namedParamJdbcTemplate.query(sbQuery.toString(), paramSource,
                (rs, rowNum) -> new UserDetailsDto(rs.getLong("USER_ID"), //
                        rs.getString("LOGONID"), //
                        rs.getString("FIRSTNAME"), //
                        rs.getString("LASTNAME"), //
                        rs.getString("EMAIL"), //
                        rs.getString("PHONE")));
    }
}
