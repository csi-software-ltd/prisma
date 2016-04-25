class Actclient {
  static mapping = {
    version false
  }

  Integer id
  Integer agentagr_id
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
  BigDecimal actfixes = 0g
  BigDecimal overfixes = 0g
  BigDecimal agentfix
  Integer modstatus = 0

  Actclient bindPeriod(Date dDate) {
    if (!dDate) dDate = new Date()
    month = dDate.getMonth()+1
    year = dDate.getYear()+1900
    this
  }

  Actclient computeSummas() {
    def curPeriods = Agentkreditplan.findAllByAgentkredit_idInListAndMonthAndYear(Agentkredit.findAllByAgentagr_id(agentagr_id).collect{it.id}?:[0],month,year)
    summa = curPeriods.sum{it.summa}?:0g
    summaprev = Agentkreditplan.findAll{ agentkredit_id in ( Agentkredit.findAllByAgentagr_id(this.agentagr_id).collect{it.id}?:[0] ) && (month < this.month || year < this.year) }.sum{ it.summa }?:0g
    summafix = curPeriods.sum{it.clientdebt}?:0g
    paid = (Payrequest.findAllByAgentagr_idAndPaytypeAndModstatusGreaterThan(agentagr_id,2,1,[sort:'paydate',order:'desc']).sum{ it.clientcommission }?:0g)+(Cash.findAllByAgentagr_idAndAgent_idAndType(agentagr_id,0,2,[sort:'operationdate',order:'desc']).sum{ it.summa }?:0g)
    agentfix = Agentfix.findAllByAgentagr_id(agentagr_id).sum{ it.summa }?:0g
    profit = curPeriods.sum{ it.recieveProfit() }?:0g
    cost = curPeriods.sum{ it.recieveCost() }?:0g
    def prevact = Actclient.findByAgentagr_id(agentagr_id,[sort:'inputdate',order:'desc'])
    profitprev = (prevact?.profit?:0g) + (prevact?.profitprev?:0g)
    costprev = (prevact?.cost?:0g) + (prevact?.costprev?:0g)
    payact(prevact?.overpaid?:0g)
    overpaid((prevact?.overpaid?:0g)-actpaid)
    prevact?.clearoverpaid()?.save()
    fixact(prevact?.actfixes?:0g)
    overfixes((prevact?.overfixes?:0g)-actfixes)
    prevact?.clearoverfixes()?.save()
    this
  }

  Actclient payact(bdSumma){
    actpaid = bdSumma
    if (actpaid>summa+summafix) actpaid = summa + summafix
    this
  }

  Actclient overpaid(bdSumma){
    overpaid += (bdSumma>0?bdSumma:0g)
    this
  }

  Actclient clearoverpaid(){
    overpaid = 0g
    this
  }

  Actclient clearpayact(){
    actpaid = 0g
    this
  }

  Actclient fixact(bdSumma){
    actfixes = bdSumma
    if (actfixes>summa+summafix) actfixes = summa + summafix
    this
  }

  Actclient overfixes(bdSumma){
    overfixes += (bdSumma>0?bdSumma:0g)
    this
  }

  Actclient clearoverfixes(){
    overfixes = 0g
    this
  }

  Actclient clearactfix(){
    actfixes = 0g
    this
  }

  Actclient csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    if(modstatus==1) Agentkreditplan.findAllByAgentkredit_idInListAndMonthAndYearAndModstatusAndParent(Agentkredit.findAllByAgentagr_id(agentagr_id).collect{it.id}?:[0],month,year,0,0).each{ it.topayment().save(flush:true) }
    if(modstatus==0) Agentkreditplan.findAllByAgentkredit_idInListAndMonthAndYearAndModstatusAndParent(Agentkredit.findAllByAgentagr_id(agentagr_id).collect{it.id}?:[0],month,year,1,0).each{ it.revertpayment().save(flush:true) }
    this
  }

  Boolean isnotpaid(){
    return actpaid != summa + summafix
  }

  Boolean isnotfixes(){
    return actfixes != summa + summafix
  }

  BigDecimal csiGetfixesSum(){
    summa + summafix - actfixes
  }
}