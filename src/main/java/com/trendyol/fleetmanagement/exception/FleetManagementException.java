package com.trendyol.fleetmanagement.exception;

public class FleetManagementException extends RuntimeException {

    private ErrorMessage errorMessage;
    private Object[] parameters;

    public Object[] getParameters() {
        return parameters;
    }

    public FleetManagementException(ErrorMessage errorMessage, Object... parameters) {
        super(errorMessage.getMessage());
        this.parameters = parameters;
    }

    public FleetManagementException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }

}
