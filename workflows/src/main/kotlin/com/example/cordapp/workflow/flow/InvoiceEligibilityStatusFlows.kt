package com.example.cordapp.workflow.flow

import co.paralleluniverse.fibers.Suspendable
import com.example.cordapp.workflow.dto.InvoiceDto
import com.example.cordapp.workflow.repository.InvoiceRepository
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.serialization.CordaSerializable

fun FlowLogic<*>.invoiceRepository() = serviceHub.cordaService(InvoiceRepository::class.java)

@StartableByRPC
@CordaSerializable
class FindByKeysFlow(
    private val ids: Collection<String>
) : FlowLogic<Long>() {
    @Suspendable
    override fun call() = invoiceRepository().findByKeys(ids)
}

@StartableByRPC
@CordaSerializable
class SaveAllInvoicesFlow(
    private val eligibilityStatusEntities: List<InvoiceDto>
) : FlowLogic<Long>() {
    @Suspendable
    override fun call() = invoiceRepository().saveAll(eligibilityStatusEntities)
}

@StartableByRPC
@CordaSerializable
class UpdateAllInvoicesFlow(
    private val eligibilityStatusEntities: List<InvoiceDto>
) : FlowLogic<Long>() {
    @Suspendable
    override fun call() = invoiceRepository().updateAll(eligibilityStatusEntities)

}
