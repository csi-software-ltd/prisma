class Actagent {
  static mapping = {
    version false
  }

  Integer id
  Integer agentagr_id
  Integer agent_id
  Integer month
  Integer year
  Date inputdate = new Date()
  BigDecimal summa
  BigDecimal profit
  BigDecimal cost
  BigDecimal summaprev
  BigDecimal profitprev
  BigDecimal costprev
  BigDecimal summafix
  BigDecimal paid
  BigDecimal actpaid = 0g
  BigDecimal overpaid = 0g
  BigDecimal agentfix
  Integer modstatus = 0
  Integer is_report = 0

  Actagent bindPeriod(Date dDate) {
    if (!dDate) dDate = new Date()
    month = dDate.getMonth()+1
    year = dDate.getYear()+1900
    this
  }

  Actagent csiSetOfficial() {
    is_report = 0
    new AgentrateplanSearch().csiSelectCurrentAgentratesForActagent(agentagr_id,agent_id,month,year).each{ plan ->
      Agentratekreditplan.get(plan.plan_id)?.csiSetOfficial()?.save(flush:true)
    }
    this
  }

  Actagent computeSummas() {
    def curPeriods = new AgentrateplanSearch().csiSelectCurrentAgentratesForActagent(agentagr_id,agent_id,month,year)
    summa = curPeriods.sum{ it.computeAgentSumma() }?:0g
    summaprev = new AgentrateplanSearch().csiSelectPreviousAgentrates(agentagr_id,agent_id,month,year).sum{ it.computeAgentSumma() }?:0g
    summafix = curPeriods.sum{ it.computeClientdebtAgentSumma() }?:0g
    paid = (Payrequest.findAllByAgentagr_idAndPaytypeAndAgent_idAndModstatusGreaterThan(agentagr_id,1,agent_id,1,[sort:'paydate',order:'desc']).sum{ it.summa }?:0g)+(Payrequest.findAllByAgentagr_idAndPaytypeAndAgent_idAndModstatusGreaterThan(agentagr_id,6,agent_id,0,[sort:'paydate',order:'desc']).sum{ it.agentcommission }?:0g)+(Cash.findAllByAgentagr_idAndAgent_id(agentagr_id,agent_id,[sort:'operationdate',order:'desc']).sum{ it.type==1?it.summa:-it.summa }?:0g)
    agentfix = Agentfix.findAllByAgentagr_idAndAgent_id(agentagr_id,agent_id).sum{ it.summa }?:0g

    def profitPeriods = Agentratekreditplan.findAllByAgentkredit_idInListAndMonthAndYear(Agentkredit.findAllByAgentagr_id(agentagr_id).collect{it.id}?:[0],month,year)
    profit = profitPeriods.sum{ it.recieveProfit() }?:0g
    cost = profitPeriods.sum{ it.recieveCost() }?:0g

    def prevact = Actagent.findAll{ agentagr_id == this.agentagr_id && (month < this.month || year < this.year) }.sort{ new Date(year-1900,month-1,1) }.reverse(true)[0]
    profitprev = (prevact?.profit?:0g) + (prevact?.profitprev?:0g)
    costprev = (prevact?.cost?:0g) + (prevact?.costprev?:0g)
    if (!is_report) {
      payact(prevact?.overpaid?:0g)
      overpaid((prevact?.overpaid?:0g)-actpaid)
      prevact?.clearoverpaid()?.save()
    }
    this
  }

  Actagent csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Actagent payact(bdSumma){
    actpaid = bdSumma
    if (actpaid>summa+summafix) actpaid = summa + summafix
    this
  }

  Actagent overpaid(bdSumma){
    overpaid += (bdSumma>0?bdSumma:0g)
    this
  }

  Actagent clearoverpaid(){
    overpaid = 0g
    this
  }

  Actagent clearpayact(){
    actpaid = 0g
    this
  }

}