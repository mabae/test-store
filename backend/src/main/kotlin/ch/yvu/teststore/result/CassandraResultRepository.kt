package ch.yvu.teststore.result

import ch.yvu.teststore.common.CassandraRepository
import com.datastax.driver.mapping.MappingManager
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

open class CassandraResultRepository @Autowired constructor(mappingManager: MappingManager) :
        ResultRepository, CassandraRepository<Result>(mappingManager, "result", Result::class.java) {

    override fun updateFailureReason(runId: UUID, testName: String, retryNum: Int, failureReason: String) {
        session.execute("UPDATE result SET failureReason=? WHERE run=? AND testName=? AND retryNum=?",
                failureReason,
                runId,
                testName,
                retryNum)
    }

    override fun findAllByRunIdAndTestName(runId: UUID, testName: String): List<Result> {
        val results = session.execute("SELECT * FROM result WHERE run=? AND testName=?", runId, testName)
        return mapper.map(results).all().toList()
    }

    override fun findAllByRunId(runId: UUID): List<Result> {
        val results = session.execute("SELECT * FROM result WHERE run=?", runId)
        return mapper.map(results).all().toList()
    }

}