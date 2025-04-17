package com.travel_payment.cnpm.dto.request.user;

import lombok.Data;

@Data
public class UserUdReq {
    private String fullName;

    private String phoneNumber;

    private String dob;
}
