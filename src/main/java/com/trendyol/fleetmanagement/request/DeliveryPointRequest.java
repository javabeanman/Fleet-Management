package com.trendyol.fleetmanagement.request;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public record DeliveryPointRequest(
                                   @ApiModelProperty(name = "Delivery Point Name")
                                   @NotBlank(message = "Name cannot be blank.")
                                   @Length(min = 4, max = 20, message = "Name must be between 4 and 20 characters.")
                                   String name,

                                   @ApiModelProperty(name = "Delivery Point Code")
                                   @NotBlank(message = "Code cannot be blank.")
                                   @Length(min = 8, max = 8, message = "Code must be 8 character.")
                                   String code,

                                   @ApiModelProperty("Place Of Delivery")
                                   @NotNull(message = "Place Of Delivery cannot be null.")
                                   @Min(value = 1, message = "Place Of Delivery can be 1,2 or 3.")
                                   @Max(value = 3, message = "Place Of Delivery can be 1,2 or 3.")
                                   Integer placeOfDelivery
                                   ) {
}
