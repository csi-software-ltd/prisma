class Loan {
  static mapping = { version false }
  private enum Historyfields {
    SUMMA, RATE, ENDDATE, COMMENT, LOANCLASS
  }

  Integer id
  Integer loantype = 0
  Integer loanclass
  Integer client
  Long client_pers
  Integer lender
  Long lender_pers
  String anumber
  Date inputdate = new Date()
  Date adate
  Date startdate
  Date enddate
  Long summa = 0
  Double rate
  Integer valuta_id
  Long debt = 0
  BigDecimal bodydebt = 0g
  Integer loanterm
  Integer payterm
  Integer paytermcondition = 2
  Integer repaymenttype_id
  Integer monthnumber
  Integer modstatus = 1
  Integer is_cbcalc
  String comment

  def transient admin_id

  def beforeInsert(){
    loantype = lender_pers?4:client_pers?5:Company.get(lender)?.is_holding==0?1:Company.get(client)?.is_holding==0?2:3
  }

  def afterInsert(){
    new Loanhist(loan_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Loanhist(loan_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  String toFullString(){
    "${lender_pers?Pers.get(lender_pers)?.shortname:Company.get(lender)?.name} - $anumber от ${String.format('%td.%<tm.%<tY',adate)} - $summa"
  }

  Boolean isHaveDirty (){ return Loan.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  Loan setData(_request){
    loanclass = _request.loanclass?:1
    anumber = _request.anumber
    adate = _request.adate
    startdate = _request.startdate
    enddate = _request.enddate
    summa = _request.summa
    rate = _request.rate?.toDouble()
    valuta_id = _request.valuta_id?:857
    bodydebt = loanclass>1?(Loanline.findAllByLoan_idAndModstatusGreaterThan(id,-1).sum{it.summa}?:0):isDirty('loanclass')?summa:(bodydebt + _request.summa-summa)
    payterm = _request.payterm
    loanterm = computePaymentPeriods()
    repaymenttype_id = _request.repaymenttype_id?:1
    monthnumber = _request.repaymenttype_id!=2?0:_request.monthnumber>0&&_request.monthnumber<=loanterm?_request.monthnumber:1
    is_cbcalc = _request.is_cbcalc?:0
    comment = _request.comment?:''
    if(isDirty('valuta_id')) recalculateRubSummas()
    this
  }

  Loan csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Loan updateModstatus(iStatus){
    if(iStatus==-1&&!Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(10,id?:0,-1)) modstatus = -1
    else if (iStatus!=-1) modstatus = (enddate<new Date()-1)?0:1
    this
  }

  def computepayments(){
    //TODO:Move it into AgentKreditService
    def resultlist = []
    def cal = Calendar.getInstance()
    def cal2 = Calendar.getInstance()
    def incomingpayment = Loanline.findAllByLoan_idAndModstatusGreaterThanEquals(id,0,[sort:'paydate',order:'asc'])
    def paidmonths = 1
    def firstmonth = monthnumber?:loanterm
    def monthlybasepay = (incomingpayment.sum{it.summa}?:summa) / (loanterm-firstmonth+1)
    def recievedsum = loanclass==1?summa:0
    def vrate = (valuta_id==857?1g:new Valutarate().csiSearchCurrent(valuta_id)?.toBigDecimal()?:1g)
    def perssum = 0
    cal.setTime(startdate)
    cal2.setTime(startdate)
    def isNext = (payterm<=cal.get(Calendar.DAY_OF_MONTH))
    if(isNext) cal.add(Calendar.MONTH,1)
    setPaymentDate(cal,payterm)
    if(cal.getTime()<enddate){
      perssum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),(isNext?cal2.getActualMaximum(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),isNext?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      recievedsum += incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
      correctPaydate(cal)
      if(isNext) cal2.add(Calendar.MONTH,1)
      setPaymentDate(cal2,payterm)
      perssum += computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:perssum,paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
      recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
      addMonth(cal)
      setPaymentDate(cal,payterm)
      correctPaydate(cal2)
      while(paidmonths<loanterm-1) {
        paidmonths++
        perssum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
        recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
        correctPaydate(cal)
        addMonth(cal2)
        setPaymentDate(cal2,payterm)
        perssum += computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
        resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:perssum,paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
        recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
        addMonth(cal)
        setPaymentDate(cal,payterm)
        correctPaydate(cal2)
      }
      paidmonths++
    }
    cal.setTime(enddate)
    resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),(cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal2.getActualMaximum(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0),paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
    return resultlist
  }

  def recomputepayments(_payment){
    //TODO:Move it into AgentKreditService
    if (!_payment) return []
    def basepayment = _payment.is_auto?_payment:Loanpayment.findByLoan_idAndPaydateLessThanAndIs_auto(id,_payment.paydate,1,[sort:'paydate',order:'desc'])
    def resultlist = []
    def cal = Calendar.getInstance()
    def cal2 = Calendar.getInstance()
    def basedate = basepayment?.paydate?:startdate
    def incomingpayment = Loanline.findAllByLoan_idAndModstatusGreaterThanEqualsAndPaydateGreaterThan(id,0,basedate,[sort:'paydate',order:'asc'])
    def paidmonths = (basepayment?.paidmonth?:0)+1
    def firstmonth = monthnumber?(monthnumber>(basepayment?.paidmonth?:0)?monthnumber:paidmonths):loanterm
    def paidsum = (Loanpayment.findAllByLoan_idAndPaydateLessThan(id,basedate).sum{it.summa}?:0)
    def recievedsum = loanclass==1 ? (summa-paidsum) : ((Loanline.findAllByLoan_idAndModstatusGreaterThanEqualsAndPaydateLessThanEquals(id,0,basedate).sum{it.summa}?:summa)-paidsum)
    incomingpayment += Loanpayment.findAllByLoan_idAndPaydateGreaterThanAndIs_auto(id,basedate,0,[sort:'paydate',order:'asc']).collect{[paydate:it.paydate,summa:-it.summa]}
    def monthlybasepay = ((incomingpayment.sum{it.summa}?:0) + recievedsum) / (loanterm-firstmonth+1)
    def vrate = (valuta_id==857?1g:new Valutarate().csiSearchCurrent(valuta_id)?.toBigDecimal()?:1g)
    def perssum = 0
    cal.setTime(basedate)
    cal2.setTime(basedate)
    def isNext = false
    if (!basepayment) isNext = (payterm<=cal.get(Calendar.DAY_OF_MONTH))
    else addMonth(cal)
    if(isNext) cal.add(Calendar.MONTH,1)
    setPaymentDate(cal,payterm)
    if(cal.getTime()<enddate){
      if (!basepayment) perssum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),(isNext?cal2.getActualMaximum(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),isNext?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      else perssum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      recievedsum += incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
      correctPaydate(cal)
      if (basepayment) addMonth(cal2)
      else if (isNext) cal2.add(Calendar.MONTH,1)
      setPaymentDate(cal2,payterm)
      perssum += computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:perssum,paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
      recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
      addMonth(cal)
      setPaymentDate(cal,payterm)
      correctPaydate(cal2)
      while(paidmonths<loanterm-1) {
        paidmonths++
        perssum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
        recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
        correctPaydate(cal)
        addMonth(cal2)
        setPaymentDate(cal2,payterm)
        perssum += computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
        resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:perssum,paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
        recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
        addMonth(cal)
        setPaymentDate(cal,payterm)
        correctPaydate(cal2)
      }
      paidmonths++
    }
    cal.setTime(enddate)
    resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),(cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal2.getActualMaximum(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0),paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
    return resultlist
  }

  def computepercent(_daystart, _firstMonthPayments, _secondMonthPayments, _recievedsum, _prevyearlength, _prevmonthlength, _curyearlength, _curmonthlength, _paidbasesumma){
    def paiddays = 0
    def result = 0
    _firstMonthPayments.groupBy{it.paydate}.each{
      result += computepercent(_recievedsum, _prevyearlength, it.key.getDate() - _daystart - paiddays, _paidbasesumma)
      _recievedsum += it.value.sum{it.summa}
      paiddays += it.key.getDate() - _daystart - paiddays
    }
    result += computepercent(_recievedsum, _prevyearlength, _prevmonthlength - paiddays, _paidbasesumma)
    paiddays = 0
    _secondMonthPayments.groupBy{it.paydate}.each{
      result += computepercent(_recievedsum, _curyearlength, it.key.getDate() - paiddays, _paidbasesumma)
      _recievedsum += it.value.sum{it.summa}
      paiddays += it.key.getDate() - paiddays
    }
    result += computepercent(_recievedsum, _curyearlength, _curmonthlength - paiddays, _paidbasesumma)
    return result
  }

  def computepercent(_recievedsum, _yearlength, _monthlength, _paidbasesumma){
    return (((_recievedsum-_paidbasesumma)*rate/100/_yearlength*_monthlength)>0?((_recievedsum-_paidbasesumma)*rate/100/_yearlength*_monthlength):0)
  }

  void setPaymentDate(_cal,_payterm){
    if(_payterm>_cal.getActualMaximum(Calendar.DAY_OF_MONTH)) _cal.set(Calendar.DAY_OF_MONTH,_cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    else _cal.set(Calendar.DAY_OF_MONTH,_payterm)
  }

  void correctPaydate(_cal){
    _cal.setTime(Tools.getNextWorkedDate(_cal.getTime()-1,1))
  }

  void addMonth(_cal){
    _cal.setTime(Tools.getPreviousWorkedDate(_cal.getTime(),1)+1)
    _cal.add(Calendar.MONTH,1)
  }

  Integer computePaymentPeriods(){
    if (enddate<=startdate) return 0
    def monthcounter = 1
    def cal = Calendar.getInstance()
    cal.setTime(startdate)
    if(payterm<=cal.get(Calendar.DAY_OF_MONTH)) cal.add(Calendar.MONTH,1)
    setPaymentDate(cal,payterm)
    while(cal.getTime()<enddate) {
      monthcounter++
      cal.add(Calendar.MONTH,1)
      setPaymentDate(cal,payterm)
    }
    return monthcounter
  }

  BigDecimal getvRate(){
    valuta_id==857?1g:new Valutarate().csiSearchCurrent(valuta_id)?.toBigDecimal()?:1g
  }

  Loan increaseBodydebt(_summa){
    bodydebt += _summa
    this
  }

  Loan decreaseBodydebt(_summa){
    increaseBodydebt(-_summa)
  }

  void recalculateRubSummas(){
    if (!is_cbcalc) return
    BigDecimal vrate = getvRate()
    Loanline.findAllByLoan_idAndModstatus(id,0).each {
      it.updateRate(vrate).merge(failOnError:true)
    }
    Loanpayment.findAllByLoan_idAndModstatus(id,0).each {
      it.updateRate(vrate).merge(failOnError:true)
    }
  }

  Boolean isRateable(){
    is_cbcalc||valuta_id==857
  }

}