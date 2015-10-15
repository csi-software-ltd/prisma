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

  def csiSelectProjectPayments(iProjectId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="p.*, c1.name as fromcompany_name, c2.name as tocompany_name, project.name as project_name"
    hsSql.from='payrequest p left join company c1 on (p.fromcompany_id=c1.id) left join company c2 on (p.tocompany_id=c2.id) left join project on (p.project_id=project.id)'
    hsSql.where="p.modstatus>=0"+
                ((iProjectId>0)?' and p.project_id=:project_id':'')
    hsSql.order="p.paydate desc, p.id desc"

    if(iProjectId>0)
      hsLong['project_id'] = iProjectId

    searchService.fetchData(hsSql,hsLong,null,null,null,PayrequestProjectSearch.class,20)
  }
}