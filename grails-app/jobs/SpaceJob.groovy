import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SpaceJob {
  def static final DATE_FORMAT='dd.MM.yyyy'
  
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((ConfigurationHolder.config.space.cron!=[:])?ConfigurationHolder.config.space.cron:"0 0 8 * * ?")
	}

  def execute() {
    log.debug("LOG>> SpaceJob Start")
    def iDays=Tools.getIntVal(ConfigurationHolder.config.space.days_before_end,7)
    for(oSpace in Space.findAllByModstatus(1)){     
      if(oSpace.enddate[Calendar.DAY_OF_YEAR]-iDays==new Date()[Calendar.DAY_OF_YEAR]){        
        def hsInrequest=[term:(new Date()+iDays).format(DATE_FORMAT),tasktype_id:2,department_id:(oSpace.spacetype_id==6?10:5),link:oSpace.id]      
        new Task().csiSetTask(hsInrequest,1,0).save(flush:true,failOnError:true)
      }
    }
    //prolong
    def cal = Calendar.getInstance()
    Space.findAllByProlongconditionAndEnddateLessThan(2,new Date()).each{
      it.prolongAgreement(cal).save(flush:true,failOnError:true)
    }
    //archiving
    Space.findAll{ enddate < new Date().clearTime() && (permitstatus == -1 || prolongcondition == 0)}.each{
      it.updateModstatus(0).save(flush:true,failOnError:true)
    }
    //clear statuses
    new SpaceSearch().csiFindProlonged().each{
      Space.get(it).clearProlong().save(flush:true,failOnError:true)
    }
    //accrue payments
    cal.setTime(new Date())
    Space.findAllByModstatusAndIs_nopaymentAndPaytermLessThanEquals(1,0,cal.getActualMaximum(Calendar.DAY_OF_MONTH)==cal.get(Calendar.DAY_OF_MONTH)?31:cal.get(Calendar.DAY_OF_MONTH)).each{
      if (it.paytermcondition==1) cal.add(Calendar.MONTH,-1)
      else if (it.paytermcondition==3) cal.add(Calendar.MONTH,1)
      if (!Spacecalculation.findAllBySpace_idAndMonthAndYear(it.id,cal.get(Calendar.MONTH)+1,cal.get(Calendar.YEAR))) it.accruePayments(cal.getTime(),cal.getActualMaximum(Calendar.DAY_OF_MONTH))
      cal.setTime(new Date())
    }
    //accrue service payments
    cal.setTime(new Date())
    Service.findAllByModstatusAndPayconditionGreaterThanAndPaytermLessThanEquals(1,0,cal.getActualMaximum(Calendar.DAY_OF_MONTH)==cal.get(Calendar.DAY_OF_MONTH)?31:cal.get(Calendar.DAY_OF_MONTH)).each{
      if (it.paytermcondition==1) cal.add(Calendar.MONTH,-1)
      else if (it.paytermcondition==3) cal.add(Calendar.MONTH,1)
      if (!Servicecalculation.findAllByService_idAndMonthAndYear(it.id,cal.get(Calendar.MONTH)+1,cal.get(Calendar.YEAR))) it.accruePayments(cal.getTime(),cal.getActualMaximum(Calendar.DAY_OF_MONTH))
      cal.setTime(new Date())
    }
    log.debug("LOG>> SpaceJob END")
  }
}