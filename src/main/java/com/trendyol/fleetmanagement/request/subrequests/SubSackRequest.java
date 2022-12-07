package com.trendyol.fleetmanagement.request.subrequests;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record SubSackRequest(
        @ApiModelProperty(name = "Sack Barcode")
        @NotBlank(message = "Sack Barcode cannot be blank.")
        @Length(min = 11, max = 11, message = "Sack Barcode must be 11 character.")
        String sackBarcode,

        @ApiModelProperty(name = "Sack Desi")
        @NotNull(message = "Sack Desi cannot be null.")
        @Min(value = 1, message = "Sack Desi can be at least 1.")
        Integer sackDesi
) {
}
