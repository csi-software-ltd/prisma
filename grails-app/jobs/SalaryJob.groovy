import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SalaryJob {
  def salaryService
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((ConfigurationHolder.config.salary.cron!=[:])?ConfigurationHolder.config.salary.cron:"0 0 8 * * ?")
	}

  def execute() {
    log.debug("LOG>> SalaryJob Start")
    salaryService.avans()
    salaryService.buhg()
    salaryService.salary()
    log.debug("LOG>> SalaryJob End")
  }
}
