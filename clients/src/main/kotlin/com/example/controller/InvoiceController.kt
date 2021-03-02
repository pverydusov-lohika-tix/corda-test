package com.example.controller

import com.example.common.NodeRPCConnection
import com.example.cordapp.workflow.dto.InvoiceDto
import com.example.cordapp.workflow.dto.TestResult
import com.example.cordapp.workflow.entity.InvoiceStatus
import com.example.cordapp.workflow.flow.FindByKeysFlow
import com.example.cordapp.workflow.flow.SaveAllInvoicesFlow
import com.example.cordapp.workflow.flow.UpdateAllInvoicesFlow
import net.corda.client.jackson.JacksonSupport
import net.corda.core.messaging.startFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class InvoiceController(
    @Value("\${config.rpc.queryTimeoutMs}")
    private val queryTimeoutMs: Long,
    rpc: NodeRPCConnection
) {
    @Bean
    fun mappingJackson2HttpMessageConverter(@Autowired rpcConnection: NodeRPCConnection): MappingJackson2HttpMessageConverter {
        val mapper = JacksonSupport.createDefaultMapper(rpcConnection.proxy)
        val converter = MappingJackson2HttpMessageConverter()
        converter.objectMapper = mapper
        return converter
    }

    private val proxy = rpc.proxy


    @GetMapping("test/{count}")
    fun runInvoiceTest(
        @PathVariable count: Int
    ): ResponseEntity<TestResult> {
        val invoices = generateInvoices(count)
        val saveTime = proxy.startFlow(::SaveAllInvoicesFlow, invoices).returnValue.get(queryTimeoutMs, TimeUnit.MILLISECONDS)

        val newInvoices = invoices.map { it.copy(status = InvoiceStatus.ELIGIBLE) }
        val updateTime = proxy.startFlow(::UpdateAllInvoicesFlow, newInvoices).returnValue.get(queryTimeoutMs, TimeUnit.MILLISECONDS)

        val keys = invoices.map { it.invoiceId }
        val findTime = proxy.startFlow(::FindByKeysFlow, keys).returnValue.get(queryTimeoutMs, TimeUnit.MILLISECONDS)

        return ResponseEntity.ok().body(TestResult(
            saveTime = saveTime,
            updateTime = updateTime,
            findTime = findTime
        ))
    }

    private fun generateInvoices(count: Int): List<InvoiceDto> =
        (1..count).map {
            InvoiceDto(
                invoiceId = UUID.randomUUID().toString(),
                status = InvoiceStatus.PENDING,
                message = ""
            )
        }
}