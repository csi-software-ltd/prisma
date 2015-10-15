import org.codehaus.groovy.grails.commons.ConfigurationHolder
class KreditVRatesJob {
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.KVRates.cron!=[:])?ConfigurationHolder.config.KVRates.cron:"0 0 4 * * ?")
	}

    def execute() {
        log.debug("LOG>> KreditVRatesJob Start")
        Kredit.findAllByModstatusAndValuta_idNotEqual(1,857).each { kredit -> kredit.recalculateRubSummas() }
        Loan.findAllByModstatusAndValuta_idNotEqual(1,857).each { loan -> loan.recalculateRubSummas() }
        log.debug("LOG>> KreditVRatesJob Finish")
    }

}