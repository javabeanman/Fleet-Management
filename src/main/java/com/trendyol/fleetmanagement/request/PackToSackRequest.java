package com.trendyol.fleetmanagement.request;

import com.trendyol.fleetmanagement.request.subrequests.SubPackRequest;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record PackToSackRequest(
                                    @ApiModelProperty(name = "Sack Barcode")
                                    @NotBlank(message = "Sack Barcode cannot be blank.")
                                    @Length(min = 7, max = 7, message = "Sack Barcode must be 7 character.")
                                    String sackBarcode,

                                    @ApiModelProperty(name = "Sack Desi")
                                    @NotNull(message = "Sack Desi cannot be null.")
                                    @Min(value = 1, message = "Sack Desi can be at least 1.")
                                    Integer sackDesi,

                                    SubPackRequest pack
                                    ) {
}
