import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ServiceJob {
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((ConfigurationHolder.config.service.cron!=[:])?ConfigurationHolder.config.service.cron:"0 0 8 * * ?")
	}

  def execute() {
    log.debug("LOG>> ServiceJob Start")
    //prolong
    def cal = Calendar.getInstance()
    Service.findAllByModstatusAndProlongconditionAndEnddateLessThan(1,2,new Date()).each{
      it.prolongAgreement(cal).save(flush:true,failOnError:true)
    }
    //archiving
    Service.findAll{ enddate < new Date().clearTime() && prolongcondition == 0 }.each{
      it.updateModstatus(0).save(flush:true,failOnError:true)
    }
    log.debug("LOG>> ServiceJob END")
  }
}