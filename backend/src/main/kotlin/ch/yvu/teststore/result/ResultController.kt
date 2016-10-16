package ch.yvu.teststore.result

import ch.yvu.teststore.common.Page
import ch.yvu.teststore.insert.InsertService
import ch.yvu.teststore.insert.dto.ResultDto
import ch.yvu.teststore.run.RunRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
class ResultController @Autowired constructor(
        val resultRepository: ResultRepository,
        val resultService: ResultService,
        val runRepository: RunRepository,
        val resultDiffService: ResultDiffService,
        val insertService: InsertService) {

    @RequestMapping(method = arrayOf(POST), value = "/results")
    fun createResult(
            @RequestParam(name = "run") run: UUID,
            @RequestParam(name = "testName") testName: String,
            @RequestParam(name = "retryNum") retryNum: Int,
            @RequestParam(name = "passed") passed: Boolean,
            @RequestParam(name = "durationMillis") durationMillis: Long,
            @RequestParam(name = "time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) time: Date,
            @RequestParam(name = "stackTrace", required = false) stackTrace: String?,
            response: HttpServletResponse) {

        val result = ResultDto(testName, retryNum, passed, durationMillis, time, stackTrace)
        insertService.insertResults(listOf(result), run)
        response.status = 201
    }

    @RequestMapping(method = arrayOf(GET), value = "/runs/{run}/results")
    fun getAllResults(@PathVariable run: UUID): List<Result> {
        return resultRepository.findAllByRunId(run)
    }

    @RequestMapping(method = arrayOf(GET), value = "/runs/{run}/results/filtered")
    fun getResultByRunAndTestName(
            @PathVariable run: UUID,
            @RequestParam(value = "testname", required = true) testName: String
    ): ResponseEntity<TestWithResults> {
        val decodedTestName = URLDecoder.decode(testName, "UTF-8")
        val result = resultService.getTestWithResults(run, decodedTestName)
        if (result == null) return ResponseEntity(NOT_FOUND)
        return ResponseEntity(result, OK)
    }

    @RequestMapping(method = arrayOf(GET), value = "/runs/{run}/results/grouped")
    fun getAllResultsGrouped(
            @PathVariable run: UUID): Map<TestWithResults.TestResult, List<TestWithResults>> {
        return resultService.getGroupedResults(run)
    }

    @RequestMapping(method = arrayOf(GET), value = "/runs/{run}/results/diff")
    fun getDiffToPrevRun(
            @PathVariable run: UUID): ResponseEntity<Map<ResultDiffService.DiffCategory, List<TestWithResults>>> {

        val currentRun = runRepository.findById(run);
        if (currentRun == null) return ResponseEntity(NOT_FOUND)

        val prevRun = runRepository.findLastRunBefore(currentRun.testSuite!!, currentRun.time!!)

        return ResponseEntity(resultDiffService.findDiff(prevRun?.id, currentRun.id!!), OK)
    }

    @RequestMapping(method = arrayOf(GET), value = "/testsuites/{testSuite}/tests/{testName:.+}")
    fun getResultsByTestSuiteAndTestName(
            @PathVariable testSuite: UUID,
            @PathVariable testName: String,
            @RequestParam(name = "page", required=false) page: String?
    ): Page<Result> {
        val decodedTestName = URLDecoder.decode(testName, "UTF-8")
        return resultService.getResultsByTestSuiteAndTestName(testSuite, decodedTestName, page)
    }

    @RequestMapping(method = arrayOf(PUT), value = "/runs/{run}/tests/{testName:.+}/{retryNum}")
    fun updateFailureReason(
            @PathVariable run: UUID,
            @PathVariable testName: String,
            @PathVariable retryNum: Int,
            @RequestParam failureReason: String
    ) {
        val decodedTestName = URLDecoder.decode(testName, "UTF-8")
        resultRepository.updateFailureReason(run, decodedTestName, retryNum, failureReason)
    }
}