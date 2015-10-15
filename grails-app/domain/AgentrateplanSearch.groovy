class AgentrateplanSearch {
  def searchService
  static mapping = { version false }
/////////agentrate//////////////////////
  Integer id
  Float agent_rate
  Integer is_sub
  Integer subtype
  Integer agent_id
  Integer is_display
/////////general////////////////////////
  Integer plan_id
  BigDecimal plan_sum
  BigDecimal plan_debt
  BigDecimal plan_clientdebt
  Float plan_calcrate
  Float plan_calccost
  Float agentpercent
  Float overallrate
  Float cost
  Integer kredit_id
  Date datestart
  Date dateend

  def csiSelectCurrentAgentrates(iAgentagrId,iAgentId){//deprecated
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, ifnull((select sum(rate) from agentrate where agentrate.agentkredit_id=agentkredit.id and is_sub=0),0) as agentpercent, agentrate.rate as agent_rate, agentkreditplan.id as plan_id, agentkreditplan.summa as plan_sum, agentkreditplan.debt as plan_debt, agentkreditplan.calcrate as plan_calcrate, agentkreditplan.calccost as plan_calccost, agentkredit.rate as overallrate, agentkreditplan.clientdebt as plan_clientdebt"
    hsSql.from='agentrate join agentkreditplan, agentkredit'
    hsSql.where="agentkredit.id=agentrate.agentkredit_id and agentkreditplan.agentkredit_id=agentkredit.id and agentkreditplan.month=month(curdate()) and agentkreditplan.year=year(curdate())"+
                ((iAgentagrId>0)?' and agentkredit.agentagr_id=:agentagr_id':'')+
                ((iAgentId>0)?' and agentrate.agent_id=:agent_id':'')
    hsSql.order="agentkredit.id asc"

    if(iAgentagrId>0)
      hsLong['agentagr_id']=iAgentagrId
    if(iAgentId>0)
      hsLong['agent_id']=iAgentId

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentrateplanSearch.class)
  }

  def csiSelectCurrentAgentratesForActagent(iAgentagrId,iAgentId,iMonth,iYear){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, ifnull((select sum(rate) from agentrateforperiods where agentrateforperiods.agentkredit_id=agentkredit.id and agentrateforperiods.agentratekreditplan_id=agentratekreditplan.id and is_sub=0),0) as agentpercent, agentrateforperiods.rate as agent_rate, agentratekreditplan.id as plan_id, agentratekreditplan.summa as plan_sum, agentratekreditplan.debt as plan_debt, agentratekreditplan.calcrate as plan_calcrate, agentratekreditplan.calccost as plan_calccost, agentkredit.rate as overallrate, agentratekreditplan.clientdebt as plan_clientdebt"
    hsSql.from='agentratekreditplan join agentrateforperiods, agentkredit'
    hsSql.where="agentkredit.id=agentrateforperiods.agentkredit_id and agentrateforperiods.agentratekreditplan_id=agentratekreditplan.id and agentratekreditplan.agentkredit_id=agentkredit.id"+
                ((iAgentagrId>0)?' and agentkredit.agentagr_id=:agentagr_id':'')+
                ((iAgentId>0)?' and agentrateforperiods.agent_id=:agent_id':'')+
                ((iMonth>0)?' and agentratekreditplan.month=:month':'')+
                ((iYear>0)?' and agentratekreditplan.year=:year':'')
    hsSql.order="agentkredit.id asc"

    if(iAgentagrId>0)
      hsLong['agentagr_id']=iAgentagrId
    if(iAgentId>0)
      hsLong['agent_id']=iAgentId
    if(iMonth>0)
      hsLong['month']=iMonth
    if(iYear>0)
      hsLong['year']=iYear

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentrateplanSearch.class)
  }

  def csiSelectPreviousAgentrates(iAgentagrId,iAgentId,iMonth,iYear){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, ifnull((select sum(rate) from agentrateforperiods where agentrateforperiods.agentkredit_id=agentkredit.id and agentrateforperiods.agentratekreditplan_id=agentratekreditplan.id and is_sub=0),0) as agentpercent, agentrateforperiods.rate as agent_rate, agentratekreditplan.id as plan_id, agentratekreditplan.summa as plan_sum, agentratekreditplan.debt as plan_debt, agentratekreditplan.calcrate as plan_calcrate, agentratekreditplan.calccost as plan_calccost, agentkredit.rate as overallrate, agentratekreditplan.clientdebt as plan_clientdebt"
    hsSql.from='agentratekreditplan join agentrateforperiods, agentkredit'
    hsSql.where="agentkredit.id=agentrateforperiods.agentkredit_id and agentrateforperiods.agentratekreditplan_id=agentratekreditplan.id and agentratekreditplan.agentkredit_id=agentkredit.id and (agentratekreditplan.month<:month or agentratekreditplan.year<:year)"+
                ((iAgentagrId>0)?' and agentkredit.agentagr_id=:agentagr_id':'')+
                ((iAgentId>0)?' and agentrateforperiods.agent_id=:agent_id':'')
    hsSql.order="agentkredit.id asc"

    if(iAgentagrId>0)
      hsLong['agentagr_id'] = iAgentagrId
    if(iAgentId>0)
      hsLong['agent_id'] = iAgentId
    hsLong['month'] = iMonth
    hsLong['year'] = iYear

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentrateplanSearch.class)
  }

  def csiSelectAgentratesByMonth(iAgentagrId,bIsDisplay,iMonth,iYear){//deprecated
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, ifnull((select sum(rate) from agentrate where agentrate.agentkredit_id=agentkredit.id and is_sub=0),0) as agentpercent, agentrate.rate as agent_rate, agentkreditplan.id as plan_id, agentkreditplan.summa as plan_sum, agentkreditplan.debt as plan_debt, agentkreditplan.calcrate as plan_calcrate, agentkreditplan.calccost as plan_calccost, agentkredit.rate as overallrate, agentkreditplan.clientdebt as plan_clientdebt"
    hsSql.from='agentrate join agentkreditplan, agentkredit'
    hsSql.where="agentkredit.id=agentrate.agentkredit_id and agentkreditplan.agentkredit_id=agentkredit.id"+
                ((iAgentagrId>0)?' and agentkredit.agentagr_id=:agentagr_id':'')+
                ((iMonth>0)?' and agentkreditplan.month=:month':'')+
                ((iYear>0)?' and agentkreditplan.year=:year':'')+
                ((bIsDisplay)?' and agentrate.is_display=1':'')
    hsSql.order="agentkredit.id asc"

    if(iAgentagrId>0)
      hsLong['agentagr_id']=iAgentagrId
    if(iMonth>0)
      hsLong['month']=iMonth
    if(iYear>0)
      hsLong['year']=iYear

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentrateplanSearch.class)
  }

  BigDecimal computeAgentSumma(){
    if (!plan_calcrate) return 0.0g
    if (is_sub&&subtype) return plan_sum * (plan_calcrate - agentpercent - plan_calccost) * agent_rate / (plan_calcrate * (100 + agent_rate))
    else if(is_sub&&!subtype) return plan_sum * (plan_calcrate - agentpercent) * agent_rate / (plan_calcrate * (100 + agent_rate))
    else return plan_sum * agent_rate / plan_calcrate

    return 0.0g
  }

  BigDecimal computeClientdebtAgentSumma(){
    if (!plan_calcrate) return 0.0g
    if (is_sub&&subtype) return plan_clientdebt * (plan_calcrate - agentpercent - plan_calccost) * agent_rate / (plan_calcrate * (100 + agent_rate))
    else if(is_sub&&!subtype) return plan_clientdebt * (plan_calcrate - agentpercent) * agent_rate / (plan_calcrate * (100 + agent_rate))
    else return plan_clientdebt * agent_rate / plan_calcrate

    return 0.0g
  }

}