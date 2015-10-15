import org.codehaus.groovy.grails.commons.ConfigurationHolder
class DirectorSalaryJob {
    def salaryService
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.dsalary.cron!=[:])?ConfigurationHolder.config.dsalary.cron:"0 0 3 1 * ?")
	}

    def execute() {
        log.debug("LOG>> DirectorSalaryJob Start")
        salaryService.computeDirSalary()
        log.debug("LOG>> DirectorSalaryJob Finish")
    }

}