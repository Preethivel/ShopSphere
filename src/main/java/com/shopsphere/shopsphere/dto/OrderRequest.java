package com.shopsphere.shopsphere.dto;

import lombok.Data;

@Data
public class OrderRequest {

    // delivery address
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryPincode;

    private String paymentMethod;
    // COD, UPI, CARD, NETBANKING

    private String notes;
    // optional special instructions
}