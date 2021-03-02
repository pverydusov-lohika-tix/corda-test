package com.example.cordapp.workflow.dto

import com.example.cordapp.workflow.entity.InvoiceEntity
import com.example.cordapp.workflow.entity.InvoiceStatus
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class InvoiceDto(
    val invoiceId: String,
    val status: InvoiceStatus,
    val message: String
)

fun InvoiceDto.toEntity() =
    InvoiceEntity(
        invoiceId = invoiceId,
        status = status,
        message = message
    )

@CordaSerializable
data class InvoiceUpdate(
    val invoiceId: String,
    val status: InvoiceStatus,
    val comment: String
)

fun InvoiceDto.toUpdateObject() =
    InvoiceUpdate(
        invoiceId = invoiceId,
        status = status,
        comment = message
    )