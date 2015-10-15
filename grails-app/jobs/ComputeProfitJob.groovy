import org.codehaus.groovy.grails.commons.ConfigurationHolder
class ComputeProfitJob {
  def agentKreditService
	static triggers = {
		//simple repeatInterval: 120000, repeatCount: 0 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.computeprofit.cron!=[:])?ConfigurationHolder.config.computeprofit.cron:"0 0 2 * * ?")
	}

  def execute() {
    log.debug("LOG>> ComputeProfitJob Start")
    def cal = Calendar.getInstance()
    def agrs = Agentagr.findAllByModstatus(1)
    (2014..new Date().getYear()+1901).each{
      cal.setTime(new Date(it-1900,0,1))
      while(cal.get(Calendar.YEAR)==it){
        agrs.each{ agr ->
          if (Actagent.findAll{ agentagr_id == agr.id && is_report == 0 && (month < cal.get(Calendar.MONTH)+1 || year < cal.get(Calendar.YEAR)) }) agentKreditService.computereportagentact(agr.id,cal.getTime())
        }
        cal.add(Calendar.MONTH,1)
      }
    }
    log.debug("LOG>> ComputeProfitJob Finish")
  }

}