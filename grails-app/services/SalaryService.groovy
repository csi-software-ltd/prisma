import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SalaryService {     

def static final DATE_FORMAT='dd.MM.yyyy' 
/////////////////////////////////////////////////////////
  def avans(){
    def cToday=Calendar.getInstance()       
    def iDaysLeft=0
    
    for(def i=cToday.get(Calendar.DAY_OF_MONTH);i<=cToday.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
      def cTmp=Calendar.getInstance()
      cTmp.set(Calendar.HOUR_OF_DAY ,0)
      cTmp.set(Calendar.MINUTE ,0)
      cTmp.set(Calendar.SECOND,0)
      cTmp.set(Calendar.MILLISECOND,0)
          
      cTmp.set(Calendar.DAY_OF_MONTH,i)
      
      //log.debug('status='+Holiday.findByHdate(cTmp.getTime())?.status+' date='+cTmp.getTime().date)
      
      if(Holiday.findByHdate(cTmp.getTime())?.status!=1 && (!(cTmp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cTmp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) || Holiday.findByHdate(cTmp.getTime())?.status==0)){
        iDaysLeft++
      }
    }
    log.debug('iDaysLeft='+iDaysLeft)
    if(iDaysLeft==Tools.getIntVal(ConfigurationHolder.config.salary_avans.working_days_until_month_end,4)){     
      for(oUser in User.findAllByIs_leaderAndModstatus(1,1)){
        def hsInrequest=[term:new Date().format(DATE_FORMAT),tasktype_id:3,executor:oUser.id]
      
        new Task().csiSetTask(hsInrequest,1,0).save(flush:true,failOnError:true)
      }
    }    
  }  
/////////////////////////////////////////////////////////  
  def csiFindDay(iDayX,bType){
    def cToday=Calendar.getInstance()        
    def cDayX=Calendar.getInstance()
    
    cDayX.set(Calendar.HOUR_OF_DAY ,0)
    cDayX.set(Calendar.MINUTE ,0)
    cDayX.set(Calendar.SECOND,0)
    cDayX.set(Calendar.MILLISECOND,0)        
    
    cDayX.set(Calendar.DAY_OF_MONTH,iDayX)
    
    if(cToday.getTime().date<cDayX.getTime().date){
      return
    }
    
    if(cDayX.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cDayX.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
      if(Holiday.findByHdate(cDayX.getTime())?.status==0){//work day
        if(cDayXFor.getTime().date==cToday.getTime().date){
         //run code
          //log.debug('run code 1')
          //log.debug('date='+cDayX.getTime().date)
          if(bType)
            salaryTask()
          else
            buhgTask()
        } 
      }else{        
        for(def i=cDayX.get(Calendar.DAY_OF_MONTH);i<=cToday.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
          def cDayXFor=Calendar.getInstance()
          cDayXFor.set(Calendar.HOUR_OF_DAY ,0)
          cDayXFor.set(Calendar.MINUTE ,0)
          cDayXFor.set(Calendar.SECOND,0)
          cDayXFor.set(Calendar.MILLISECOND,0)
          
          cDayXFor.set(Calendar.DAY_OF_MONTH,i)
          if(!(cDayXFor.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cDayXFor.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)){
            if(Holiday.findByHdate(cDayXFor.getTime())?.status!=1){//not holiday
              if(cDayXFor.getTime().date==cToday.getTime().date){
                //run code
                //log.debug('run code 2')
                //log.debug('date='+cDayXFor.getTime().date)
                if(bType)
                  salaryTask()
                else
                  buhgTask()
              }
              break               
            }
          }else if(Holiday.findByHdate(cDayXFor.getTime())?.status==0){//work day
            if(cDayXFor.getTime().date==cToday.getTime().date){
                 //run code
                 //log.debug('run code 3')
                 //log.debug('date='+cDayXFor.getTime().date)
                 if(bType)
                   salaryTask()
                 else
                   buhgTask()
            }
            break
          }          
        }
      }         
    }else if(Holiday.findByHdate(cDayX.getTime())?.status==1){//holiday
      for(def i=cDayX.get(Calendar.DAY_OF_MONTH);i<=cToday.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
          def cDayXFor=Calendar.getInstance()
          
          cDayXFor.set(Calendar.HOUR_OF_DAY ,0)
          cDayXFor.set(Calendar.MINUTE ,0)
          cDayXFor.set(Calendar.SECOND,0)
          cDayXFor.set(Calendar.MILLISECOND,0)
          
          cDayXFor.set(Calendar.DAY_OF_MONTH,i)
          if(!(cDayXFor.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cDayXFor.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)){
            log.debug('status='+Holiday.findByHdate(cDayXFor.getTime())?.status+' date='+cDayXFor.getTime().date)
            if(Holiday.findByHdate(cDayXFor.getTime())?.status!=1){//not holiday
              if(cDayXFor.getTime().date==cToday.getTime().date){
                 //run code
                 //log.debug('run code 4')
                 //log.debug('date='+cDayXFor.getTime().date)
                 
                 if(bType)
                   salaryTask()
                 else
                   buhgTask()
              }
              break
            }
          }else if(Holiday.findByHdate(cDayXFor.getTime())?.status==0){//work day
            if(cDayXFor.getTime().date==cToday.getTime().date){
                 //run code
                 //log.debug('run code 5')
                 //log.debug('date='+cDayXFor.getTime().date)
                 
                 if(bType)
                   salaryTask()
                 else
                   buhgTask()
            }
            break
          }          
        }
    }else{
      if(cDayX.getTime().date==cToday.getTime().date){
        //run code
        //log.debug('run code 6')
        //log.debug('date='+cDayX.getTime().date)
        
        if(bType)
          salaryTask()
        else
          buhgTask()
      }     
    }    
  } 
/////////////////////////////////////////////////////////
  def buhg(){
    csiFindDay(Tools.getIntVal(ConfigurationHolder.config.salary_buhg.day_of_month,7),0)
  }
/////////////////////////////////////////////////////////
  def salary(){
    csiFindDay(Tools.getIntVal(ConfigurationHolder.config.salary.day_of_month,11),1)
  }  
/////////////////////////////////////////////////////////  
  def salaryTask(){
    for(oUser in User.findAllByIs_leaderAndModstatus(1,1)){
      def hsInrequest=[term:new Date().format(DATE_FORMAT),tasktype_id:5,executor:oUser.id]
      
      new Task().csiSetTask(hsInrequest,1,0).save(flush:true,failOnError:true)
    }    
  }
/////////////////////////////////////////////////////////  
  def buhgTask(){  
    def hsInrequest=[term:new Date().format(DATE_FORMAT),tasktype_id:4,department_id:12]
      
    new Task().csiSetTask(hsInrequest,1,0).save(flush:true,failOnError:true)    
  }

  void createNewBuhreport(lAdminId, Date _reportdate){
    new CompersSearch().csiFindNonLinkedCompers(_reportdate.getMonth()+1,_reportdate.getYear()+1900).groupBy{it.company_id}.each{ cs ->
      def perssalaryList = []
      cs.value.each{
        perssalaryList << new Salarycomp(is_pers:1).setData(collectPersBuhDataFrom(it),_reportdate).save(failOnError:true)
      }
      new Salarycomp(is_pers:0).setData(collectCompBuhDataFrom(cs.value[0],perssalaryList),_reportdate).save(failOnError:true)
    }
    new Salaryreport(month:_reportdate.getMonth()+1,year:_reportdate.getYear()+1900,salarytype_id:2,department_id:0).setData(null).csiSetSumma(0g).csiSetModstatus(0).csiSetAdmin(lAdminId).save(failOnError:true)
  }

  LinkedHashMap collectPersBuhDataFrom(CompersSearch _cs){
    def data = [:]
    def oCompany = Company.get(_cs.company_id)
    data.companyname = oCompany.name
    data.companyinn = oCompany.inn
    data.fio = _cs.shortname
    data.snils = Pers.get(_cs.pers_id)?.snils
    data.position = Position.get(_cs.position_id)?.name
    data.fullsalary = _cs.salary
    data.netsalary = data.fullsalary * 0.87
    data.region = ''
    data
  }

  LinkedHashMap collectCompBuhDataFrom(CompersSearch _cs, ArrayList _persData){
    def data = [:]
    def oCompany = Company.get(_cs.company_id)
    data.companyname = oCompany.name
    data.companyinn = oCompany.inn
    data.fullsalary = _persData.sum{it.fullsalary}
    data.netsalary = _persData.sum{it.netsalary}
    data.ndfl = data.fullsalary * 0.13
    data.fss_tempinvalid = data.fullsalary * 0.029
    data.fss_accident = data.fullsalary * 0.002
    data.ffoms = data.fullsalary * 0.051
    data
  }

  Integer computeCardsSummaForReport(_report){
    Date _baseDate = new Date(_report.year-1900,_report.month-1,1)
    Pers.findAllByPerstype(2).each{ pers ->
      def tempactsalary = computeDirSalary(_baseDate,pers).toBigDecimal()
      Salarycomp.findAllByMonthAndYearAndPers_id(_report.month,_report.year,pers.id,[sort:'is_noaccount',order:'asc']).each{
        it.csiUpdateDirectorsCardsSumma(tempactsalary - it.netsalary >= 0 ? it.netsalary : tempactsalary > 0 ? tempactsalary : 0g).save(failOnError:true)
        tempactsalary = tempactsalary - it.netsalary > 0 ? tempactsalary - it.netsalary : 0g
        if (pers.emplpers_id>0) User.findByPers_idAndModstatus(pers.emplpers_id,1,[sort:'cassadebt',order:'desc'])?.updateCassadebt(-it.empldebt+it.computeempldebt().save(failOnError:true).empldebt)?.save(failOnError:true,flush:true)
      }.find{ it.id }?.csiUpdateCashsalary(tempactsalary)?.save(failOnError:true)
    }
    Pers.findAllByPerstype(1).each{ pers ->
      Salarycomp.findAllByMonthAndYearAndPers_id(_report.month,_report.year,pers.id,[sort:'is_noaccount',order:'asc']).each{
        it.csiUpdateEmployeeCardsSumma().save(failOnError:true)
      }
    }
    Pers.findAllByPerstype(3).each{ pers ->
      Salarycomp.findAllByMonthAndYearAndPers_id(_report.month,_report.year,pers.id,[sort:'is_noaccount',order:'asc']).each{
        it.csiUpdateTechniciansCardsSumma().save(failOnError:true)
      }
    }
    return 2
  }

  Integer createPayrequests(_report){
    def _ndspercent = Tools.getIntVal(Dynconfig.findByName('payrequest.nds.value')?.value,18)/100
    ArrayList prequestList = []
    Salarycomp.findAllByMonthAndYearAndIs_persAndIs_noaccountAndPaidstatus(_report.month,_report.year,0,0,0).each{ scomp ->
      //все налоги
      if (scomp.ndfl+scomp.debtndfl>0) prequestList << new Payrequest(summa:scomp.ndfl+scomp.debtndfl,summands:(scomp.ndfl+scomp.debtndfl)*_ndspercent,paytype:1,paycat:2,tax_id:1,kbkrazdel_id:6).computePaydate().filldata(scomp).save(failOnError:true)
      if (scomp.fss_tempinvalid+scomp.debtfss_tempinvalid>0) prequestList << new Payrequest(summa:scomp.fss_tempinvalid+scomp.debtfss_tempinvalid,summands:(scomp.fss_tempinvalid+scomp.debtfss_tempinvalid)*_ndspercent,paytype:1,paycat:2,tax_id:4,kbkrazdel_id:7).computePaydate().filldata(scomp).save(failOnError:true)
      if (scomp.fss_accident+scomp.debtfss_accident>0) prequestList << new Payrequest(summa:scomp.fss_accident+scomp.debtfss_accident,summands:(scomp.fss_accident+scomp.debtfss_accident)*_ndspercent,paytype:1,paycat:2,tax_id:5,kbkrazdel_id:7).computePaydate().filldata(scomp).save(failOnError:true)
      if (scomp.ffoms+scomp.debtffoms>0) prequestList << new Payrequest(summa:scomp.ffoms+scomp.debtffoms,summands:(scomp.ffoms+scomp.debtffoms)*_ndspercent,paytype:1,paycat:2,tax_id:3,kbkrazdel_id:8).computePaydate().filldata(scomp).save(failOnError:true)
      if (scomp.pf+scomp.debtpf>0) prequestList << new Payrequest(summa:scomp.pf+scomp.debtpf,summands:(scomp.pf+scomp.debtpf)*_ndspercent,paytype:1,paycat:2,tax_id:2,kbkrazdel_id:7).computePaydate().filldata(scomp).save(failOnError:true)
      scomp.csiSetPaidstatus(1).save(failOnError:true)
    }
    Salarycomp.findAllByMonthAndYearAndIs_persAndPaidmainstatus(_report.month,_report.year,1,0).each{ scomp ->
      //все основные карты
      if (scomp.cardmain>0) prequestList << new Payrequest(summa:scomp.cardmain,summands:0,paytype:1,paycat:3,tax_id:0).computePaydate().filldata(scomp).save(failOnError:true)
      scomp.csiSetPaidmainstatus(1).save(failOnError:true)
    }
    Salarycomp.findAllByMonthAndYearAndIs_persAndPaidaddstatus(_report.month,_report.year,1,0).each{ scomp ->
      //все доп карты
      if (scomp.cardadd>0) prequestList << new Payrequest(summa:scomp.cardadd,summands:0,paytype:1,paycat:3,tax_id:0).computePaydate().filldata(scomp,true).save(failOnError:true)
      scomp.csiSetPaidaddstatus(1).save(failOnError:true)
    }
    if(prequestList.size()>0){
      prequestList.groupBy{it.fromcompany_id}.each{ lsReq ->
        def oTaskpay = new Taskpay(paygroup:1).csiSetTaskpay(term:lsReq.value[0].paydate.format(DATE_FORMAT),company_id:lsReq.value[0].fromcompany_id,summa:lsReq.value.sum{it.summa}).csiSetInitiator(0).save(flush:true,failOnError:true)
        lsReq.value.each{ it.csiSetTaskpay_id(oTaskpay.id).csiSetModstatus(1).save(flush:true,failOnError:true) }
      }
    }
    return 1
  }

  void createDefferedPayrequests(_report){
    def _ndspercent = Tools.getIntVal(Dynconfig.findByName('payrequest.nds.value')?.value,18)/100
    ArrayList prequestList = []
    Salarycomp.findAllByMonthAndYearAndIs_persAndIs_noaccountAndPaidstatus(_report.month,_report.year,0,1,0).each{ scomp ->
      if(Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(scomp.company_id,1,1).find{it.ibankstatus==1}){
        //все налоги
        if (scomp.ndfl+scomp.debtndfl>0) prequestList << new Payrequest(summa:scomp.ndfl+scomp.debtndfl,summands:(scomp.ndfl+scomp.debtndfl)*_ndspercent,paytype:1,paycat:2,tax_id:1,kbkrazdel_id:6).computePaydate(new Date()).filldata(scomp).save(failOnError:true)
        if (scomp.fss_tempinvalid+scomp.debtfss_tempinvalid>0) prequestList << new Payrequest(summa:scomp.fss_tempinvalid+scomp.debtfss_tempinvalid,summands:(scomp.fss_tempinvalid+scomp.debtfss_tempinvalid)*_ndspercent,paytype:1,paycat:2,tax_id:4,kbkrazdel_id:7).computePaydate(new Date()).filldata(scomp).save(failOnError:true)
        if (scomp.fss_accident+scomp.debtfss_accident>0) prequestList << new Payrequest(summa:scomp.fss_accident+scomp.debtfss_accident,summands:(scomp.fss_accident+scomp.debtfss_accident)*_ndspercent,paytype:1,paycat:2,tax_id:5,kbkrazdel_id:7).computePaydate(new Date()).filldata(scomp).save(failOnError:true)
        if (scomp.ffoms+scomp.debtffoms>0) prequestList << new Payrequest(summa:scomp.ffoms+scomp.debtffoms,summands:(scomp.ffoms+scomp.debtffoms)*_ndspercent,paytype:1,paycat:2,tax_id:3,kbkrazdel_id:8).computePaydate(new Date()).filldata(scomp).save(failOnError:true)
        if (scomp.pf+scomp.debtpf>0) prequestList << new Payrequest(summa:scomp.pf+scomp.debtpf,summands:(scomp.pf+scomp.debtpf)*_ndspercent,paytype:1,paycat:2,tax_id:2,kbkrazdel_id:7).computePaydate(new Date()).filldata(scomp).save(failOnError:true)
        scomp.csiClearIs_noaccount().csiSetPaidstatus(1).save(failOnError:true)
      }
    }
    Salarycomp.findAllByMonthAndYearAndIs_persAndPaidmainstatus(_report.month,_report.year,1,-1).each{ scomp ->
      if(Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(scomp.company_id,1,1).find{it.ibankstatus==1}){
        if(Persaccount.findByPers_idAndModstatusAndIs_main(scomp.pers_id,1,1)){
          //все основные карты
          if (scomp.cardmain>0) prequestList << new Payrequest(summa:scomp.cardmain,summands:0,paytype:1,paycat:3,tax_id:0).computePaydate(new Date()).filldata(scomp).save(failOnError:true)
          scomp.csiClearIs_noaccount().csiSetPaidmainstatus(1).save(failOnError:true)
        } else scomp.csiClearIs_noaccount().save(failOnError:true)
      }
    }
    Salarycomp.findAllByMonthAndYearAndIs_persAndPaidaddstatus(_report.month,_report.year,1,-1).each{ scomp ->
      if(Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(scomp.company_id,1,1).find{it.ibankstatus==1}){
        if(Persaccount.findByPers_idAndModstatusAndIs_main(scomp.pers_id,1,0)){
          //все доп карты
          if (scomp.cardadd>0) prequestList << new Payrequest(summa:scomp.cardadd,summands:0,paytype:1,paycat:3,tax_id:0).computePaydate(new Date()).filldata(scomp,true).save(failOnError:true)
          scomp.csiClearIs_noaccount().csiSetPaidaddstatus(1).save(failOnError:true)
        } else scomp.csiClearIs_noaccount().save(failOnError:true)
      }
    }
    if(prequestList.size()>0){
      prequestList.groupBy{it.fromcompany_id}.each{ lsReq ->
        def oTaskpay = new Taskpay(paygroup:1).csiSetTaskpay(term:lsReq.value[0].paydate.format(DATE_FORMAT),company_id:lsReq.value[0].fromcompany_id,summa:lsReq.value.sum{it.summa}).csiSetInitiator(0).save(flush:true,failOnError:true)
        lsReq.value.each{ it.csiSetTaskpay_id(oTaskpay.id).csiSetModstatus(1).save(flush:true,failOnError:true) }
      }
    }
  }

  void updateOffreportStatus(_report){
    Salarycomp.findAllByMonthAndYearAndIs_persAndIs_noaccountAndPaidstatus(_report.month,_report.year,0,1,0).each{ scomp ->
      if(Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(scomp.company_id,1,1).find{it.ibankstatus==1}){
        scomp.csiClearIs_noaccount().save(failOnError:true)
      }
    }
    Salarycomp.findAllByMonthAndYearAndIs_persAndPaidmainstatus(_report.month,_report.year,1,-1).each{ scomp ->
      if(Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(scomp.company_id,1,1).find{it.ibankstatus==1}){
        //убираем долг у сотрудника
        if(scomp.perstype==1&&scomp.cashsalary>0) {
          User.findByPers_idAndModstatus(scomp.pers_id,1,[sort:'precassadebt',order:'desc'])?.updatePredebt(-scomp.cashsalary)?.save(flush:true,failOnError:true)
          scomp.clearCashsalary()
        }
        if(Persaccount.findByPers_idAndModstatusAndIs_main(scomp.pers_id,1,1)){
          //все основные карты
          scomp.csiClearIs_noaccount().csiSetPaidmainstatus(0).save(failOnError:true)
        } else scomp.csiClearIs_noaccount().save(failOnError:true)
      }
    }
    Salarycomp.findAllByMonthAndYearAndIs_persAndPaidaddstatus(_report.month,_report.year,1,-1).each{ scomp ->
      if(Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(scomp.company_id,1,1).find{it.ibankstatus==1}){
        if(Persaccount.findByPers_idAndModstatusAndIs_main(scomp.pers_id,1,0)){
          scomp.csiClearIs_noaccount().csiSetPaidaddstatus(0).save(failOnError:true)
        } else scomp.csiClearIs_noaccount().save(failOnError:true)
      }
    }
  }

  void processoffreport(){
    Salaryreport.findAllBySalarytype_idAndModstatusLessThan(3,2).each { report ->
      if (report.modstatus==1) createDefferedPayrequests(report)
      else updateOffreportStatus(report)
    }
  }

  ArrayList computeSalary(_report){
    def result = []
    User.findAllByModstatusAndDepartment_idGreaterThanAndPers_idNotInList(1,0,Salarycomp.findAllByMonthAndYearAndIs_persAndPerstypeNotEqual(_report.month,_report.year,1,3).each{ scomp ->
      getSalaryInstance(result,scomp).("setOffPayment${scomp.perstype==1?'Employee':'Directors'}Data")(scomp,scomp.perstype==1?computeEmployeeMonthSalary(scomp.pers_id,scomp.month,scomp.year):0).save(failOnError:true)
    }.collect{it.pers_id}.unique()+[0l]).collect{it.pers_id}.unique().each{
      result << Salary.findOrCreateByMonthAndYearAndPers_id(_report.month,_report.year,it).setOffPaymentEmployeeData(null,computeEmployeeMonthSalary(it,_report.month,_report.year)).save(failOnError:true)
    }
    result
  }

  Salary getSalaryInstance(ArrayList _res, Salarycomp _scomp){
    if(!_res.find{it.pers_id==_scomp.pers_id}) _res << Salary.findOrCreateByMonthAndYearAndPers_id(_scomp.month,_scomp.year,_scomp.pers_id)
    _res.find{it.pers_id==_scomp.pers_id}
  }

  void computeDirSalary(){
    def calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH,-1)
    calendar.set(Calendar.DATE,15)
    def estimateDate = calendar.getTime().clearTime()
    Pers.findAllByPerstypeAndIs_fixactsalary(2,0).each{ pers ->
        def jobstartdates = [:]
        def gdcomplist = Compers.findAllByPers_idAndPosition_idAndModstatus(pers.id,1,1).collect{ jobstartdates[it.company_id] = it.jobstart; it.company_id }.unique()?:[0]
        def gbcomplist = Compers.findAllByPers_idAndPosition_idAndModstatusAndCompany_idNotInList(pers.id,2,1,gdcomplist).collect{it.company_id}.unique()?:[0]
        def agrcomplist = gdcomplist.collect{ companyId -> Kredit.findByClientAndEnddateGreaterThanAndStartdateLessThanAndAdateGreaterThanEquals(companyId,estimateDate,estimateDate,jobstartdates[companyId])||Cession.findByCessionaryAndEnddateGreaterThanAndAdateLessThanAndAdateGreaterThanEqualsAndIs_dirsalary(companyId,estimateDate,estimateDate,jobstartdates[companyId],1)||Lizing.findByArendatorAndEnddateGreaterThanAndAdateLessThanAndAdateGreaterThanEqualsAndIs_dirsalary(companyId,estimateDate,estimateDate,jobstartdates[companyId],1) }
        pers.updateActsalary((gdcomplist-[0]).size()*Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_gd')?.value,5000)+((gbcomplist-[0]).size()>0?Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_gb')?.value,5000):0)+(agrcomplist-false).size()*Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_agr')?.value,5000)).csiSetIsHaveAgr((agrcomplist-false).size()>0?1:0).save(flush:true)
    }
    Pers.findAllByPerstypeAndIs_fixactsalary(2,1).each{ pers ->
        def gdcomplist = Compers.findAllByPers_idAndPosition_idAndModstatus(pers.id,1,1).collect{it.company_id}.unique()?:[0]
        def agrcomplist = gdcomplist.collect{ companyId -> Kredit.findByClientAndEnddateGreaterThanAndStartdateLessThan(companyId,estimateDate,estimateDate)||Cession.findByCessionaryAndEnddateGreaterThanAndAdateLessThan(companyId,estimateDate,estimateDate)||Lizing.findByArendatorAndEnddateGreaterThanAndAdateLessThan(companyId,estimateDate,estimateDate) }
        pers.csiSetIsHaveAgr((agrcomplist-false).size()>0?1:0).save(flush:true)
    }
  }

  Long computeDirSalary(Date _baseDate, Pers pers){
    if (!pers) return 0l
    if (pers.is_fixactsalary==1) return pers.actsalary
    def calendar = Calendar.getInstance()
    calendar.setTime(_baseDate?:new Date())
    calendar.add(Calendar.MONTH,-1)
    calendar.set(Calendar.DATE,15)
    def estimateDate = calendar.getTime().clearTime()
    def jobstartdates = [:]
    def gdcomplist = Compers.findAllByPers_idAndPosition_idAndModstatus(pers.id,1,1).collect{ jobstartdates[it.company_id] = it.jobstart; it.company_id }.unique()?:[0]
    def gbcomplist = Compers.findAllByPers_idAndPosition_idAndModstatusAndCompany_idNotInList(pers.id,2,1,gdcomplist).collect{it.company_id}.unique()?:[0]
    def agrcomplist = gdcomplist.collect{ companyId -> Kredit.findByClientAndEnddateGreaterThanAndStartdateLessThanAndAdateGreaterThanEquals(companyId,estimateDate,estimateDate,jobstartdates[companyId])||Cession.findByCessionaryAndEnddateGreaterThanAndAdateLessThanAndAdateGreaterThanEqualsAndIs_dirsalary(companyId,estimateDate,estimateDate,jobstartdates[companyId],1)||Lizing.findByArendatorAndEnddateGreaterThanAndAdateLessThanAndAdateGreaterThanEqualsAndIs_dirsalary(companyId,estimateDate,estimateDate,jobstartdates[companyId],1) }
    ((gdcomplist-[0]).size()*Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_gd')?.value,5000)+((gbcomplist-[0]).size()>0?Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_gb')?.value,5000):0)+(agrcomplist-false).size()*Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_agr')?.value,5000))
  }

  Integer computeEmployeeMonthSalary(Long _pers_id, Integer _month, Integer _year){
    def reportMonth = new Date(_year-1900,_month-1,1)
    def nextMonth = new Date(_year-1900,_month,1)
    def lsMonthSalaries = Psalary.findAllByPdateBetweenAndPers_id(reportMonth,nextMonth,_pers_id)
    def prevSalary = Psalary.findByPdateLessThanAndPers_id(reportMonth,_pers_id,[sort:'pdate',order:'desc'])
    def monthWorkdaysCount = Tools.computeMonthWorkDays(_month,_year)
    def resultlist = []
    if(prevSalary&&lsMonthSalaries.size()>0){
        resultlist << [sal:prevSalary.actsalary,days:Tools.computeDateIntervalWorkDays(reportMonth,lsMonthSalaries.head().pdate)]
        lsMonthSalaries.eachWithIndex{ it, i -> resultlist << [sal:it.actsalary,days:Tools.computeDateIntervalWorkDays(it.pdate,lsMonthSalaries.getAt(i+1)?.pdate?:nextMonth)] }
    } else if(prevSalary&&lsMonthSalaries.size()==0)
        resultlist << [sal:prevSalary.actsalary,days:monthWorkdaysCount]
    else if(!prevSalary&&lsMonthSalaries.size()>0)
        lsMonthSalaries.eachWithIndex{ it, i -> resultlist << [sal:it.actsalary,days:Tools.computeDateIntervalWorkDays(it.pdate,lsMonthSalaries.getAt(i+1)?.pdate?:nextMonth)] }
    else resultlist << [sal:0,days:monthWorkdaysCount]
    resultlist.sum{ it.sal*it.days/monthWorkdaysCount }.toInteger()
  }

}