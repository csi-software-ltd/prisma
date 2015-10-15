class Smr {
  static mapping = { version false }
  private enum Historyfields {
    SUMMA, SMRCAT_ID, ENDDATE, RESPONSIBLE, COMMENT
  }

  Integer id
  Integer client
  Integer supplier
  Integer smrsort
  Integer smrcat_id
  Date inputdate = new Date()
  Date adate
  Date enddate
  String anumber
  String cbank_id
  String sbank_id
  String description
  Integer modstatus
  String comment
  Long summa
  Integer paytype
  Long avans
  Double avanspercent
  Long responsible
  Integer project_id

  def transient admin_id

  def afterInsert(){
    new Smrhist(smr_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Smrhist(smr_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  Boolean isHaveDirty (){ return Smr.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  Smr setData(_request){
    smrcat_id = _request.smrcat_id
    adate = _request.adate
    enddate = _request.enddate
    anumber = _request.anumber
    cbank_id = _request.cbank_id
    sbank_id = _request.sbank_id
    description = _request.description?:''
    comment = _request.comment?:''
    summa = _request.summa
    paytype = _request.paytype
    avans = paytype==1?0:_request.avans
    avanspercent = paytype==1?0d:_request.avanspercent.toDouble()
    responsible = _request.responsible
    project_id = _request.project_id?:0
    if (!smrsort) smrsort = Company.get(client).is_holding==0?3:Company.get(supplier).is_holding==0?1:2
    this
  }

  Smr csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Smr updateModstatus(iStatus){
    if(iStatus==-1&&!Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(9,id?:0,-1)) modstatus = -1
    else if (iStatus!=-1) modstatus = (enddate<new Date()-1)?0:1
    this
  }

}