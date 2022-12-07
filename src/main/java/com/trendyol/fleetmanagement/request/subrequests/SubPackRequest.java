package com.trendyol.fleetmanagement.request.subrequests;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record SubPackRequest(
        @ApiModelProperty(name = "Pack Barcode")
        @NotBlank(message = "Pack Barcode cannot be blank.")
        @Length(min = 11, max = 11, message = "Pack Barcode must be 11 character.")
        String packBarcode,

        @ApiModelProperty(name = "Pack Desi")
        @NotNull(message = "Pack Desi cannot be null.")
        @Min(value = 1, message = "Pack Desi can be at least 1.")
        Integer packDesi
        ) {
}
