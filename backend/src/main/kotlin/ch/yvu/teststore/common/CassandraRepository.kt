package ch.yvu.teststore.common

import com.datastax.driver.mapping.Mapper.Option.saveNullFields
import com.datastax.driver.mapping.MappingManager

abstract class CassandraRepository<M : Model>(val mappingManager: MappingManager, val table: String, val modelClass: Class<M>) {
    val session = mappingManager.session
    val mapper = mappingManager.mapper(modelClass)
    var pagedResultFetcher = PagedResultFetcher(session, mapper)
    var maxRowsResultFetcher = MaxRowsResultFetcher(pagedResultFetcher)

    open fun save(item: M): M {
        mapper.save(item, saveNullFields(false))
        return item
    }

    open fun deleteAll() {
        mapper.deleteQuery("DELETE FROM $table")
    }

    open fun findAll(): List<M> {
        val results = session.execute("SELECT * FROM $table")
        return mapper.map(results).all().toList()
    }

    open fun count(): Long {
        throw UnsupportedOperationException("not yet implemented")
    }
}