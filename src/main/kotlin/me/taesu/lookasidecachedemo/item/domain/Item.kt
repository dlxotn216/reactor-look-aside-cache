package me.taesu.lookasidecachedemo.item.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

/**
 * Created by itaesu on 2022/09/13.
 *
 * @author Lee Tae Su
 * @version look-aside-cache-demo
 * @since look-aside-cache-demo
 */
@Document(collection = "items")
class Item(
    id: String? = null,

    @Field(name = "itemName")
    val name: String,

    @Field(name = "itemDescription")
    val description: String
) {
    @Id
    var id: String? = id
        protected set
}

@Repository
interface ItemRepository: ReactiveMongoRepository<Item, String>
