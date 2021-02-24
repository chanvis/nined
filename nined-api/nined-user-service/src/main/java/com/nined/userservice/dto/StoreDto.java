package com.nined.userservice.dto;

import lombok.Data;

/**
 * Data transfer object for Store data
 * 
 * @author vijay
 *
 */
@Data
public class StoreDto implements Comparable<StoreDto> {

    private Long storeId;

    private String type;
    
    private String identifier;

    private String description;

    private boolean active;

    private boolean enable2FA;

    private String setCurr;
    
    private Long createdDate;

    private Long lastUpdatedDate;

    @Override
    public int compareTo(StoreDto client) {
        return identifier.compareTo(client.getIdentifier());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final StoreDto client = (StoreDto) obj;
        if (this == client) {
            return true;
        } else {
            return this.identifier.equals(client.getIdentifier());
        }
    }

    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (identifier == null ? 0 : identifier.hashCode());
        return hashno;
    }
}
