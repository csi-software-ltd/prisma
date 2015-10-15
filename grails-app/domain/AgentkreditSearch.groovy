class AgentkreditSearch {
  def searchService
  static mapping = {
    version false
  }
/////////agentkredit////////////////////
  Integer id
  Integer agentagr_id
  Integer kredit_id
  Float rate
  Integer payterm
  Date calcdate
/////////kredit/////////////////////////
  BigDecimal kr_summa
  BigDecimal lastbodydebt
  Date adate
  Date enddate
  Integer kreditterm
  Integer valuta_id
/////////bank///////////////////////////
  String bank_name
/////////company////////////////////////
  String client_name
/////////agentrate//////////////////////
  Integer agentexist
  Integer periodexist
  Float agentpercent

  def csiSelectKredits(iAgentagrId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, company.name as client_name, kredit.agentsum as kr_summa, (select count(*) from agentkreditplan where agentkreditplan.agentkredit_id=agentkredit.id) as periodexist, (select count(*) from agentrate where agentrate.agentkredit_id=agentkredit.id) as agentexist, (select sum(rate) from agentrate where agentrate.agentkredit_id=agentkredit.id and is_sub=0) as agentpercent, (select sum(debt) from agentkreditplan where agentkreditplan.agentkredit_id=agentkredit.id and agentkreditplan.modstatus<2 and is_last=1) as lastbodydebt, bank.shortname as bank_name"
    hsSql.from='agentkredit, kredit, company, bank'
    hsSql.where="agentkredit.kredit_id=kredit.id and kredit.client=company.id and kredit.bank_id=bank.id"+
                ((iAgentagrId>0)?' and agentkredit.agentagr_id=:agentagr_id':'')
    hsSql.order="agentkredit.id desc"

    if(iAgentagrId>0)
      hsLong['agentagr_id']=iAgentagrId

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentkreditSearch.class)
  }

}