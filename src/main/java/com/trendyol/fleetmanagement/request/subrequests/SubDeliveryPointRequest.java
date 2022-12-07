package com.trendyol.fleetmanagement.request.subrequests;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public record SubDeliveryPointRequest(

        @ApiModelProperty(name = "Delivery Point Code")
        @NotEmpty(message = "Delivery Point Code cannot be empty")
        @Length(min = 8, max = 8, message = "Delivery Point Code must be 8 character.")
        String deliveryPointCode
) {

}
