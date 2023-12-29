package me.dio.credit.application.system.service

import io.mockk.MockKException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*


@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK
    lateinit var creditRepository: CreditRepository

    @InjectMockKs
    lateinit var creditService: CreditService
    @MockK
    lateinit var customerService: CustomerService

    @Test
    fun `should create a credit`() {
        //given
        val fakeCredit: Credit = buildCredit()
        val customerId: Long = 1
        every { customerService.findById(customerId) } returns fakeCredit.customer!!
        every { creditRepository.save(fakeCredit) } returns fakeCredit
        //when
        val actual: Credit = this.creditService.save(fakeCredit)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun `should find credit by customer`() {
        //given
        val fakeCustomer: Customer = buildCustomer()
        val fakeCredit: Credit = buildCredit(customer = fakeCustomer)
        every { creditRepository.findAllByCustomerId(fakeCustomer.id!!) } returns listOf(fakeCredit)
        //when
        val actual: List<Credit> = creditService.findAllByCustomer(fakeCustomer.id!!)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).startsWith(fakeCredit).isNotEmpty()
        verify(exactly = 1) { creditService.findAllByCustomer(fakeCustomer.id!!) }
    }

    @Test
    fun `should find credit by creditCode`() {
        //given
        val fakeId: Long = 1
        val creditCode: UUID = UUID.randomUUID()
        val credit: Credit = buildCredit(customer = Customer(id = fakeId))
        every { creditRepository.findByCreditCode(creditCode) } returns credit
        //when
        val actual: Credit = creditService.findByCreditCode(fakeId, creditCode)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(credit)
        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }

    @Test
    fun `should not find customer by invalid id and throw Exception`() {
        //given
        val id: Long = 2
        val credit: Credit = buildCredit()
        every { creditRepository.findAllByCustomerId(id) } returns listOf()
        every { creditRepository.save(credit) }.throws(BusinessException("Id $id not found"))
        //when
        //then
    }


    @Test
    fun `should return Exception with invalid creditCode`() {
        //given
        val id: Long = 1
        val creditCode: UUID = UUID.randomUUID()
        every { creditRepository.findByCreditCode(creditCode) }
            .throws(RuntimeException("Creditcode $creditCode not found"))
        //when
        //then
        Assertions.assertThatExceptionOfType(RuntimeException::class.java)
            .isThrownBy { creditService.findByCreditCode(id, creditCode) }
            .withMessage("Creditcode $creditCode not found")
    }

    @Test
    fun `should return an Exception invalid customerId` () {
        //given
        val id: Long = 2
        val creditCode: UUID = UUID.randomUUID()
        var credit: Credit = buildCredit()
        every { creditRepository.findByCreditCode(creditCode) } returns credit
        //when
        //then
        Assertions.assertThatExceptionOfType(RuntimeException::class.java)
            .isThrownBy { creditService.findByCreditCode(id, creditCode) }
            .withMessage("Contact admin")
        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }

    fun buildCredit(
        creditValue: BigDecimal = 1000.0.toBigDecimal(),
        dayFirstInstallment: LocalDate = LocalDate.of(2025, 12, 25),
        numberOfInstallments: Int = 10,
        customer: Customer = CustomerServiceTest.buildCustomer()
    ) = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = customer
    )

    companion object{
        fun buildCustomer(
            firstName: String = "Harrison",
            lastName: String = "MOnteiro",
            cpf: String = "13043245750",
            email: String = "monteiro4100@gmail.com",
            password: String = "1234",
            zipCode: String = "108106120",
            street: String = "Rua Adelino",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            id: Long = 1L
        ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                zipCode = zipCode,
                street = street
            ),
            income = income,
            id = id
        )
    }
}


