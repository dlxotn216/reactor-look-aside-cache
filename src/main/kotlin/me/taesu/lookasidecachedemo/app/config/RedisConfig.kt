package me.taesu.lookasidecachedemo.app.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by itaesu on 2022/09/13.
 *
 * @author Lee Tae Su
 * @version look-aside-cache-demo
 * @since look-aside-cache-demo
 */
@Configuration
@EnableRedisRepositories
class RedisConfiguration {
    @Bean
    fun reactiveRedisConnectionFactory(
        redisProperties: RedisProperties
    ): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(redisProperties.redisHost, redisProperties.redisPort)
    }

    @Bean
    fun reactiveRedisTemplate(
        reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
        objectMapper: ObjectMapper
    ): ReactiveRedisTemplate<String, Any> {
        val keySerializer = StringRedisSerializer()
        val valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        val builder = RedisSerializationContext.newSerializationContext<String, Any>(keySerializer)
        return ReactiveRedisTemplate(
            reactiveRedisConnectionFactory,
            builder.value(valueSerializer).build(),
            // builder.hashValue(valueSerializer).build()
        )
    }
}

@Profile("local")
@Configuration
class TestRedisConfiguration(redisProperties: RedisProperties) {
    private val redisServer: RedisServer = RedisServer(redisProperties.redisPort)

    @PostConstruct
    fun postConstruct() {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
    }
}

@Configuration
class RedisProperties(
    @Value("\${spring.redis.port}") val redisPort: Int,
    @Value("\${spring.redis.host}") val redisHost: String
)