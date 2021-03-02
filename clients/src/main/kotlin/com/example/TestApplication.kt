package com.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["com.example"])
private open class TestApplication

fun main(args: Array<String>) {
    SpringApplication.run(TestApplication::class.java, *args)

}
