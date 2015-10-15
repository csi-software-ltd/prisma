import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ActsaldohistoryJob {
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((ConfigurationHolder.config.actsaldohistory.cron!=[:])?ConfigurationHolder.config.actsaldohistory.cron:"0 0 23 * * ?")
	}

  def execute() {
    log.debug("LOG>> ActsaldohistoryJob Start")
    
    for(oCompany in Company.findAllByIs_holdingAndModstatus(1,1)){    
      for(oBankaccount in Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_idLessThan(oCompany?.id,1,6)){
        new Actsaldohistory().csiSetData(oBankaccount).save(flush:true,failOnError:true)
      }
    }
    
    log.debug("LOG>> ActsaldohistoryJob End")
  }
}
