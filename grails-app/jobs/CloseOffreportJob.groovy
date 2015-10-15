import org.codehaus.groovy.grails.commons.ConfigurationHolder
class CloseOffreportJob {
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.closeoffreport.cron!=[:])?ConfigurationHolder.config.closeoffreport.cron:"0 0 1 1 * ?")
	}

  def execute() {
    log.debug("LOG>> CloseOffreportJob Start")
    Salaryreport.findAllBySalarytype_idAndModstatus(3,1).each { report ->
      report.csiSetModstatus(2).save(failOnError:true,flush:true)
    }
    log.debug("LOG>> CloseOffreportJob Finish")
  }

}