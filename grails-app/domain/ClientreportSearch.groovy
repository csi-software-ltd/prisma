class ClientreportSearch {
  def searchService
  static mapping = { version false }
//////////////payrequest///////////////////
  Integer id
  Integer client_id
//////////////general//////////////////////
  BigDecimal income
  BigDecimal outlay
  BigDecimal clsupcomission
  BigDecimal clretcomission
  BigDecimal clmidcomission
  BigDecimal clretmidcomission
  BigDecimal clrepayment
  BigDecimal clcomission
  String client_name
  BigDecimal curclientsaldo
  BigDecimal dinclientsaldo

  def csiSelectClientReport(dReportdate){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsLong=[:]

    hsSql.select="*, ifnull((select sum(if(payrequest.confirmstatus,0,if(payrequest.paytype in (1,3,4,7),-payrequest.clientdelta,payrequest.clientdelta))) from payrequest where payrequest.client_id=if(cl1.parent>0,cl1.parent,cl1.id) and payrequest.subclient_id=if(cl1.parent>0,cl1.id,0) and payrequest.agent_id=0 and payrequest.modstatus>=0 and ((month(payrequest.paydate)<=:month and year(payrequest.paydate)=:year) or year(payrequest.paydate)<:year)),0) as dinclientsaldo, cl1.saldo+cl1.addsaldo as curclientsaldo, cl1.name as client_name, sum(comission) as clcomission, sum(clientcommission) as clrepayment, sum(if(is_clientcommission,clientdelta,0)) as clretcomission, sum(supcomission) as clsupcomission, sum(if(is_midcommission,clientdelta,0)) as clretmidcomission, sum(midcomission) as clmidcomission, sum(if(paytype in (1,3,4,7),summa,0)) as outlay, sum(if(paytype in (2,5,8,9),summa,0)) as income"
    hsSql.from='client cl1 left join payrequest p on (p.client_id=if(cl1.parent>0,cl1.parent,cl1.id) and p.subclient_id=if(cl1.parent>0,cl1.id,0) and month(p.paydate)=:month and year(p.paydate)=:year and p.agent_id=0 and p.modstatus>=0)'
    hsSql.where="1=1"
    hsSql.group="cl1.id"
    hsSql.order="client_name asc"

    if(!dReportdate) dReportdate = new Date()
    hsLong['month'] = dReportdate.getMonth()+1
    hsLong['year'] = dReportdate.getYear()+1900

    searchService.fetchData(hsSql,hsLong,null,null,null,ClientreportSearch.class)
  }

}