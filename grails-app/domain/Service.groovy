class Service {
  static mapping = { version false }
  private enum Historyfields {
    SUMMA, ZBANK_ID, ENDDATE, EBANK_ID, COMMENT, DESCRIPTION, RESPONSIBLE
  }

  Integer id
  Integer zcompany_id
  Integer ecompany_id
  Integer is_nds
  Integer atype
  Integer asort
  Date inputdate = new Date()
  Date adate
  Date enddate
  String anumber
  Long summa
  Integer payterm
  Integer paycondition = 1
  Integer paytermcondition = 1
  String zbank_id
  String ebank_id
  Integer prolongcondition
  Integer prolongterm
  String description = ''
  String comment = ''
  Integer modstatus
  Integer project_id
  Long responsible =  0

  def transient admin_id = 0

  def afterInsert(){
    new Servicehist(service_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
    new Servicedopagr(service_id:id).fillFrom(this).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Servicehist(service_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  Boolean isHaveDirty (){ return Service.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  Service setMainAgrData(_request){
    summa = _request.summa
    enddate = _request.enddate
    this
  }

  Service setData(_request){
    is_nds = _request.is_nds?:0
    atype = _request.atype
    asort = _request.asort
    adate = _request.adate
    anumber = _request.anumber
    payterm = _request.payterm
    paycondition = _request.paycondition?:0
    paytermcondition = _request.paytermcondition
    zbank_id = _request.zbank_id?:''
    ebank_id = _request.ebank_id?:''
    prolongcondition = _request.prolongcondition?:0
    prolongterm = prolongcondition!=2?0:_request.prolongterm?:11
    description = _request.description?:''
    comment = _request.comment?:''
    project_id = _request.project_id?:0
    responsible = _request.responsible?:0l
    this
  }

  Service fillFrom(Servicedopagr _dopagr){
    enddate = _dopagr.enddate
    summa = _dopagr.summa
    this
  }

  Service csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Service updateModstatus(iStatus){
    if(iStatus==-1&&!Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(8,id?:0,-1)) modstatus = -1
    else if (iStatus!=-1) modstatus = !iStatus?0:(enddate<new Date().clearTime())?0:1
    this
  }

  Service prolongAgreement(_cal){
    if (prolongcondition==2&&enddate<new Date()){
      _cal.setTime(enddate)
      _cal.add(Calendar.MONTH,prolongterm)
      enddate = _cal.getTime()
    }
    this
  }

  void accruePayments(Date _date, Integer _monthdays){
    if(((_date.getMonth()>=adate.getMonth()&&_date.getYear()==adate.getYear())||_date.getYear()>adate.getYear())||((_date.getMonth()<=enddate.getMonth()&&_date.getYear()==enddate.getYear())||_date.getYear()<enddate.getYear())){
      new Servicecalculation(service_id:id).setBaseData(month:_date.getMonth()+1,year:_date.getYear()+1900,calcdate:new Date()).setData(summa:computeSumma(_date,_monthdays)).save(flush:true)
    }
  }

  BigDecimal computeSumma(Date _date, Integer _monthdays){
    BigDecimal result = summa.toBigDecimal()
    if (_date.getMonth()==enddate.getMonth()&&_date.getYear()==enddate.getYear()) result = result / _monthdays * enddate.getDate()
    result
  }
}