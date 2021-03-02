package com.example.cordapp.workflow.entity

import com.example.cordapp.workflow.dto.InvoiceDto
import net.corda.core.schemas.MappedSchema
import net.corda.core.serialization.CordaSerializable
import javax.persistence.*

object InvoiceSchema

object InvoiceSchemaV1 : MappedSchema(
    schemaFamily = InvoiceSchema::class.java,
    version = 1,
    mappedTypes = listOf(InvoiceEntity::class.java)
) {
    override val migrationResource = "invoice.changelog-master"
}

@Entity
@Table(name = "invoice")
data class InvoiceEntity(
    @Id
    @Column(name = "invoice_id")
    val invoiceId: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    val status: InvoiceStatus,

    @Column(name = "message")
    val message: String?
) {
    fun toDto() =
        InvoiceDto(
            invoiceId = invoiceId,
            status = status,
            message = message ?: ""
        )
}

@CordaSerializable
enum class InvoiceStatus {
    PENDING,
    FAILED,
    INELIGIBLE,
    ELIGIBLE
}
