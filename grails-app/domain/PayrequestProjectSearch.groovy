class PayrequestProjectSearch {
  def searchService
  static mapping = { version false }
//////////////payrequest///////////////////
  Integer id
  Date paydate
  Date execdate
  BigDecimal summa
  BigDecimal summands
  String fromcompany
  Integer fromcompany_id
  String tocompany
  Integer tocompany_id
  Integer paytype
  Integer paycat
  Integer modstatus
  Integer instatus
  Integer taskpay_id
  Integer agreementtype_id
  Integer tax_id
  Long pers_id
  String agreementnumber
  String destination
  String tagcomment
  Integer project_id
  Integer client_id
  Integer expensetype_id
  Integer agentagr_id
  Integer agent_id
  Integer is_bankmoney
  Long file_id
//////////////Company/////////////////////
  String fromcompany_name
  String tocompany_name
  String project_name

  def csiSelectProjectPayments(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="p.*, c1.name as fromcompany_name, c2.name as tocompany_name, project.name as project_name"
    hsSql.from='payrequest p left join company c1 on (p.fromcompany_id=c1.id) left join company c2 on (p.tocompany_id=c2.id) left join project on (p.project_id=project.id)'
    hsSql.where="p.modstatus>=0 and p.project_id=:project_id"+
                (hsInrequest?.platperiod_year?(hsInrequest?.platperiod_month?' AND platperiod=:platperiod':' AND platperiod like concat("%.",:platperiod,"%")'):'')
    hsSql.order="p.paydate desc, p.id desc"

    hsLong['project_id'] = hsInrequest?.project_id?:-1
    if(hsInrequest?.platperiod_year)
      if(hsInrequest?.platperiod_month)
        hsString['platperiod'] = String.format('%tm.%<tY',new Date(hsInrequest?.platperiod_year-1900,hsInrequest?.platperiod_month-1,1))
      else
        hsString['platperiod'] = String.format('%tY',new Date(hsInrequest?.platperiod_year-1900,1,1))

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'p.id',true,PayrequestProjectSearch.class)
  }
}