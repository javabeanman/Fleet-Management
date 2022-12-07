package com.trendyol.fleetmanagement.exception

enum class ErrorMessage(val message: String) {

    PACK_NOT_FOUND("No Pack found with Barcode {0} and Desi {1}."),

    SACK_NOT_FOUND("No Sack found with Barcode {0} and Desi {1}."),

    VEHICLE_NOT_FOUND("No Vehicle found with Plate {0}."),

    DELIVERY_POINT_NOT_FOUND("No Delivery Point found with Code {0}."),

    VEHICLE_ALREADY_EXIST("A Vehicle with Plate {0} already exists."),

    DELIVERY_POINT_ALREADY_EXIST("A Vehicle with Code {0} already exists."),

    PACK_CANNOT_BE_USED("Pack with Barcode {1} and Desi {0} is not in state {2}. This Pack cannot be used."),

    SACK_CANNOT_BE_USED("Sack with Barcode {1} and Desi {0} is not in state {2}. This Sack cannot be used."),

    VEHICLE_CANNOT_BE_USED("Vehicle with {0} Plate is not in state {1}. This Vehicle cannot be used."),

    THIS_CONTAINS_THIS("This {0} contains this {1}. Please check and try again."),

    THIS_DOES_NOT_CONTAIN_THIS("This {0} does not contain this {1}. Please check and try again."),

    NOT_ENOUGH_SPACE_IN_SACK("Not enough space in Sack for Pack."),

    NO_PACK_OR_SACK_ADDED("No Pack or Sack have been added to Vehicle."),

    EMPTY_SACKS_CANNOT_BE_ADDED("Empty Sack with Barcode {0} and Desi {1}.")
}