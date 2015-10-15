import org.codehaus.groovy.grails.commons.ConfigurationHolder
class KreditDebtJob {
    def agentKreditService
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.KDebt.cron!=[:])?ConfigurationHolder.config.KDebt.cron:"0 0 22 * * ?")
	}

    def execute() {
        log.debug("LOG>> KreditDebtJob Start")
        Kredit.findAllByModstatus(1).each { kredit -> kredit.updateDebt(agentKreditService.computeKreditDebt(kredit)).save() }
        Lizing.findAllByModstatus(1).each { lizing -> lizing.updateDebt(agentKreditService.computeLizingDebt(lizing)).save() }
        log.debug("LOG>> KreditDebtJob Finish")
    }

}