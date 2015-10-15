class AgentfixSearch {
  def searchService
  static mapping = {
    version false
  }
/////////agentfix////////////////////
  Integer id
  Integer agentagr_id
  Integer agent_id
  Date inputdate
  Date paydate
  BigDecimal summa
/////////agent///////////////////////
  String agent_name

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectFixes(iAgentagrId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, agent.name as agent_name"
    hsSql.from='agentfix, agent'
    hsSql.where="agentfix.agent_id=agent.id"+
                ((iAgentagrId>0)?' and agentfix.agentagr_id=:agentagr_id':'')
    hsSql.order="agentfix.paydate desc"

    if(iAgentagrId>0)
      hsLong['agentagr_id']=iAgentagrId

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentfixSearch.class)
  }

  def csiSelectFixesByDatesBetween(iAgentagrId,iAgentId,dMinDate,dMaxDate){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, agent.name as agent_name"
    hsSql.from='agentfix, agent'
    hsSql.where="agentfix.agent_id=agent.id"+
                ((iAgentagrId>0)?' and agentfix.agentagr_id=:agentagr_id':'')+
                ((iAgentId>0)?' and agentfix.agent_id=:agent_id':'')+
                ((dMinDate)?' and agentfix.paydate>=:mindate':'')+
                ((dMaxDate)?' and agentfix.paydate<=:maxdate':'')
    hsSql.order="agentfix.paydate desc"

    if(iAgentagrId>0)
      hsLong['agentagr_id']=iAgentagrId
    if(iAgentId>0)
      hsLong['agent_id']=iAgentId
    if(dMinDate)
      hsString['mindate']=String.format('%tF',dMinDate)
    if(dMaxDate)
      hsString['maxdate']=String.format('%tF',dMaxDate)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,AgentfixSearch.class)
  }

}