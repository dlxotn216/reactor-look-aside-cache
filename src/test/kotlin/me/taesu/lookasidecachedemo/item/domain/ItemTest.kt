package me.taesu.lookasidecachedemo.item.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import reactor.test.StepVerifier

/**
 * Created by itaesu on 2022/09/13.
 *
 * @author Lee Tae Su
 * @version look-aside-cache-demo
 * @since look-aside-cache-demo
 */
@DataMongoTest
internal class ItemTest {
    @Autowired
    private lateinit var itemRepository: ItemRepository

    @Test
    fun `아이템 다큐먼트 생성 테스트`() {
        // given
        val item = itemRepository.save(Item(name = "상품 테스트", description = "설명")).block()!!

        // when
        val itemMono = itemRepository.findById(item.id!!)


        // then
        StepVerifier
            .create(itemMono)
            .assertNext {
                assertThat(it.name).isEqualTo("상품 테스트")
            }
            .expectComplete()
            .verify()
    }

}