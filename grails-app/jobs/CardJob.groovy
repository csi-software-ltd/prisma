import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CardJob {
  def cardService
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((ConfigurationHolder.config.card.cron!=[:])?ConfigurationHolder.config.card.cron:"0 0 21 * * ?")
	}

  def execute() {
    log.debug("LOG>> CardJob Start")
    cardService.deactivateAndNotice()
    cardService.bankaccountNotice()
    log.debug("LOG>> CardJob End")
  }
}
