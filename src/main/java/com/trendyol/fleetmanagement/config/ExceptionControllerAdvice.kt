package com.trendyol.fleetmanagement.config

import com.trendyol.fleetmanagement.exception.FleetManagementException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.text.MessageFormat
import java.util.Objects


@RestControllerAdvice
class ExceptionControllerAdvice {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): Map<String, String?> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage

            log.warn("An invalid request was rejected for reason: {}", errorMessage)
        }
        return errors
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FleetManagementException::class)
    fun handleBadRequestException(ex: FleetManagementException): Map<String, String?> {
        val errors: MutableMap<String, String?> = HashMap()
        if(Objects.isNull(ex.parameters) || ex.parameters.isEmpty()){
            errors["message"] = ex.message
        } else {
            errors["message"] = ex.message?.let { formatMessage(it, ex.parameters) }
        }
            log.error("An exception occurred, which will cause a {} response", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex)
        return errors
    }

    private fun formatMessage(pattern:String, replacements:Array<Any>): String {
        var message = pattern
        for (i in replacements.indices){
           message = message.replace("{$i}", replacements[i].toString())
        }
        return message
    }

}