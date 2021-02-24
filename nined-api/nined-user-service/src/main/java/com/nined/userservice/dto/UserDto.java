package com.nined.userservice.dto;

import lombok.Data;

/**
 * Data transfer object for User
 * 
 * @author vijay
 *
 */
@Data
public class UserDto implements Comparable<UserDto> {

    private Long userId;

    private String logonId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
    
    private RoleDto role;

    private boolean active;

    private boolean acctActivated;

    private Long createdDate;

    private Long lastUpdatedDate;

    @Override
    public int compareTo(UserDto user) {
        return lastName.compareTo(user.getLastName());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final UserDto user = (UserDto) obj;
        if (this == user) {
            return true;
        } else {
            return this.lastName.equals(user.getLastName());
        }
    }

    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (lastName == null ? 0 : lastName.hashCode());
        return hashno;
    }
}
