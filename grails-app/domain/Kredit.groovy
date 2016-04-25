class Kredit {
  static mapping = { version false }
  static constraints = {
    stopdate(nullable:true)
    startsaldodate(nullable:true)
  }
  private enum Historyfields {
    KREDTYPE, IS_REAL, IS_TECH, SUMMA, RATE, ENDDATE, KREDITTERM, IS_AGR, COMMENT, RESPONSIBLE, IS_REALTECH
  }

  Integer id
  Integer kredtype
  Integer is_real
  Integer is_tech
  Integer is_realtech
  Integer client
  String bank_id
  Integer creditor = 0
  String cbank_id = ''
  Integer reasonagroffice = 0
  Integer reasonagrwh = 0
  Integer reasonagrbank = 0
  Integer reasonagrspace = 0
  String anumber
  Date adate
  BigDecimal summa = 0
  BigDecimal startsumma = 0
  BigDecimal agentsum = 0
  Date startsaldodate
  Double rate
  Integer valuta_id
  BigDecimal debt = 0.0g
  Date startdate
  Date enddate
  Date stopdate
  Integer kreditterm
  Integer payterm
  Integer paytermcondition = 2
  Integer repaymenttype_id
  Integer monthnumber
  Integer modstatus = 1
  Integer kredittransh = 0
  Date inputdate = new Date()
  String aim = ''
  Integer is_agr
  Integer is_cbcalc
  Integer client_id = 0
  Integer curclient_id = 0
  Integer zalogstatus = 1
  Integer cessionstatus = 0
  String sschet = ''
  String percschet = ''
  String comschet = ''
  String comment
  Long responsible
  Integer project_id = 0
  Integer is_check = 0

  def transient admin_id = 0
  def transient dopagrcomment = ''

  def afterInsert(){
    new Kredithist(kredit_id:id,admin_id:admin_id,dopagrcomment:dopagrcomment).setData(properties).save(failOnError:true)
    new Kreditdopagr(kredit_id:id).fillFrom(this).save(failOnError:true)
    new Agentperiod(kredit_id:id).fillFrom(this).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Kredithist(kredit_id:id,admin_id:admin_id,dopagrcomment:dopagrcomment).setData(properties).save(failOnError:true)
    if(isDirty('startdate')){ def _startdate = getPersistentValue('startdate'); Agentperiod.withNewSession{ Agentperiod.findByKredit_idAndStartdate(id,_startdate)?.csiSetStartdate(startdate)?.save(flush:true) }}
    if(isDirty('enddate')) { def _enddate = getPersistentValue('enddate'); Agentperiod.withNewSession{ Agentperiod.findByKredit_idAndEnddate(id,_enddate)?.csiSetEnddate(enddate)?.save(flush:true) }}
    if(isDirty('client_id')) { def _client_id = getPersistentValue('client_id'); Agentperiod.withNewSession{ Agentperiod.findByKredit_idAndClient_id(id,_client_id)?.csiSetClient_id(client_id)?.save(flush:true) }}
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)} в ${Bank.get(bank_id)?.shortname} (${is_real?'реал':is_tech?'техн':'реалтех'})${!modstatus?' (архивный)':''}"
  }

  String toFullString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)} в ${Bank.get(bank_id)?.shortname} по ${String.format('%td.%<tm.%<tY',enddate)} - ${Tools.toFixed(summa,2)} под ${Tools.toFixed(rate,2)}"
  }

  static def getBanks(){
    Kredit.list().collect{it.bank_id}.unique().collect{Bank.get(it)}.sort{it.name}
  }

  static def getBanks(iClientId){
    Kredit.findAllByClient_id(iClientId).collect{it.bank_id}.unique().collect{Bank.get(it)}
  }

  static def getClientsBank(lsBankId,iClientId){
    Kredit.findAllByBank_idInListAndIdInListAndModstatus(lsBankId,Agentperiod.findAllByClient_id(iClientId).collect{it.kredit_id}?:[0],1).collect{it.client}.unique().collect{Company.get(it)}
  }

  static def getCessionBanks(){
    Kredit.findAllByCessionstatusGreaterThan(0).collect{it.bank_id}.collect{Bank.get(it)}
  }

  static def getResponsibles(){
    Kredit.findAllByResponsibleGreaterThan(0).collect{it.responsible}.unique().inject([:]){map, id -> map[id]=User.get(id)?.name;map}
  }

  static def getCompanies(sBankId){
    if(sBankId) Kredit.findAllByBank_id(sBankId).collect{it.client}.unique().collect{Company.get(it)}.sort{it.name}
    else Kredit.list().collect{ it.creditor?:it.client }.unique().collect{Company.get(it)}.sort{it.name}
  }

  static def getAgreementsBy(Integer _company_id){
    if(_company_id>0) Kredit.findAll(sort:'anumber',order:'asc'){ modstatus in 0..1 && ((creditor == 0 && client == _company_id) || creditor == _company_id) }
    else Kredit.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc'])
  }

  Kredit setMainAgrData(_request){
    anumber = _request.anumber
    adate = _request.adate
    summa = _request.summa
    rate = _request.rate?.toDouble()
    startdate = _request.startdate
    enddate = _request.enddate
    this
  }

  Kredit setData(_request){
    kredtype = _request.kredtype
    is_real = !_request.kreditsort?1:0
    is_tech = _request.kreditsort==1?1:0
    is_realtech = _request.kreditsort==2?1:0
    startsumma = _request.startsumma?:0.0g
    agentsum = Agentkredit.findAllByKredit_id(id?:0)?agentsum:_request.agentsum?:0.0g
    startsaldodate = _request.startsaldodate
    valuta_id = _request.valuta_id?:857
    payterm = kredtype==3?0:_request.payterm
    kreditterm = computePaymentPeriods()
    repaymenttype_id = kredtype==4?1:_request.repaymenttype_id?:1
    monthnumber = _request.repaymenttype_id!=2?0:_request.monthnumber>0&&_request.monthnumber<=kreditterm?_request.monthnumber:1
    kredittransh = kredtype!=4?0:_request.kredittransh
    aim = _request.aim?:''
    is_agr = _request.is_agr?:0
    is_cbcalc = _request.is_cbcalc?:0
    client_id = _request.client_id==-1?0:_request.client_id?:client_id
    curclient_id = cessionstatus?curclient_id:client_id
    sschet = _request?.sschet?_request.sschet.replace('.',''):''
    percschet = _request?.percschet?_request.percschet.replace('.',''):''
    comschet = _request?.comschet?_request.comschet.replace('.',''):''
    comment = _request.comment?:''
    responsible = _request.responsible?:0l
    is_check = _request.is_check?:0
    project_id = _request.project_id?:0
    if(isDirty('valuta_id')) recalculateRubSummas()
    this
  }

  Kredit fillFrom(Kreditdopagr _dopagr){
    enddate = _dopagr.enddate
    summa = _dopagr.summa
    rate = _dopagr.rate
    if (_dopagr.id==Kreditdopagr.getMinId(id)) startdate = _dopagr.startdate
    this
  }

  Kredit csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Kredit csiSetZalogstatus(iStatus){
    zalogstatus = iStatus?:1
    this
  }

  Kredit csiSetDopComment(sComment){
    dopagrcomment = sComment?:''
    this
  }

  Kredit csiSetCessionstatus(iStatus){
    cessionstatus = iStatus?:0
    this
  }

  Kredit csiSetCreditor(iCreditor){
    creditor = iCreditor?:0
    this
  }

  Kredit csiSetStartdate(dStartdate){
    startdate = dStartdate?:startdate
    this
  }

  Kredit csiSetCurclient(iClientId, Date _cdate){
    if(curclient_id!=iClientId){
      curclient_id = iClientId
      Agentperiod.findByKredit_idAndEnddate(id,enddate)?.csiSetEnddate(_cdate-1)?.save()
      new Agentperiod(kredit_id:id).csiSetStartdate(_cdate).csiSetEnddate(enddate).csiSetClient_id(iClientId).save()
    }
    this
  }

  Kredit csiSetCbank(sBankId){
    cbank_id = sBankId?:''
    this
  }

  BigDecimal getvRate(){
    valuta_id==857?1g:new Valutarate().csiSearchCurrent(valuta_id)?.toBigDecimal()?:1g
  }

  Kredit csiSetModstatus(iStatus){
    if(iStatus==-1&&!(Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(3,id,-1)||Kreditline.findAllByKredit_idAndModstatusGreaterThanEquals(id,0)||Agentkredit.findAllByKredit_id(id))) modstatus = -1
    else if (iStatus!=-1) modstatus = iStatus?:0

    this
  }

  Kredit updateDebt(_summa){
    debt = _summa?:0
    this
  }

  Boolean isHaveDirty (){ return Kredit.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  def computepayments(){
    //Kreditline.findAllByKredit_idAndModstatusGreaterThanEquals(35,0).collect{[[paydate:it.paydate,summa:it.summa],[paydate:it.paydate+60,summa:-it.summa]]}.flatten().sort{it.paydate}
    def _incomingpayment = Kreditline.findAllByKredit_idAndModstatusGreaterThanEquals(id,0,[sort:'paydate',order:'asc'])
    def _outgoingpayment = _incomingpayment.collect{[paydate:it.paydate+kredittransh<=enddate?Tools.getPreviousWorkedDate(it.paydate+kredittransh+1):enddate,summa:-it.summa]}
    def _firstmonth = monthnumber?:kreditterm
    def _monthlybasepay = kredtype==4?0:(_incomingpayment.sum{it.summa}?:summa) / (kreditterm-_firstmonth+1)
    if (kredtype==4) _incomingpayment = (_incomingpayment+_outgoingpayment).sort{it.paydate}
    mergepayments(processcomputepayments(_incomingpayment,_monthlybasepay,_firstmonth),_outgoingpayment)
  }

  def mergepayments(_basepayments,_transhoutpayments){
    if (kredtype!=4) return _basepayments
    def vrate = (valuta_id==857?1g:new Valutarate().csiSearchCurrent(valuta_id)?.toBigDecimal()?:1g)
    _transhoutpayments.each{ out_p ->
      def tmp_p = _basepayments.find{it.paydate==out_p.paydate}
      if (tmp_p) tmp_p.basesumma += -out_p.summa
      else _basepayments << [paydate:out_p.paydate, basesumma:-out_p.summa, perssumma:0, paidmonths:0, recievedsum:0, rate:vrate]
    }
    _basepayments
  }

  def processcomputepayments(incomingpayment,monthlybasepay,firstmonth){
    //TODO:Move it into AgentKreditService
    def resultlist = []
    def cal = Calendar.getInstance()
    def cal2 = Calendar.getInstance()
    def paidmonths = 1
    def recievedsum = kredtype==1?summa:0
    def vrate = (valuta_id==857?1g:new Valutarate().csiSearchCurrent(valuta_id)?.toBigDecimal()?:1g)
    def percsum = 0
    cal.setTime(startdate)
    cal2.setTime(startdate)
    def isNext = (payterm<=cal.get(Calendar.DAY_OF_MONTH))
    if(isNext) cal.add(Calendar.MONTH,1)
    setPaymentDate(cal,payterm)
    if(cal.getTime()<enddate){
      percsum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),(isNext?cal2.getActualMaximum(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),isNext?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      recievedsum += incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
      correctPaydate(cal)
      if(isNext) cal2.add(Calendar.MONTH,1)
      setPaymentDate(cal2,payterm)
      percsum += computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:percsum,paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
      recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
      addMonth(cal)
      setPaymentDate(cal,payterm)
      correctPaydate(cal2)
      while(paidmonths<kreditterm-1) {
        paidmonths++
        percsum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
        recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
        correctPaydate(cal)
        addMonth(cal2)
        setPaymentDate(cal2,payterm)
        percsum += computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
        resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:percsum,paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
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
    def basepayment = _payment.is_auto?_payment:Kreditpayment.findByKredit_idAndPaydateLessThanAndIs_auto(id,_payment.paydate,1,[sort:'paydate',order:'desc'])
    def resultlist = []
    def cal = Calendar.getInstance()
    def cal2 = Calendar.getInstance()
    def basedate = basepayment?.paydate?:startdate
    def incomingpayment = Kreditline.findAllByKredit_idAndModstatusGreaterThanEqualsAndPaydateGreaterThan(id,0,basedate,[sort:'paydate',order:'asc'])
    def paidmonths = (basepayment?.paidmonth?:0)+1
    def firstmonth = monthnumber?(monthnumber>(basepayment?.paidmonth?:0)?monthnumber:paidmonths):kreditterm
    def paidsum = Kreditpayment.findAllByKredit_idAndPaydateLessThan(id,basedate).sum{it.summa}?:0
    def recievedsum = kredtype==1 ? (summa-paidsum) : ((Kreditline.findAllByKredit_idAndModstatusGreaterThanEqualsAndPaydateLessThanEquals(id,0,basedate).sum{it.summa}?:summa)-paidsum)
    incomingpayment += Kreditpayment.findAllByKredit_idAndPaydateGreaterThanAndIs_auto(id,basedate,0,[sort:'paydate',order:'asc']).collect{[paydate:it.paydate,summa:-it.summa]}
    def monthlybasepay = ((incomingpayment.sum{it.summa}?:0) + recievedsum) / (kreditterm-firstmonth+1)
    def vrate = (valuta_id==857?1g:new Valutarate().csiSearchCurrent(valuta_id)?.toBigDecimal()?:1g)
    def percsum = 0
    cal.setTime(basedate)
    cal2.setTime(basedate)
    def isNext = false
    if (!basepayment) isNext = (payterm<=cal.get(Calendar.DAY_OF_MONTH))
    else addMonth(cal)
    if(isNext) cal.add(Calendar.MONTH,1)
    setPaymentDate(cal,payterm)
    if(cal.getTime()<enddate){
      if (!basepayment) percsum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),(isNext?cal2.getActualMaximum(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),isNext?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      else percsum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
      correctPaydate(cal)
      if (basepayment) addMonth(cal2)
      else if (isNext) cal2.add(Calendar.MONTH,1)
      setPaymentDate(cal2,payterm)
      percsum += computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
      resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:percsum,paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
      recievedsum += incomingpayment.findAll{ it.paydate >= cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
      addMonth(cal)
      setPaymentDate(cal,payterm)
      correctPaydate(cal2)
      while(paidmonths<kreditterm-1) {
        paidmonths++
        percsum = computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
        recievedsum += incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() }?.sum{it.summa}?:0
        correctPaydate(cal)
        addMonth(cal2)
        setPaymentDate(cal2,payterm)
        percsum += computepercent(cal2.get(Calendar.DAY_OF_MONTH),incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()==cal2.getTime().getMonth() },incomingpayment.findAll{ it.paydate > cal2.getTime() && it.paydate <= cal.getTime() && it.paydate.getMonth()!=cal2.getTime().getMonth() },recievedsum,cal2.getActualMaximum(Calendar.DAY_OF_YEAR),cal2.getActualMaximum(Calendar.DAY_OF_MONTH)-cal2.get(Calendar.DAY_OF_MONTH),cal.getActualMaximum(Calendar.DAY_OF_YEAR),cal.get(Calendar.MONTH)!=cal2.get(Calendar.MONTH)?cal.get(Calendar.DAY_OF_MONTH):0,resultlist.sum{it.basesumma}?:0)
        resultlist << [paydate:cal.getTime(),basesumma:(paidmonths>=firstmonth?monthlybasepay:0),perssumma:percsum,paidmonths:paidmonths,recievedsum:recievedsum,rate:vrate]
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
    if (kredtype==3) return 0
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

  void recalculateRubSummas(){
    if (!is_cbcalc) return
    BigDecimal vrate = getvRate()
    Kreditline.findAllByKredit_idAndModstatus(id,0).each {
      it.updateRate(vrate).merge(failOnError:true)
    }
    Kreditpayment.findAllByKredit_idAndModstatus(id,0).each {
      it.updateRate(vrate).merge(failOnError:true)
    }
  }

  Boolean isRateable(){
    is_cbcalc||valuta_id==857
  }
}