import grails.gorm.*
class AgentKreditService {

  void updateAgentagrPeriod(int agr_id, Date baseDate){
    def maxenddate = Calendar.getInstance()
    def enddate = Calendar.getInstance()
    def startdate = Calendar.getInstance()
    Agentkredit.findAllByAgentagr_id(agr_id).each{ akr ->
      def query = new DetachedCriteria(Agentkreditplan).build { eq('agentkredit_id',akr.id) or { lt('year',baseDate.getYear()+1900) le('month',baseDate.getMonth()+1) } }
      def lastperiod = query.list(sort:'dateend',order:'desc',max:1)[0]
      startdate.setTime(lastperiod?.dateend?lastperiod?.dateend+1:Kredit.get(akr.kredit_id).startdate.clone())
      maxenddate.setTime(baseDate.clearTime())
      maxenddate.add(Calendar.MONTH,akr.calcperiod==0?1:akr.calcperiod==1?-1:akr.calcperiod==2?3:0)
      Kredit kredit = Kredit.get(akr.kredit_id)
      if(maxenddate.getTime()>=kredit.enddate) maxenddate.setTime(kredit.enddate)
      if(lastperiod?.isSameDate(baseDate)||startdate.getTime()>=maxenddate.getTime()){
        if (lastperiod?.modstatus==0) lastperiod?.calculateSum(startdate,Agentkreditplan.createCriteria().list(sort:'dateend',order:'desc') { eq('agentkredit_id',akr.id) ne('id',lastperiod.id) or { lt('year',lastperiod.year) le('month',lastperiod.month) } }.sum{ it.recalculatePeriod(maxenddate) }?:0g)?.save(failOnError:true)
      } else {
        if (akr.calcperiod==3) {
          enddate.setTime(baseDate.clearTime())
          enddate.set(Calendar.DAY_OF_MONTH,enddate.getActualMaximum(Calendar.DAY_OF_MONTH))
        } else {
          if(!lastperiod) enddate.setTime(maxenddate.getTime())
          else {
            enddate.setTime(startdate.getTime())
            enddate.add(Calendar.MONTH,akr.calcperiod==0?1:akr.calcperiod==1?-1:3)
            enddate.add(Calendar.DATE,-1)
          }
        }
        if(enddate.getTime()>kredit.enddate) enddate.setTime(kredit.enddate)
        lastperiod?.closePeriod()?.save(failOnError:true)
        akr.updateCalcdate(new Agentkreditplan(agentkredit_id:akr.id,vrate:kredit.getvRate(),calcrate:lastperiod?lastperiod.calcrate:akr.rate,calccost:lastperiod?lastperiod.calccost:akr.cost).bindPeriod(baseDate).setDates(startdate.getTime(),enddate.getTime()).calculateSum(startdate,query.list(sort:'dateend',order:'desc').sum{ it.recalculatePeriod(maxenddate) }?:0g).save(failOnError:true)?.dateend).save(failOnError:true)
      }
    }
  }

  void updateAgentratePeriod(int agr_id, Date baseDate){
    def enddate = Calendar.getInstance()
    def startdate = Calendar.getInstance()
    Agentkredit.findAllByAgentagr_id(agr_id).each{ akr ->
      def query = new DetachedCriteria(Agentratekreditplan).build { eq('agentkredit_id',akr.id) or { lt('year',baseDate.getYear()+1900) le('month',baseDate.getMonth()+1) } }
      def lastperiod = query.list(sort:'id',order:'desc',max:1)[0]
      startdate.setTime(lastperiod?.dateend?lastperiod?.dateend+1:Kredit.get(akr.kredit_id).startdate.clone())
      enddate.setTime(baseDate.clearTime())
      enddate.set(Calendar.DAY_OF_MONTH,enddate.getActualMaximum(Calendar.DAY_OF_MONTH))
      Kredit kredit = Kredit.get(akr.kredit_id)
      if(enddate.getTime()>kredit.enddate) enddate.setTime(kredit.enddate)
      if(lastperiod?.isSameDate(baseDate)||startdate.getTime()>=enddate.getTime()){
        if (lastperiod?.modstatus==0) lastperiod?.calculateSum(startdate,Agentratekreditplan.createCriteria().list(sort:'dateend',order:'desc') { eq('agentkredit_id',akr.id) ne('id',lastperiod.id) or { lt('year',baseDate.getYear()+1900) le('month',baseDate.getMonth()+1) } }.sum{ it.recalculatePeriod(startdate) }?:0g)?.save(failOnError:true,flush:true)
      } else {
        lastperiod?.closePeriod()?.save(failOnError:true)
        def akrplan = new Agentratekreditplan(agentkredit_id:akr.id,vrate:kredit.getvRate(),calcrate:lastperiod?lastperiod.calcrate:akr.rate,calccost:lastperiod?lastperiod.calccost:akr.cost).bindPeriod(baseDate).setDates(startdate.getTime(),enddate.getTime()).calculateSum(startdate,query.list(sort:'dateend',order:'desc').sum{ it.recalculatePeriod(startdate) }?:0g).save(failOnError:true,flush:true)
        if (akrplan)
          (lastperiod?Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_id(akr.id,lastperiod.id):Agentrate.findAllByAgentkredit_id(akr.id)).each{ arate ->
            new Agentrateforperiods().setMainData(arate.properties).csiSetPlanId(akrplan.id).save(failOnError:true)
          }
      }
    }
  }

  void computeclientact(int agr_id){
    Agentkreditplan.findAllByAgentkredit_idInListAndModstatus(Agentkredit.findAllByAgentagr_id(agr_id).collect{ it.id }?:[0],0).collect{ new Date(it.year-1900,it.month-1,1)}.unique().sort().each{
      if(!Actclient.findByAgentagr_idAndMonthAndYear(agr_id,it.getMonth()+1,it.getYear()+1900)) new Actclient(agentagr_id:agr_id).bindPeriod(it).computeSummas().save(flush:true,failOnError:true)
    }
  }

  void computeagentact(int agr_id, Date baseDate){
    updateAgentratePeriod(agr_id,baseDate)
    Agentrate.findAllByAgentkredit_idInList(Agentkredit.findAllByAgentagr_id(agr_id).collect{ it.id }?:[0]).collect{ it.agent_id }.unique().each{ agent_id ->
      if(!Actagent.findByAgentagr_idAndAgent_idAndMonthAndYearAndIs_report(agr_id,agent_id,baseDate.getMonth()+1,baseDate.getYear()+1900,0)) Actagent.findOrCreateWhere(agentagr_id:agr_id,agent_id:agent_id,is_report:1,month:baseDate.getMonth()+1,year:baseDate.getYear()+1900).csiSetOfficial().computeSummas().save(failOnError:true)
    }
    updateagentactpaidsum(agr_id)
  }

  void computereportagentact(int agr_id, Date baseDate){
    updateAgentratePeriod(agr_id,baseDate)
    Agentrate.findAllByAgentkredit_idInList(Agentkredit.findAllByAgentagr_id(agr_id).collect{ it.id }?:[0]).collect{ it.agent_id }.unique().each{ agent_id ->
      if(!Actagent.findByAgentagr_idAndAgent_idAndMonthAndYearAndIs_report(agr_id,agent_id,baseDate.getMonth()+1,baseDate.getYear()+1900,0)) Actagent.findOrCreateWhere(agentagr_id:agr_id,agent_id:agent_id,is_report:1,month:baseDate.getMonth()+1,year:baseDate.getYear()+1900).computeSummas().save(failOnError:true)
    }
  }

  void updateclientactpaidsum(){
    Actclient.list().each{ it.clearpayact().clearoverpaid().save(flush:true) }
    Payrequest.findAllByPaytypeAndModstatusGreaterThanAndAgentagr_idGreaterThanAndAgent_id(2,1,0,0).groupBy{it.agentagr_id}.each{
      def paidsum = it.value.sum{it.clientcommission}
      def actlist = Actclient.findAllByAgentagr_id(it.key,[sort:'inputdate',order:'asc']).each{
        paidsum += it.actpaid
        paidsum -= it.payact(paidsum).save(flush:true).actpaid
      }
      !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
    }
    Cash.findAllByAgentagr_idGreaterThanAndAgent_idAndType(0,0,2).groupBy{it.agentagr_id}.each{
      def paidsum = it.value.sum{ it.summa }
      def actlist = Actclient.findAllByAgentagr_id(it.key,[sort:'inputdate',order:'asc']).each{
        paidsum += it.actpaid
        paidsum -= it.payact(paidsum).save(flush:true).actpaid
      }
      !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
    }
    Agentfix.list().groupBy{it.agentagr_id}.each{
      def paidsum = it.value.sum{it.summa}
      def actlist = Actclient.findAllByAgentagr_id(it.key,[sort:'inputdate',order:'asc']).each{
        paidsum += it.actpaid
        paidsum -= it.payact(paidsum).save(flush:true).actpaid
      }
      !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
    }
  }

  void updateclientactpaidsum(int agr_id){
    Actclient.findAllByAgentagr_id(agr_id).each{ it.clearpayact().clearoverpaid().save(flush:true) }
    Payrequest.findAllByPaytypeAndModstatusGreaterThanAndAgentagr_idAndAgent_id(2,1,agr_id,0).groupBy{it.agentagr_id}.each{
      def paidsum = it.value.sum{it.clientcommission}
      def actlist = Actclient.findAllByAgentagr_id(it.key,[sort:'inputdate',order:'asc']).each{
        paidsum += it.actpaid
        paidsum -= it.payact(paidsum).save(flush:true).actpaid
      }
      !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
    }
    Cash.findAllByAgentagr_idAndAgent_idAndType(agr_id,0,2).groupBy{it.agentagr_id}.each{
      def paidsum = it.value.sum{ it.summa }
      def actlist = Actclient.findAllByAgentagr_id(it.key,[sort:'inputdate',order:'asc']).each{
        paidsum += it.actpaid
        paidsum -= it.payact(paidsum).save(flush:true).actpaid
      }
      !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
    }
    Agentfix.findAllByAgentagr_id(agr_id).groupBy{it.agentagr_id}.each{
      def paidsum = it.value.sum{it.summa}
      def actlist = Actclient.findAllByAgentagr_id(it.key,[sort:'inputdate',order:'asc']).each{
        paidsum += it.actpaid
        paidsum -= it.payact(paidsum).save(flush:true).actpaid
      }
      !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
    }
  }

  void updateclientactfixsum(){
    Actclient.list().each{ it.clearactfix().clearoverfixes().save(flush:true) }
    Payrequest.findAllByPaytypeAndAgentagr_idGreaterThanAndAgent_idAndModstatusGreaterThan(4,0,0,-1).groupBy{it.agentagr_id}.each{
      def paidsum = it.value.sum{it.summa}
      def actlist = Actclient.findAllByAgentagr_id(it.key,[sort:'inputdate',order:'asc']).each{
        paidsum += it.actfixes
        paidsum -= it.fixact(paidsum).save(flush:true).actfixes
      }
      !actlist?:actlist.last().overfixes(paidsum).save(flush:true)
    }
  }

  void updateclientactfixsum(int agr_id){
    Actclient.findAllByAgentagr_id(agr_id).each{ it.clearactfix().clearoverfixes().save(flush:true) }
    Payrequest.findAllByPaytypeAndAgentagr_idAndAgent_idAndModstatusGreaterThan(4,agr_id,0,-1).groupBy{it.agentagr_id}.each{
      def paidsum = it.value.sum{it.summa}
      def actlist = Actclient.findAllByAgentagr_id(it.key,[sort:'inputdate',order:'asc']).each{
        paidsum += it.actfixes
        paidsum -= it.fixact(paidsum).save(flush:true).actfixes
      }
      !actlist?:actlist.last().overfixes(paidsum).save(flush:true)
    }
  }

  void updateagentactpaidsum(){
    Actagent.list().each{ it.clearpayact().clearoverpaid().save(flush:true) }
    Payrequest.findAllByPaytypeAndModstatusGreaterThanAndAgent_idGreaterThanAndAgentagr_idGreaterThan(1,1,0,0).groupBy{it.agentagr_id}.each{ requestlist ->
      requestlist.value.groupBy{it.agent_id}.each{
        def paidsum = it.value.sum{it.summa}
        def actlist = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(requestlist.key,it.key,0,[sort:'inputdate',order:'asc']).each{
          paidsum += it.actpaid
          paidsum -= it.payact(paidsum).save(flush:true).actpaid
        }
        !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
      }
    }
    Payrequest.findAllByPaytypeAndModstatusGreaterThanAndAgent_idGreaterThanAndAgentagr_idGreaterThan(6,0,0,0).groupBy{it.agentagr_id}.each{ requestlist ->
      requestlist.value.groupBy{it.agent_id}.each{
        def paidsum = it.value.sum{it.agentcommission}
        def actlist = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(requestlist.key,it.key,0,[sort:'inputdate',order:'asc']).each{
          paidsum += it.actpaid
          paidsum -= it.payact(paidsum).save(flush:true).actpaid
        }
        !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
      }
    }
    Cash.findAllByAgentagr_idGreaterThanAndAgent_idGreaterThan(0,0).groupBy{it.agentagr_id}.each{ cashlist ->
      cashlist.value.groupBy{it.agent_id}.each{
        def paidsum = it.value.sum{ it.type==1?it.summa:-it.summa }
        def actlist = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(cashlist.key,it.key,0,[sort:'inputdate',order:'asc']).each{
          paidsum += it.actpaid
          paidsum -= it.payact(paidsum).save(flush:true).actpaid
        }
        !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
      }
    }
    Agentfix.list().groupBy{it.agentagr_id}.each{ fixlist ->
      fixlist.value.groupBy{it.agent_id}.each{
        def paidsum = it.value.sum{it.summa}
        def actlist = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(fixlist.key,it.key,0,[sort:'inputdate',order:'asc']).each{
          paidsum += it.actpaid
          paidsum -= it.payact(paidsum).save(flush:true).actpaid
        }
        !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
      }
    }
  }

  void updateagentactpaidsum(int agr_id){
    Actagent.findAllByAgentagr_id(agr_id).each{ it.clearpayact().clearoverpaid().save(flush:true) }
    Payrequest.findAllByPaytypeAndModstatusGreaterThanAndAgent_idGreaterThanAndAgentagr_id(1,1,0,agr_id).groupBy{it.agentagr_id}.each{ requestlist ->
      requestlist.value.groupBy{it.agent_id}.each{
        def paidsum = it.value.sum{it.summa}
        def actlist = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(requestlist.key,it.key,0,[sort:'inputdate',order:'asc']).each{
          paidsum += it.actpaid
          paidsum -= it.payact(paidsum).save(flush:true).actpaid
        }
        !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
      }
    }
    Payrequest.findAllByPaytypeAndModstatusGreaterThanAndAgent_idGreaterThanAndAgentagr_id(6,0,0,agr_id).groupBy{it.agentagr_id}.each{ requestlist ->
      requestlist.value.groupBy{it.agent_id}.each{
        def paidsum = it.value.sum{it.agentcommission}
        def actlist = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(requestlist.key,it.key,0,[sort:'inputdate',order:'asc']).each{
          paidsum += it.actpaid
          paidsum -= it.payact(paidsum).save(flush:true).actpaid
        }
        !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
      }
    }
    Cash.findAllByAgentagr_idAndAgent_idGreaterThan(agr_id,0).groupBy{it.agentagr_id}.each{ cashlist ->
      cashlist.value.groupBy{it.agent_id}.each{
        def paidsum = it.value.sum{ it.type==1?it.summa:-it.summa }
        def actlist = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(cashlist.key,it.key,0,[sort:'inputdate',order:'asc']).each{
          paidsum += it.actpaid
          paidsum -= it.payact(paidsum).save(flush:true).actpaid
        }
        !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
      }
    }
    Agentfix.findAllByAgentagr_id(agr_id).groupBy{it.agentagr_id}.each{ fixlist ->
      fixlist.value.groupBy{it.agent_id}.each{
        def paidsum = it.value.sum{it.summa}
        def actlist = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(fixlist.key,it.key,0,[sort:'inputdate',order:'asc']).each{
          paidsum += it.actpaid
          paidsum -= it.payact(paidsum).save(flush:true).actpaid
        }
        !actlist?:actlist.last().overpaid(paidsum).save(flush:true)
      }
    }
  }

  BigDecimal computeKreditDebt(_kredit){
    BigDecimal kredsumma = 0.0g
    BigDecimal paid = 0.0g
    if (!_kredit) return 0.0g
    if (_kredit.kredtype==1) kredsumma = _kredit.startsaldodate?_kredit.startsumma:_kredit.summa
    else if (_kredit.kredtype==3) kredsumma = _kredit.summa + (Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeAndModstatusGreaterThanAndPaydateGreaterThanEquals(3,_kredit.id,0,1,-1,_kredit.startsaldodate?:new Date(1,0,1)).sum{it.summa}?:0)
    else kredsumma = (_kredit.startsaldodate?_kredit.startsumma:0)+(Kreditline.findAllByKredit_idAndModstatusAndPaydateGreaterThanEquals(_kredit.id,1,_kredit.startsaldodate?:new Date(1,0,1)).sum{it.summa}?:0)

    if (_kredit.kredtype==3) paid = (_kredit.startsaldodate?_kredit.startsumma:_kredit.summa) + (Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeAndModstatusGreaterThanAndPaydateGreaterThanEquals(3,_kredit.id,0,2,-1,_kredit.startsaldodate?:new Date(1,0,1)).sum{it.summa}?:0)
    else paid = Kreditpayment.findAllByKredit_idAndPaidstatusGreaterThanAndPaydateGreaterThanEquals(_kredit.id,0,_kredit.startsaldodate?:new Date(1,0,1)).sum{it.summa}?:0
    kredsumma - paid
  }

  BigDecimal computeLizingDebt(_lizing){
    BigDecimal debt = 0.0g
    if (!_lizing) return debt
    if (_lizing.startsaldodate)
      debt = _lizing.startsaldo - (Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusAndPaydateGreaterThanAndPaytypeInList(4,_lizing.id,3,_lizing.startsaldodate,[1,3]).sum{it.summa}?:0)
    else 
      debt = _lizing.summa - (Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusAndPaytypeInList(4,_lizing.id,3,[1,3]).sum{it.summa}?:0)
    debt
  }

  BigDecimal computeDepositCurSumma(_deposit){
    BigDecimal summa = 0.0g
    if (!_deposit) return summa
    if (_deposit.startsaldodate)
      summa = _deposit.startsumma + (Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusAndIs_dopAndPaydateGreaterThan(11,_deposit.id,1,3,0,_deposit.startsaldodate).sum{it.summa}?:0.0g) - (Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusAndIs_dopAndPaydateGreaterThan(11,_deposit.id,2,3,0,_deposit.startsaldodate).sum{it.summa}?:0.0g)
    else 
      summa = (Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusGreaterThanAndIs_dop(11,_deposit.id,1,3,0).sum{it.summa}?:0.0g) - (Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusGreaterThanAndIs_dop(11,_deposit.id,2,3,0).sum{it.summa}?:0.0g)
    summa
  }

  BigDecimal computeOverdraftPercentByDate(Kredit _kredit, Date _percDate, Date _startDate){
    def cal = Calendar.getInstance()
    def basedate = Payrequest.findByAgreementtype_idAndAgreement_idAndPaytypeAndIs_dopAndModstatusGreaterThanAndPaydateGreaterThanEqualsAndPaydateLessThan(3,_kredit.id,1,1,-1,_startDate?:new Date(1,0,1),_percDate,[sort:'paydate',order:'desc'])?.paydate?:_startDate?:_kredit.startdate
    cal.setTime(basedate)
    cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    def payments = Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeInListAndModstatusGreaterThanAndPaydateGreaterThanEqualsAndPaydateLessThan(3,_kredit.id,0,[1,2],-1,basedate?:new Date(1,0,1),_percDate,[sort:'paydate',order:'asc']).collect{[paydate:it.paydate,summa:(it.paytype==1?-it.summa:it.summa)]}
    def result = 0.0g
    def kredsumma = (_kredit.startsaldodate?_kredit.startsumma:_kredit.summa)+(Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeInListAndModstatusGreaterThanAndPaydateLessThanAndPaydateGreaterThanEquals(3,_kredit.id,0,[1,2],-1,basedate?:new Date(1,0,1),_kredit.startsaldodate?:new Date(1,0,1)).sum{(it.paytype==1?-it.summa:it.summa)}?:0.0g)
    while(cal.getTime()<_percDate){
      payments << [paydate:cal.getTime(),summa:0.0g]
      cal.add(Calendar.DAY_OF_MONTH,1)
      cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    }
    payments << [paydate:_percDate,summa:0.0g]
    cal.setTime(basedate)
    payments.sort{it.paydate}.groupBy{it.paydate}.each{
      result += computepercent(kredsumma,cal.getActualMaximum(Calendar.DAY_OF_YEAR),it.key-cal.getTime(),_kredit.rate)
      kredsumma += it.value.sum{it.summa}
      cal.setTime(it.key)
    }
    result
  }

  def computepercent(_recievedsum, _yearlength, _monthlength, _rate){
    return ((_recievedsum*_rate/100/_yearlength*_monthlength)>0?(_recievedsum*_rate/100/_yearlength*_monthlength):0)
  }

  BigDecimal computeKreditPercentByDate(Integer _kreditId, Date _percDate, Date _startDate){
    Kredit _kredit = Kredit.get(_kreditId)
    if (!_kredit) return 0.0g
    else if (_percDate>_kredit.enddate) return 0.0g
    else if (_kredit.kredtype==3) return computeOverdraftPercentByDate(_kredit, _percDate, _startDate)
    else if (!Kreditpayment.findAllByKredit_id(_kredit.id)) return 0.0g
    else if (Kreditpayment.findAllByKredit_idAndPercpaidstatusGreaterThanAndPaydateGreaterThanEquals(_kredit.id,0,_percDate)) return 0.0g

    return (Kreditpayment.findAllByKredit_idAndPercpaidstatusAndPaydateLessThanEqualsAndPaydateGreaterThanEquals(_kredit.id,0,_percDate,_startDate?:new Date(1,0,1)).sum{it.summaperc}?:0) + computePercentByPeriod(_kredit,Kreditpayment.findByKredit_idAndPaydateLessThanEqualsAndPaydateGreaterThanEquals(_kredit.id,_percDate,_startDate?:new Date(1,0,1),[sort:'paydate',order:'desc']),_percDate,_startDate)
  }

  BigDecimal computePercentByPeriod(Kredit _kredit, Kreditpayment _payment, Date _percDate, Date _startDate){
    def cal = Calendar.getInstance()
    def cal2 = Calendar.getInstance()
    def basedate = _payment?.paydate?:_startDate?:_kredit.startdate
    def incomingpayment = Kreditline.findAllByKredit_idAndModstatusGreaterThanEqualsAndPaydateGreaterThan(_kredit.id,0,basedate,[sort:'paydate',order:'asc'])
    def paidsum = Kreditpayment.findAllByKredit_idAndPaydateLessThanEquals(_kredit.id,basedate).sum{it.summa}?:0
    def recievedsum = _kredit.kredtype==1 ? (_kredit.summa-paidsum) : ((Kreditline.findAllByKredit_idAndModstatusGreaterThanEqualsAndPaydateLessThanEquals(_kredit.id,0,basedate).sum{it.summa}?:_kredit.summa)-paidsum)
    incomingpayment += Kreditpayment.findAllByKredit_idAndPaydateGreaterThanAndIs_auto(_kredit.id,basedate,0,[sort:'paydate',order:'asc']).collect{[paydate:it.paydate,summa:-it.summa]}
    def perssum = 0
    cal.setTime(_percDate)
    cal2.setTime(basedate)
    Boolean isNext = cal2.getTime().getMonth()!=cal.getTime().getMonth()
    _kredit.computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),(isNext?cal2.getActualMaximum(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),isNext?cal.get(Calendar.DAY_OF_MONTH):0,0)
  }

  def computeFinLizingBalance(_finlizing){
    def result = [bodydebt:0.0g,balance:0.0g]
    if (!_finlizing) return result
    result.bodydebt = _finlizing.summa
    Finlizperiod.findAllByFinlizing_id(_finlizing.id,[sort:'fmonth',order:'asc']).each{ period ->
      def percent = result.bodydebt*0.01/365*period.qdays
      result.balance += period.compensation+percent-period.returnsumma-(Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeInListAndModstatusGreaterThanAndIs_dopAndPlatperiod(12,_finlizing.id,[1,2],-1,1,String.format('%tm.%<tY',period.fmonth)).sum{ it.paytype==1?it.summa:-it.summa }?:0.0g)
      result.bodydebt -= period.body
    }
    result
  }

  def computeIndepositBody(_indeposit){
    if (!_indeposit) return 0.0g
    if (_indeposit.aclass==1)
      _indeposit.startsaldo + (Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeInList(13,_indeposit.id,[1,2]).sum{ it.paytype==1?-it.depbody:it.depbody }?:0)
    else
      _indeposit.startsaldo + (Cash.findAllByIndeposit_idAndCashclass(_indeposit.id,18).sum{ it.type==1?-it.summa:it.summa }?:0)
  }

  def computeIndepositPercent(_indeposit){
    if (!_indeposit) return [percdebt:0.0g,percdate:null]
    def cal = Calendar.getInstance()
    def today = new Date()
    if (_indeposit.aclass==1){
      def percdate = Payrequest.findAll(sort:'paydate',order:'desc',max:1){ agreementtype_id==13 && agreement_id==_indeposit.id && paytype==1 && depprc>0 }[0]?.paydate?:_indeposit.adate
      def basesumma = _indeposit.startsaldo + (Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeInListAndPaydateLessThan(13,_indeposit.id,[1,2],percdate).sum{ it.paytype==1?-it.depbody:it.depbody }?:0)
      cal.setTime(percdate)
      cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
      def payments = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeInListAndPaydateGreaterThanEquals(13,_indeposit.id,[1,2],percdate,[sort:'paydate',order:'asc']).collect{[paydate:it.paydate,summa:(it.paytype==1?-it.depbody:it.depbody)]}
      def result = 0.0g
      while(cal.getTime()<today){
        payments << [paydate:cal.getTime(),summa:0.0g]
        cal.add(Calendar.DAY_OF_MONTH,1)
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
      }
      payments << [paydate:today,summa:0.0g]
      cal.setTime(percdate)
      payments.sort{it.paydate}.groupBy{it.paydate}.each{
        result += computepercent(basesumma,cal.getActualMaximum(Calendar.DAY_OF_YEAR),it.key-cal.getTime(),_indeposit.rate)
        basesumma += it.value.sum{it.summa}
        cal.setTime(it.key)
      }
      [percdebt:result,percdate:percdate]
    } else {
      def percdate = Cash.findByIndeposit_idAndCashclass(_indeposit.id,19,[sort:'operationdate',order:'desc'])?.operationdate?:_indeposit.adate
      def basesumma = _indeposit.startsaldo + (Cash.findAllByIndeposit_idAndCashclassAndOperationdateLessThan(_indeposit.id,18,percdate).sum{ it.type==1?-it.summa:it.summa }?:0)
      cal.setTime(percdate)
      cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
      def payments = Cash.findAllByIndeposit_idAndCashclassAndOperationdateGreaterThanEquals(_indeposit.id,18,percdate,[sort:'operationdate',order:'asc']).collect{[paydate:it.operationdate,summa:(it.type==1?-it.summa:it.summa)]}
      def result = 0.0g
      while(cal.getTime()<today){
        payments << [paydate:cal.getTime(),summa:0.0g]
        cal.add(Calendar.DAY_OF_MONTH,1)
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
      }
      payments << [paydate:today,summa:0.0g]
      cal.setTime(percdate)
      payments.sort{it.paydate}.groupBy{it.paydate}.each{
        result += computepercent(basesumma,cal.getActualMaximum(Calendar.DAY_OF_YEAR),it.key-cal.getTime(),_indeposit.rate)
        basesumma += it.value.sum{it.summa}
        cal.setTime(it.key)
      }
      [percdebt:result,percdate:percdate]
    }
  }

  BigDecimal computeIndepositProjectPercent(_project,_indeposit,Date _date = null){
    BigDecimal result = 0.0g
    if (!_project||!_indeposit) return result
    def percdate = Indepositproject.findByIndeposit_idAndProject_idAndIs_percent(_indeposit.id,_project.id,1,[sort:'operationdate',order:'desc'])?.operationdate?:_project.is_main?_indeposit.adate:Indepositproject.findByIndeposit_idAndProject_idAndIs_percent(_indeposit.id,_project.id,0,[sort:'operationdate',order:'asc'])?.operationdate
    if (!percdate) return result
    def today = _date?:new Date()
    def basesumma = (_project.is_main?_indeposit.startsaldo:0.0g) + (Indepositproject.findAllByIndeposit_idAndProject_idAndIs_percentAndOperationdateLessThan(_indeposit.id,_project.id,0,percdate).sum{ it.summa }?:0.0g)
    def payments =  Indepositproject.findAllByIndeposit_idAndProject_idAndIs_percentAndOperationdateGreaterThanEqualsAndOperationdateLessThanEquals(_indeposit.id,_project.id,0,percdate,today,[sort:'operationdate',order:'asc']).collect{[paydate:it.operationdate,summa:it.summa]}
    def cal = Calendar.getInstance()
    cal.setTime(percdate)
    cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    while(cal.getTime()<today){
      payments << [paydate:cal.getTime(),summa:0.0g]
      cal.add(Calendar.DAY_OF_MONTH,1)
      cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    }
    payments << [paydate:today,summa:0.0g]
    cal.setTime(percdate)
    payments.sort{it.paydate}.groupBy{it.paydate}.each{
      result += computepercent(basesumma,cal.getActualMaximum(Calendar.DAY_OF_YEAR),it.key-cal.getTime(),_indeposit.rate)
      basesumma += it.value.sum{it.summa}
      cal.setTime(it.key)
    }
    result
  }

  def computeSpaceDebt(_space){
    [maindebt:computeSpaceMaindebt(_space),dopdebt:computeSpaceDopdebt(_space)]
  }

  BigDecimal computeSpaceMaindebt(_space){
    if (!_space) return 0.0g
    (Spacecalculation.findAllBySpace_idAndIs_dop(_space.id,0).sum{ it.summa }?:0.0g)-(Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeAndModstatusGreaterThan(2,_space.id,0,1,1).sum{ it.summa }?:0.0g)
  }

  BigDecimal computeSpaceDopdebt(_space){
    if (!_space) return 0.0g
    (Spacecalculation.findAllBySpace_idAndIs_dop(_space.id,1).sum{ it.summa }?:0.0g)-(Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeAndModstatusGreaterThan(2,_space.id,1,1,1).sum{ it.summa }?:0.0g)
  }

  BigDecimal computeServiceMaindebt(_service){
    if (!_service) return 0.0g
    (Servicecalculation.findAllByService_id(_service.id).sum{ it.summa }?:0.0g)-(Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusGreaterThan(8,_service.id,1,1).sum{ it.summa }?:0.0g)
  }
}