import {Component, OnInit} from "@angular/core";
import {ROUTER_DIRECTIVES, RouteParams} from "@angular/router-deprecated";
import {Run} from "./run";
import {RunService} from "./run.service";
import {RunPage} from "./run-page";

@Component({
    templateUrl: 'app/run/run-list.html',
    styleUrls: ['app/run/run-list.css'],
    directives: [ROUTER_DIRECTIVES]
})
export class RunListComponent implements OnInit {
    errorMessage: string;
    runs: Run[] = [];
    nextPage: string;
    testSuiteId: string;
    constructor(
        private _runService: RunService,
        private _routeParams: RouteParams
    ) {}

    ngOnInit():any {
        this.testSuiteId = this._routeParams.get('testsuite_id');
        this.getRunsPaged(this.testSuiteId, null);
    }

    getRuns(testSuiteId: string) {
        this._runService.getRuns(testSuiteId).subscribe(
            runs => this.runs = runs,
            error => this.errorMessage = <any>error);
    }

    getRunsPaged(testSuiteId: string, nextPage: string) {
        this._runService.getRunsPaged(testSuiteId, nextPage).subscribe(
            runPage => this.extractPage(runPage),
            error => this.errorMessage = <any>error);
    }

    private extractPage(runPage: RunPage) {
        this.runs = this.runs.concat(runPage.runs);
        this.nextPage = runPage.nextPage;
    }

    more() {

        this.getRunsPaged(this.testSuiteId, this.nextPage);
    }
}