import org.codehaus.groovy.grails.commons.ConfigurationHolder

class FinlizingJob {
  def agentKreditService
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((ConfigurationHolder.config.flizing.cron!=[:])?ConfigurationHolder.config.flizing.cron:"0 0 8 * * ?")
	}

  def execute() {
    log.debug("LOG>> FinlizingJob Start")
    //archiving
    Finlizing.findAll{ enddate < new Date().clearTime() }.each{
      if ((agentKreditService.computeFinLizingBalance(it).bodydebt*100).toInteger()==0) it.updateModstatus(0).save(flush:true,failOnError:true)
    }
    log.debug("LOG>> FinlizingJob END")
  }
}