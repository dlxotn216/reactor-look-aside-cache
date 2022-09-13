package me.taesu.lookasidecachedemo.item.application

import kotlinx.coroutines.reactor.awaitSingle
import me.taesu.lookasidecachedemo.app.aop.LookAside
import me.taesu.lookasidecachedemo.app.aop.LookAsideDocumentId
import me.taesu.lookasidecachedemo.app.exception.ServiceRuntimeException
import me.taesu.lookasidecachedemo.item.domain.Item
import me.taesu.lookasidecachedemo.item.domain.ItemRepository
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.extra.retry.retryRandomBackoff
import java.time.Duration

/**
 * Created by itaesu on 2022/09/13.
 *
 * @author Lee Tae Su
 * @version look-aside-cache-demo
 * @since look-aside-cache-demo
 */
@Service
class ItemRetrieveService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>,
    private val itemRepository: ItemRepository
) {
    suspend fun retrieve(id: String): Item {
        return reactiveRedisTemplate.opsForHash<String, Item>().get("items", id)
            .switchIfEmpty {
                retrieveAndPut(id)
            }
            .awaitSingle()
            ?: throw ServiceRuntimeException("$id not found")
    }

    private fun retrieveAndPut(id: String): Mono<Item> {
        return itemRepository.findById(id)
            .retryRandomBackoff(3, Duration.ofMillis(100L), Duration.ofMillis(1000L))
            .flatMap { item ->
                reactiveRedisTemplate.opsForHash<String, Item>().put("items", id, item).map { item }
            }
    }

    @LookAside
    suspend fun retrieveWithApo(
        id: LookAsideDocumentId,
    ): Mono<Item> {
        return itemRepository.findById(id.documentId)
    }
}