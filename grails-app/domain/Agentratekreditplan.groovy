class Agentratekreditplan {
  static mapping = {
    version false
  }

  Integer id
  Integer agentkredit_id
  Integer month
  Integer year
  BigDecimal debt
  BigDecimal clientdebt
  Date datestart
  Date dateend
  Date inputdate = new Date()
  Integer modstatus = 0
  BigDecimal summa
  Float calcrate
  Float calccost
  BigDecimal vrate = 1g
  Integer is_last = 1
  Integer is_report = 1

  def beforeDelete(){
    Agentratekreditplan.findByAgentkredit_idAndIdNotEqual(agentkredit_id,id,[sort:'dateend',order:'asc'])?.csiSetLast()?.save()
  }

  Agentratekreditplan closePeriod() {
    is_last = 0
    this
  }

  Agentratekreditplan csiSetLast() {
    is_last = 1
    this
  }

  Agentratekreditplan topayment() {
    modstatus = modstatus?:1
    this
  }

  Agentratekreditplan revertpayment() {
    modstatus = modstatus==1?0:modstatus
    this
  }

  Agentratekreditplan csiSetOfficial() {
    is_report = 0
    this
  }

  Agentratekreditplan updateCalcrate(Float _rate) {
    if (is_report) csiSetCalcrate(_rate)
    this
  }

  Agentratekreditplan csiSetCalcrate(Float _rate) {
    calcrate = _rate?:0f
    this
  }

  Agentratekreditplan updateCalccost(Float _cost) {
    if (is_report) csiSetCalccost(_cost)
    this
  }

  Agentratekreditplan csiSetCalccost(Float _cost) {
    calccost = _cost?:0f
    this
  }

  Agentratekreditplan setDates(Date _start, Date _end) {
    if (is_last) {
      datestart = _start
      Kredit kredit = Kredit.get(Agentkredit.get(agentkredit_id).kredit_id)
      dateend = _end<kredit.enddate?_end:kredit.enddate
    }
    this
  }

  Agentratekreditplan bindPeriod(Date dDate) {
    if (!dDate) dDate = new Date()
    month = dDate.getMonth()+1
    year = dDate.getYear()+1900
    this
  }

  Boolean isSameDate(Date dDate) {
    if (!dDate) return false
    (month == dDate.getMonth()+1) && (year == dDate.getYear()+1900)
  }

  Agentratekreditplan calculateSum(Calendar startdate, BigDecimal _prevPeriodDebt) {
    BigDecimal result = 0
    Agentkredit akr = Agentkredit.get(agentkredit_id)
    Kredit kredit = Kredit.get(akr.kredit_id)
    startdate.setTime(datestart)
    if(kredit.startdate==startdate.getTime()) startdate.add(Calendar.DATE,1)
    BigDecimal initsum = (Kreditline.findAllByKredit_idAndPaydateLessThanAndModstatusGreaterThanEquals(akr.kredit_id,startdate.getTime(),0).sum{it.summa}?:0)-(Kreditpayment.findAllByKredit_idAndPaydateLessThan(akr.kredit_id,startdate.getTime()).sum{it.summa}?:0)+(kredit.kredtype==1?kredit.agentsum:0)
    def bodychanges = ((Kreditline.findAllByKredit_idAndPaydateGreaterThanEqualsAndPaydateLessThanEqualsAndModstatusGreaterThanEquals(akr.kredit_id,startdate.getTime(),dateend,0).collect{[paydate:it.paydate,summa:it.summa]}+Kreditpayment.findAllByKredit_idAndPaydateGreaterThanEqualsAndPaydateLessThanEqualsAndSummaGreaterThan(akr.kredit_id,startdate.getTime(),dateend,0).collect{[paydate:it.paydate,summa:-it.summa]})+([[paydate:dateend,summa:0]])).groupBy{it.paydate}.sort{it.key}.each{ change ->
      int monthcount = 0
      int daycount = 0
      int adddays = 0
      if (akr.payterm){
          while(startdate.getTime()<=change.key) { adddays = startdate.getActualMaximum(Calendar.DAY_OF_MONTH); monthcount++; startdate.add(Calendar.DATE,adddays); }
          startdate.add(Calendar.DATE,-adddays)
          monthcount--
      }
      while(startdate.getTime()<=change.key) { daycount++; startdate.add(Calendar.DATE,1); }
      result += (initsum*monthcount*calcrate/1200+initsum*daycount*calcrate/36500)*vrate
      initsum += change.value.sum{it.summa}
    }
    summa = result
    debt = initsum
    clientdebt = _prevPeriodDebt
    this
  }

  BigDecimal recalculatePeriod(Calendar startdate) {
    BigDecimal result = 0
    Agentkredit akr = Agentkredit.get(agentkredit_id)
    Kredit kredit = Kredit.get(akr.kredit_id)
    startdate.setTime(datestart)
    if(kredit.startdate==startdate.getTime()) startdate.add(Calendar.DATE,1)
    BigDecimal initsum = (Kreditline.findAllByKredit_idAndPaydateLessThanEqualsAndModstatusGreaterThanEquals(akr.kredit_id,startdate.getTime(),0).sum{it.summa}?:0)-(Kreditpayment.findAllByKredit_idAndPaydateLessThan(akr.kredit_id,startdate.getTime()).sum{it.summa}?:0)+(kredit.kredtype==1?kredit.agentsum:0)
    def bodychanges = ((Kreditline.findAllByKredit_idAndPaydateGreaterThanAndPaydateLessThanEqualsAndModstatusGreaterThanEquals(akr.kredit_id,startdate.getTime(),dateend,0).collect{[paydate:it.paydate,summa:it.summa]}+Kreditpayment.findAllByKredit_idAndPaydateGreaterThanEqualsAndPaydateLessThanEqualsAndSummaGreaterThan(akr.kredit_id,startdate.getTime(),dateend,0).collect{[paydate:it.paydate,summa:-it.summa]})+([[paydate:dateend,summa:0]])).groupBy{it.paydate}.sort{it.key}.each{ change ->
      int monthcount = 0
      int daycount = 0
      int adddays = 0
      if (akr.payterm){
          while(startdate.getTime()<=change.key) { adddays = startdate.getActualMaximum(Calendar.DAY_OF_MONTH); monthcount++; startdate.add(Calendar.DATE,adddays); }
          startdate.add(Calendar.DATE,-adddays)
          monthcount--
      }
      while(startdate.getTime()<=change.key) { daycount++; startdate.add(Calendar.DATE,1); }
      result += (initsum*monthcount*calcrate/1200+initsum*daycount*calcrate/36500)*vrate
      initsum += change.value.sum{it.summa}
    }
    result - summa - clientdebt
  }

  BigDecimal recieveSS(){
    Agentkredit akr = Agentkredit.get(agentkredit_id)
    if (!akr) return 0.0g
    if (!calcrate) return 0.0g
    Agentrateforperiods ar = Agentrateforperiods.findByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(akr.id,id,1)
    if (!ar) return (summa + clientdebt) * (calcrate - (Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(akr.id,id,0).sum{ it.rate }?:0)) / calcrate
    else if (ar.subtype) return (summa + clientdebt) * (100 * (calcrate - (Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(akr.id,id,0).sum{ it.rate }?:0)) + calccost * ar.rate) / (calcrate * (100 + ar.rate))
    else return 100 * (summa + clientdebt) * (calcrate - (Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(akr.id,id,0).sum{ it.rate }?:0)) / (calcrate * (100 + ar.rate))
  }

  Float recieveSSpercent(){
    Agentkredit akr = Agentkredit.get(agentkredit_id)
    if (!akr) return 0.0g
    if (!calcrate) return 0.0g
    calcrate - (Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(akr.id,id,0).sum{ it.rate }?:0)
  }

  BigDecimal recieveCost(){
    recieveCost(Agentkredit.get(agentkredit_id))
  }

  BigDecimal recieveCost(Agentkredit akr){
    if (!akr) return 0.0g
    if (!calcrate) return 0.0g
    (summa + clientdebt) * calccost / calcrate
  }

  BigDecimal recieveProfit(){
    recieveSS() - recieveCost()
  }
}