import org.codehaus.groovy.grails.commons.ConfigurationHolder
class PayClientactJob {
  def agentKreditService
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.payclientact.cron!=[:])?ConfigurationHolder.config.payclientact.cron:"0 30 1 * * ?")
	}

  def execute() {
    log.debug("LOG>> PayClientactJob Start")
    agentKreditService.updateclientactpaidsum()
    agentKreditService.updateclientactfixsum()
    agentKreditService.updateagentactpaidsum()
    log.debug("LOG>> PayClientactJob Finish")
  }

}