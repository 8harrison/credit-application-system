package me.dio.credit.application.system.errors

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleMethodArgumentValidationExceptions(
        exception: MethodArgumentNotValidException,
        webRequest: WebRequest
    ): ResponseEntity<ApiError> {
        val errors = exception.bindingResult.fieldErrors.map { fieldError ->
            "${fieldError.field}: ${fieldError.defaultMessage}"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError(errors))
    }

    data class ApiError(val errors: List<String>) {
        constructor(error: String) : this(listOf(error))
    }
}