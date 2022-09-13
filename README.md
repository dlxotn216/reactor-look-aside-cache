# Spring Webflux 기반 룩-어사이드(Look-aside) 캐시 패턴 구현

## 룩-어사이드 캐시 패턴이란?

## 구현 코드

```kotlin
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
```

## 개선점

만약 여러 Document에 대해서 적용이 필요하다면 retrieveAndPut 코드가 여기저기 존재할 것이다.  
AOP를 적용하면 어떨까?