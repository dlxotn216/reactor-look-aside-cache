package me.taesu.lookasidecachedemo.item.interfaces

import kotlinx.coroutines.reactor.awaitSingle
import me.taesu.lookasidecachedemo.item.application.ItemRetrieveService
import me.taesu.lookasidecachedemo.app.aop.LookAsideDocumentId
import me.taesu.lookasidecachedemo.item.domain.Item
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Created by itaesu on 2022/09/13.
 *
 * @author Lee Tae Su
 * @version look-aside-cache-demo
 * @since look-aside-cache-demo
 */
@RestController
class ItemRetrieveController(private val service: ItemRetrieveService) {
    @GetMapping("/api/v1/items/{id}")
    suspend fun retrieve(@PathVariable id: String): Item {
        return service.retrieve(id)
    }

    @GetMapping("/api/v2/items/{id}")
    suspend fun retrieveWithApo(@PathVariable id: String): Item {
        return service.retrieveWithApo(LookAsideDocumentId("items", id)).awaitSingle()
    }
}