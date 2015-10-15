import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CardService {     

/////////////////////////////////////////////////////////
  def deactivateAndNotice(){
    def cToday=Calendar.getInstance()       
    //log.debug('today='+cToday.get(Calendar.DAY_OF_MONTH)+' end of month='+cToday.getActualMaximum(Calendar.DAY_OF_MONTH))
    
    if(cToday.get(Calendar.DAY_OF_MONTH)==cToday.getActualMaximum(Calendar.DAY_OF_MONTH)){
      def iMonth=cToday.get(Calendar.MONTH)+1
      def sMonth=''
      if(iMonth<10)
        sMonth='0'+iMonth.toString()
      else  
        sMonth=iMonth.toString()
      
      //log.debug('1:month='+sMonth+' year='+cToday.get(Calendar.YEAR).toString())
      for(oPersaccount in Persaccount.findAllByModstatusAndValidyearAndValidmonth(1,cToday.get(Calendar.YEAR).toString(),sMonth)){
        oPersaccount.csiSetModstatus(0).save(flush:true,failOnError:true)
      } 
///////notice>>/////////////////////////////       
      iMonth++ 
      def sYear=''
      if(iMonth<12){        
        sYear=cToday.get(Calendar.YEAR).toString()
      }else{  
        iMonth=1
        def iYear=cToday.get(Calendar.YEAR)+1
        sYear=iYear.toString()        
      }  
        
      if(iMonth<10)
        sMonth='0'+iMonth.toString()
      else  
        sMonth=iMonth.toString()
      
      def sTerm='01.'+sMonth+'.'+sYear
      
      def sDescription=''
      //log.debug('2:month='+sMonth+' year='+sYear)
      for(oPersaccount in Persaccount.findAllByModstatusAndValidyearAndValidmonth(1,sYear,sMonth)){
        sDescription+=(Pers.get(oPersaccount.pers_id?:0)?.shortname?:'') +' : '+ oPersaccount.nomer+' ; '
      }
      
      if(sDescription)
        new Task().csiSetTask([department_id:12,term:sTerm,tasktype_id:7,description:sDescription],1,0).save(flush:true,failOnError:true)      
    }         
  }

  def bankaccountNotice(){
    def today = new Date()+1
    Bankaccount.findAllByModstatusAndIbank_close(1,(new Date()+31).clearTime()).each{ baccaunt ->
      new Task().csiSetTask([department_id:Tools.getIntVal(Dynconfig.findByName('bankaccount.ibank.prolong.department')?.value,19),term:String.format('%td.%<tm.%<tY',today),tasktype_id:12,description:"Срок действия банк-клиента по компании ${Company.get(baccaunt.company_id)?.name} в банке ${Bank.get(baccaunt.bank_id)?.name} заканчивается ${String.format('%td.%<tm.%<tY',baccaunt.ibank_close)}",company_id:baccaunt.company_id],1,0).save(flush:true,failOnError:true)
    }
    Bankaccount.findAllByModstatusAndIbank_close(1,(new Date()+16).clearTime()).each{ baccaunt ->
      new Task().csiSetTask([department_id:Tools.getIntVal(Dynconfig.findByName('bankaccount.ibank.prolong.department')?.value,19),term:String.format('%td.%<tm.%<tY',today),tasktype_id:12,description:"Срок действия банк-клиента по компании ${Company.get(baccaunt.company_id)?.name} в банке ${Bank.get(baccaunt.bank_id)?.name} заканчивается ${String.format('%td.%<tm.%<tY',baccaunt.ibank_close)}",company_id:baccaunt.company_id],1,0).save(flush:true,failOnError:true)
    }
  }
}