package com.trendyol.fleetmanagement.request;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

public record PackRequest(
                          @ApiModelProperty(name = "Pack Barcode")
                          @NotBlank(message = "Barcode cannot be blank.")
                          @Length(min = 11, max = 11, message = "Barcode must be 11 character.")
                          String barcode,

                          @ApiModelProperty("Pack Width")
                          @NotNull(message = "Width cannot be null.")
                          @Min(value = 10, message = "Width must be at least 10(m²) or greater.")
                          Integer width,

                          @ApiModelProperty("Pack Length")
                          @NotNull(message = "Length cannot be null.")
                          @Min(value = 10, message = "Length must be at least 10(m²) or greater.")
                          Integer length,

                          @ApiModelProperty("Pack Height")
                          @NotNull(message = "Height cannot be null.")
                          @Min(value = 10, message = "Height must be at least 10(m²) or greater.")
                          Integer height
                          ) {
}
