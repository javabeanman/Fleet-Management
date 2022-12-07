package com.trendyol.fleetmanagement.request;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public record VehicleRequest(
                             @ApiModelProperty(name = "Vehicle Plate")
                             @NotBlank(message = "Plate cannot be blank.")
                             @Length(min = 7, max = 8, message = "Plate must be between 7 and 8 characters.")
                             String plate
                             ) {
}
