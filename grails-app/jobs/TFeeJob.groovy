import org.codehaus.groovy.grails.commons.ConfigurationHolder
class TFeeJob {
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.tfee.cron!=[:])?ConfigurationHolder.config.tfee.cron:"0 0 1 1 * ?")
	}

  def execute() {
    log.debug("LOG>> TFeeJob Start")
    def today = new Date()
    Client.findAllByIs_t(1).each{ client ->
      if (!Payrequest.findByPaytypeAndPlatperiodAndClient_id(7,String.format('%tm.%<tY',today),client.id)) new Payrequest().csiSetPayrequest([paycat:4,paytype:7,client_id:client.id,paydate:String.format('%td.%<tm.%<tY',today),summa:client.fee]).csiSetModstatus(3).save(flush:true,failOnError:true)
    }
    log.debug("LOG>> TFeeJob Finish")
  }

}