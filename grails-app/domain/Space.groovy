class Space {
  static mapping = {
    version false
  }
  static constraints = {
    debtdate(nullable:true)
    permitdate(nullable:true)
    workdate(nullable:true)
  }
  private enum Historyfields {
    SPACETYPE_ID, OWNER, ENDDATE, MODSTATUS, COMMENT, AREA, PAYTERM, RATEMETER, IS_ADDPAYMENT, RATE, RESPONSIBLE, MAINAGR_ID, IS_NOSUBRENTING, IS_NOPAYMENT, PROJECT_ID
  }

  Integer id
  Integer mainagr_id
  Integer spacetype_id
  Integer arendator
  Integer arendodatel
  String city = ''
  String fulladdress
  String shortaddress
  String anumber
  Date adate
  Date inputdate = new Date()
  Date enddate
  Integer prolongcondition = 0
  Integer prolongterm
  Integer monthnotification = 0
  String description
  Integer asort
  Integer subsub_id = 0
  Integer arendatype_id
  Integer paystatus
  Integer modstatus
  Integer is_nosubrenting
  Integer is_subwritten = 0
  Integer subspaceqty
  Integer is_nopayment
  Integer is_adrsame = 0
  String comment
  Integer project_id
  Double area
  Double terarea
  Integer is_territory
  Integer payterm
  Integer paycondition = 1
  Integer paytermcondition = 1
  Integer contcol = 0
  String bank_id
  Double ratemeter
  Integer is_addpayment
  Integer is_noexpense = 0
  BigDecimal rate
  BigDecimal actrate
  BigDecimal ratedop
  BigDecimal debt = 0.0g
  BigDecimal addpayment_debt = 0.0g
  Date debtdate
  Long responsible
  Integer permitstatus = 0
  Integer workstatus = 0
  String prolongcomment = ''
  Date permitdate
  Date workdate
  Long workuser = 0l

  def transient admin_id = 0

  def afterInsert(){
    new Spacehist(space_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Spacehist(space_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${shortaddress?' | '+shortaddress:''}${!modstatus?' (архивный)':''}"
  }

  String toFullString(){
    "${Company.get(arendodatel)?.name} - $anumber от ${String.format('%td.%<tm.%<tY',adate)} - ${Tools.toFixed(rate,2)}"
  }

  static def getResponsibles(){
    Space.list().collect{it.responsible}.unique().inject([:]){map, id -> map[id]=User.get(id)?.name;map}
  }

  static def getCompanies(lsTypes){
    Space.findAllBySpacetype_idInList(lsTypes?:Spacetype.list().collect{it.id}).collect{it.arendator}.unique().collect{Company.get(it)}.sort{it.name}
  }

  static def getCompaniesExt(lsTypes){
    Space.findAllBySpacetype_idInListAndAsort(lsTypes?:Spacetype.list().collect{it.id},1).collect{it.arendator}.unique().collect{Company.get(it)}.sort{it.name}
  }

  static def getAgreementsBy(Integer _company_id, Integer _ctrcompany_id){
    if(_company_id>0&&_ctrcompany_id>0) Space.findAll{ modstatus in 0..1 && ((arendator == _company_id && arendodatel == _ctrcompany_id) || (arendator == _ctrcompany_id && arendodatel == _company_id)) }
    else if(_company_id>0||_ctrcompany_id>0) Space.findAll{ modstatus in 0..1 && (arendator == (_company_id?:_ctrcompany_id) || arendodatel == (_company_id?:_ctrcompany_id)) }
    else Space.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc'])
  }

  Space setData(_request){
    mainagr_id = (!_request.asort)?_request.mainagr_id:0
    spacetype_id = _request.spacetype_id
    fulladdress = _request.fulladdress
    shortaddress = _request.shortaddress?:fulladdress
    anumber = _request.anumber
    adate = _request.adate
    enddate = _request.enddate
    prolongcondition = _request.prolongcondition?:0
    prolongterm = _request.prolongcondition!=2?0:_request.prolongterm?:0
    monthnotification = _request.prolongcondition!=3?0:_request.monthnotification?:0
    description = _request.description?:''
    asort = _request.asort?:0
    subsub_id = Space.get(mainagr_id)?.mainagr_id?:0
    arendatype_id = Company.get(arendodatel)?.is_holding?2:1
    is_nosubrenting = !asort?0:!_request.is_nosubrenting?1:0
    is_subwritten = !asort||is_nosubrenting?0:_request.is_subwritten?:0
    subspaceqty = !asort||!_request.is_nosubrenting?0:_request.is_nosubrenting?:0
    is_nopayment = arendatype_id==2?(_request.is_nopayment?:0):0
    is_adrsame = _request.is_adrsame?:0
    comment = _request.comment?:''
    project_id = _request.project_id?:0
    area = _request.area?_request.area.toDouble():0d
    is_territory = _request.is_territory?:0
    terarea = !is_territory?0d:_request.terarea?_request.terarea.toDouble():0d
    payterm = _request.payterm
    paycondition = _request.paycondition?:1
    paytermcondition = _request.paytermcondition?:1
    contcol = spacetype_id!=5?0:_request.contcol?:0
    bank_id = _request.bank_id?:''
    ratemeter = _request.ratemeter?_request.ratemeter.toDouble():0d
    is_addpayment = _request.is_addpayment?:0
    is_noexpense = _request.is_noexpense?:0
    rate = _request.rate?:0.0g
    actrate = _request.actrate?:rate
    ratedop = !is_addpayment?0.0g:_request.ratedop?:0.0g
    responsible = _request.responsible?:0l
    this
  }

  Space updateModstatus(iStatus){
    if(iStatus==-1&&!Payment.findAllByAgreementtype_idAndAgreement_id(2,id?:0)) modstatus = -1
    else if (iStatus!=-1) modstatus = (enddate<new Date().clearTime())?0:1
    this
  }

  Space computePaystatus(){
    paystatus = (Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(arendator,1,1).find{it.ibankstatus==1}&&Bankaccount.findByCompany_idAndModstatusAndTypeaccount_id(arendodatel,1,1))?1:0
    this
  }

  Space updateprolongData(_request){
    prolongcomment = _request.prolongcomment?:''
    this
  }

  Space csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Space csiSetPermitstatus(iStatus){
    if (workstatus==0&&iStatus!=permitstatus){
      permitstatus = iStatus?:0
      permitdate = permitstatus?new Date():null
    }
    this
  }

  Space csiSetWorkstatus(iStatus,lUserId){
    if (permitstatus==1&&iStatus!=workstatus){
      workstatus = iStatus?:0
      workdate = workstatus?new Date():null
      workuser = workstatus?lUserId:0
    }
    this
  }

  Space prolongAgreement(_cal){
    if (prolongcondition==2&&enddate<new Date()){
      _cal.setTime(enddate)
      _cal.add(Calendar.MONTH,prolongterm)
      enddate = _cal.getTime()
    }
    this
  }

  Space clearProlong(){
    permitstatus = 0
    workstatus = 0
    prolongcomment = ''
    permitdate = null
    workdate = null
    workuser = 0l
    this
  }

  void generatePayrequest(){
    def today = new Date()
    if (!is_nopayment&&paystatus){
      new Payrequest(paytype:1,paycat:1,agreementtype_id:2,agreement_id:id).csiSetSpaceAgrData(this).setGeneralData(payrequest_paydate:new Date(today.getYear(),today.getMonth()+(today.getDate()>payterm?1:0),payterm),summa:rate,summands:rate*Tools.getIntVal(Dynconfig.findByName('payrequest.nds.value')?.value,18)/100,destination:'Оплата арендной платы по договору',is_nds:1).save(failOnError:true)
      if (is_addpayment&&ratedop>0) new Payrequest(paytype:1,paycat:1,agreementtype_id:2,agreement_id:id).csiSetSpaceAgrData(this).setGeneralData(payrequest_paydate:new Date(today.getYear(),today.getMonth()+(today.getDate()>payterm?1:0),payterm),summa:ratedop,summands:ratedop*Tools.getIntVal(Dynconfig.findByName('payrequest.nds.value')?.value,18)/100,destination:'Оплата по арендному договору за дополнительные услуги',is_nds:1).csiSetDop().save(failOnError:true)
    }
  }

  void accruePayments(Date _date, Integer _monthdays){
    if(((_date.getMonth()>=adate.getMonth()&&_date.getYear()==adate.getYear())||_date.getYear()>adate.getYear())||((_date.getMonth()<=enddate.getMonth()&&_date.getYear()==enddate.getYear())||_date.getYear()<enddate.getYear())){
      new Spacecalculation(space_id:id,is_dop:0).setBaseData(month:_date.getMonth()+1,year:_date.getYear()+1900,calcdate:new Date()).setData(summa:computeSumma(_date,false,_monthdays)).save(flush:true)
      if (is_addpayment&&ratedop>0) new Spacecalculation(space_id:id,is_dop:1).setBaseData(month:_date.getMonth()+1,year:_date.getYear()+1900,calcdate:new Date()).setData(summa:computeSumma(_date,true,_monthdays)).save(flush:true)
    }
  }

  BigDecimal computeSumma(Date _date, Boolean _is_dop, Integer _monthdays){
    BigDecimal result = _is_dop?ratedop:rate
    if (_date.getMonth()==enddate.getMonth()&&_date.getYear()==enddate.getYear()) result = result / _monthdays * enddate.getDate()
    result
  }

  Boolean isHaveDirty (){ return Space.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

}