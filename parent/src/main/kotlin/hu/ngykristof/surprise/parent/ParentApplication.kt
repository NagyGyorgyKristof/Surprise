package hu.ngykristof.surprise.parent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ParentApplication

fun main(args: Array<String>) {
	runApplication<ParentApplication>(*args)
}
