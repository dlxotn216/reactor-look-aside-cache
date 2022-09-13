package me.taesu.lookasidecachedemo.app.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Aspect
@Component
class LookAsideCacheAopConfig(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>,
) {
    @Around(value = "@annotation(me.taesu.lookasidecachedemo.app.aop.LookAside)")
    fun lookAside(
        joinPoint: ProceedingJoinPoint
    ): Any {
        val argument = joinPoint.args.filterIsInstance(LookAsideDocumentId::class.java).firstOrNull()
            ?: throw IllegalArgumentException("")
        val entityId = argument.entityId
        val documentId = argument.documentId

        return reactiveRedisTemplate.opsForHash<String, Any>().get(entityId, documentId)
            .onErrorResume {
                joinPoint.proceed() as Mono<*>
            }
            .switchIfEmpty {
                val proceed = joinPoint.proceed() as Mono<*>
                proceed.flatMap { item ->
                    reactiveRedisTemplate.opsForHash<String, Any>().put(entityId, documentId, item).map { item }
                }
            }
    }
}