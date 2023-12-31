package me.dio.credit.application.system.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import org.hibernate.validator.constraints.br.CPF
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal

data class CustomerDto(
    @field:NotEmpty(message = "Invalid input") val firstName: String,
    @field:NotEmpty(message = "Invalid input") val lastName: String,
    @field:NotEmpty(message = "Invalid input")
    @CPF(message = "This is an invalid CPF") val cpf: String,
    @field:NotNull val income: BigDecimal,
    @field:Email(message = "Invalid email")
    @field:NotEmpty(message = "Invalid input") val email: String,
    @field:NotEmpty(message = "Invalid input") val password: String,
    @field:NotEmpty(message = "Invalid input") val zipCode: String,
    @field:NotEmpty(message = "Invalid input") val street: String
) {

    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        income = this.income,
        address = Address(
            zipCode = this.zipCode,
            street = this.street
        )
    )
}