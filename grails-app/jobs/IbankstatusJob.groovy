import org.codehaus.groovy.grails.commons.ConfigurationHolder
class IbankstatusJob {
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.ibankstatus.cron!=[:])?ConfigurationHolder.config.ibankstatus.cron:"0 0 1 * * ?")
	}

  def execute() {
    log.debug("LOG>> IbankstatusJob Start")
    Bankaccount.list().each{
    	it.updateIbankstatus().save(flush:true)
    }
    Space.list().each{
    	it.computePaystatus().save(flush:true)
    }
    log.debug("LOG>> IbankstatusJob Finish")
  }

}