import org.codehaus.groovy.grails.commons.ConfigurationHolder
class OffreportJob {
  def salaryService
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.offreport.cron!=[:])?ConfigurationHolder.config.offreport.cron:"0 0 2 * * ?")
	}

  def execute() {
    log.debug("LOG>> OffreportJob Start")
    salaryService.processoffreport()
    log.debug("LOG>> OffreportJob Finish")
  }

}