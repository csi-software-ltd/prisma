class AgentkreditplanSearch {
  def searchService
  static mapping = {
    version false
  }
/////////agentkredit////////////////////
  Integer akr_id
  Integer agentagr_id
  Integer kredit_id
  Float rate
/////////kredit/////////////////////////
  BigDecimal kr_summa
  Date adate
  Date enddate
  Integer valuta_id
/////////company////////////////////////
  String client_name
/////////agentkreditplan////////////////
  Integer id
  BigDecimal plan_debt
  BigDecimal clientdebt
  Date datestart
  Date dateend
  Integer plan_modstatus
  BigDecimal plan_summa
  Float plan_calcrate
  Integer is_last
  Integer year
  Integer month
  BigDecimal vrate
  Integer ishaveact

  def csiSelectPeriods(iAgentagrId,iModstatus){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, company.name as client_name, kredit.agentsum as kr_summa, agentkreditplan.debt as plan_debt, agentkreditplan.modstatus as plan_modstatus, agentkreditplan.summa as plan_summa, agentkreditplan.calcrate as plan_calcrate, agentkredit.id as akr_id, (select count(*) from actclient where month=agentkreditplan.month and year=agentkreditplan.year and agentagr_id=agentkredit.agentagr_id) as ishaveact"
    hsSql.from='agentkreditplan, agentkredit, kredit, company'
    hsSql.where="agentkredit.kredit_id=kredit.id and kredit.client=company.id and agentkreditplan.agentkredit_id=agentkredit.id"+
                ((iAgentagrId>0)?' and agentkredit.agentagr_id=:agentagr_id':'')+
                ((iModstatus>-100)?' and agentkreditplan.modstatus=:modstatus':'')
    hsSql.order="agentkreditplan.year desc, agentkreditplan.month desc"

    if(iAgentagrId>0)
      hsLong['agentagr_id'] = iAgentagrId
    if(iModstatus>-100)
      hsLong['modstatus'] = iModstatus

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentkreditplanSearch.class)
  }

  def csiSelectPeriodsByMonth(iAgentagrId,iMonth,iYear){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, company.name as client_name, kredit.agentsum as kr_summa, agentkreditplan.debt as plan_debt, agentkreditplan.modstatus as plan_modstatus, agentkreditplan.summa as plan_summa, agentkreditplan.calcrate as plan_calcrate, agentkredit.id as akr_id, 0 as ishaveact"
    hsSql.from='agentkreditplan, agentkredit, kredit, company'
    hsSql.where="agentkredit.kredit_id=kredit.id and kredit.client=company.id and agentkreditplan.agentkredit_id=agentkredit.id"+
                ((iAgentagrId>0)?' and agentkredit.agentagr_id=:agentagr_id':'')+
                ((iMonth>0)?' and agentkreditplan.month=:month':'')+
                ((iYear>2013)?' and agentkreditplan.year=:year':'')
    hsSql.order="agentkredit.id desc"

    if(iAgentagrId>0)
      hsLong['agentagr_id'] = iAgentagrId
    if(iMonth>0)
      hsLong['month'] = iMonth
    if(iYear>2013)
      hsLong['year'] = iYear

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentkreditplanSearch.class)
  }

}