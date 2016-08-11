package ch.yvu.teststore.statistics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.util.*

@RestController
class StatisticsController @Autowired constructor(val testStatisticsRepository: TestStatisticsRepository){

    @RequestMapping(method = arrayOf(GET), value = "/testsuites/{testSuite}/statistics")
    fun getStatisticsByTestSuite(
            @PathVariable testSuite: UUID
    ): List<TestStatistics> {
        return testStatisticsRepository.findAllByTestSuite(testSuite)
    }

    @RequestMapping(method = arrayOf(GET), value = "/testsuites/{testSuite}/statistics/{testName:.+}")
    fun getStatisticsByTestSuiteAndTestName(
            @PathVariable testSuite: UUID,
            @PathVariable testName: String): ResponseEntity<TestStatistics> {
        val testNameDecoded = URLDecoder.decode(testName, "UTF-8")
        val testStatistics = testStatisticsRepository.findByTestSuiteAndTestName(testSuite, testNameDecoded)

        if(testStatistics == null) return ResponseEntity(NOT_FOUND)
        else return ResponseEntity(testStatistics, OK)
    }

}