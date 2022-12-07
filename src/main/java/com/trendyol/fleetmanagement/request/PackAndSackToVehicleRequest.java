package com.trendyol.fleetmanagement.request;

import com.trendyol.fleetmanagement.request.subrequests.SubPackRequest;
import com.trendyol.fleetmanagement.request.subrequests.SubSackRequest;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public record PackAndSackToVehicleRequest(

        @ApiModelProperty(name = "Vehicle Plate")
        @NotBlank(message = "Vehicle Plate cannot be blank.")
        @Length(min = 7, max = 8, message = "Vehicle Plate must be between 7 and 8 characters.")
        String vehiclePlate,

        @ApiModelProperty(name = "Delivery Point Code")
        @NotBlank(message = "Delivery Point Code cannot be blank.")
        @Length(min = 8, max = 8, message = "Delivery Point Code must be 8 character.")
        String deliveryPointCode,

        Set<SubSackRequest> sackRequests,

        Set<SubPackRequest> packRequests
) {
}
