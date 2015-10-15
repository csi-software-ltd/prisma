class License {
  def searchService
  static mapping = {
    version false
  }
  static constraints = {
    enddate(nullable:true)
  }
  private enum Historyfields {
    ANUMBER, ADATE, ENDDATE, PAYTYPE, ENTRYFEE, ALIMIT, REGFEE, MODSTATUS
  }

  Integer id
  Integer company_id
  Integer sro_id
  Integer industry_id
  Date inputdate = new Date()
  String anumber
  Date adate
  Date enddate
  String license = ''
  Integer paytype
  Integer entryfee
  Integer paidfee = 0
  Integer alimit
  Integer regfee
  Integer regfeeterm
  Integer strakhfee
  Integer modstatus
  Long file = 0

  def transient admin_id

  def afterInsert(){
    new Licensehist(license_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Licensehist(license_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  License setData(_request){
    industry_id = _request.industry_id
    anumber = _request.anumber
    adate = _request.adate
    enddate = _request.enddate
    paytype = _request.paytype?:1
    entryfee = _request.entryfee?:0
    regfee = _request.regfee?:0
    alimit = _request.alimit?:0
    regfeeterm = _request.regfeeterm?:1
    strakhfee = _request.strakhfee?:0
    this
  }

  License csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  License csiSetModstatus(iStatus){
    if(iStatus==-1&&!Payment.findAllByAgreementtype_idAndAgreement_id(1,id?:0)) modstatus = -1
    else if (iStatus!=-1) modstatus = iStatus?:0
    this
  }

  License csiSetFileId(iFileId){
    file = iFileId?:file
    this
  }

  Boolean isHaveDirty (){ return License.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }
  
  def csiFindLicenceIndustry(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='license'
    hsSql.where="modstatus>0"+
                ((iCompanyId>0)?' and company_id=:company_id':'')
    hsSql.order="id desc"
    hsSql.group="industry_id"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,License.class)
  }
}