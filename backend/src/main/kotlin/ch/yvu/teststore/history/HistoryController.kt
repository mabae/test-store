package ch.yvu.teststore.history

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class HistoryController @Autowired constructor(val historyService: HistoryService) {
    @RequestMapping(method = arrayOf(GET), value = "/testsuites/{testSuite}/history")
    fun getHistory(
            @PathVariable testSuite: UUID,
            @RequestParam(value = "limit") limit: Int): List<RunHistory> {

        return historyService.getRunHistories(testSuite, limit)
    }
}