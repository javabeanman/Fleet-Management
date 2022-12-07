package com.trendyol.fleetmanagement.request;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record SackRequest(
                          @ApiModelProperty(name = "Sack Barcode")
                          @NotBlank(message = "Barcode cannot be blank.")
                          @Length(min = 7, max = 7, message = "Sack Barcode must be 7 character.")
                          String barcode,

                          @ApiModelProperty("Sack Desi")
                          @NotNull(message = "Desi cannot be null.")
                          @Min(value = 40, message = "Desi must be at least 40 or greater.")
                          Integer desi
                          ) {
}
