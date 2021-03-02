package com.example.cordapp.workflow.repository

import com.example.cordapp.workflow.HibernateProperties
import com.example.cordapp.workflow.dto.InvoiceDto
import com.example.cordapp.workflow.dto.toEntity
import com.example.cordapp.workflow.entity.InvoiceEntity
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.core.utilities.loggerFor
import net.corda.nodeapi.internal.persistence.currentDBSession
import org.intellij.lang.annotations.Language

@CordaService
class InvoiceRepository(
        private val serviceHub: AppServiceHub
) : SingletonSerializeAsToken() {

    companion object {
        val logger = loggerFor<InvoiceRepository>()
    }

    fun findByKeys(ids: Collection<String>): Long {
        val start = System.currentTimeMillis()
        val invoices = if (ids.isEmpty()) emptyList()
        else {
            ids.chunked(HibernateProperties.TRACK_BY_PAGE_SIZE).flatMap { chunk ->
                serviceHub.withEntityManager {
                    @Language("SQL")
                    val query =
                        """
                            SELECT *
                            FROM invoice 
                            WHERE invoice_id IN :invoiceIds
                        """.trimIndent()
                    @Suppress("UNCHECKED_CAST")
                    createNativeQuery(query, InvoiceEntity::class.java)
                        .setParameter("invoiceIds", ids)
                        .resultList as List<InvoiceEntity>
                }
            }.map { it.toDto() }
        }

        val time = System.currentTimeMillis() - start
        logger.info("End findByKeys method, duration=$time ms")
        return time
    }

    fun saveAll(invoices: List<InvoiceDto>): Long {
        val start = System.currentTimeMillis()
        serviceHub.withEntityManager {
            val session = currentDBSession()
            session.jdbcBatchSize = HibernateProperties.JDBC_BATCH_SIZE
            invoices.mapIndexed { index, invoice ->
                if (index % HibernateProperties.JDBC_BATCH_SIZE == 0) session.flush()

                session.persist(invoice.toEntity())
            }
            session.flush()
        }
        val time = System.currentTimeMillis() - start
        logger.info("End saveAll method, duration=$time ms")
        return time
    }

    fun updateAll(invoices: List<InvoiceDto>): Long {
        val start = System.currentTimeMillis()
        if (invoices.isNotEmpty()) {
            serviceHub.withEntityManager {
                val session = currentDBSession()
                session.jdbcBatchSize = HibernateProperties.JDBC_BATCH_SIZE
                invoices.mapIndexed { index, invoice ->
                    if (index % HibernateProperties.JDBC_BATCH_SIZE == 0) session.flush()

                    session.merge(invoice.toEntity())
                }
                session.flush()
            }
        }
        val time = System.currentTimeMillis() - start
        logger.info("End updateAll method, duration=$time ms")
        return time
    }

}