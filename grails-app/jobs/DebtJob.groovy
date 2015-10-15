import org.codehaus.groovy.grails.commons.ConfigurationHolder

class DebtJob {

	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((ConfigurationHolder.config.debt.cron!=[:])?ConfigurationHolder.config.debt.cron:"0 0 8 * * ?")
	}

  def execute() {
    log.debug("LOG>> DebtJob Start")           
    
    for(oComp in Compers.findAll("FROM Compers WHERE salarydebt>0 AND modstatus=1")){
      def oPers=Pers.get(oComp?.pers_id)
      def bFlag=0
      if(oPers){   
        if(oPers?.perstype==1){
          if(Persaccount.findWhere(pers_id:oPers.id,is_main:1,modstatus:1)){
            bFlag=1
          }
        }else if(oPers?.perstype==3){
          if(Persaccount.findWhere(pers_id:oPers.id,is_main:0,modstatus:1)){
            bFlag=1
          }
        }else if(oPers?.perstype==2){
          if(Persaccount.findWhere(pers_id:oPers.id,is_main:0,modstatus:1) && Persaccount.findWhere(pers_id:oPers.id,is_main:1,modstatus:1)){
            bFlag=1
          }
        }        
        if(bFlag){
          new Task().csiSetTask([department_id:4,term:String.format('%td.%<tm.%<tY',new Date()),tasktype_id:8,company_id:oComp?.company_id?:0],1,0).save(flush:true,failOnError:true) 
        }
      }      
    }
    
    for(oCompany in Company.findAllWhere(is_taxdebt:1,modstatus:1)){
      if(Bankaccount.findWhere(company_id:oCompany?.id,ibankstatus:1,typeaccount_id:1))
        new Task().csiSetTask([department_id:4,term:String.format('%td.%<tm.%<tY',new Date()),tasktype_id:8,company_id:oCompany?.id?:0],1,0).save(flush:true,failOnError:true) 
    }
    
    log.debug("LOG>> DebtJob End")
  }
}
