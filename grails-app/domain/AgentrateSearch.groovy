class AgentrateSearch {
  def searchService
  static mapping = {
    version false
  }
/////////agentrate//////////////////////
  Integer id
  Integer agentkredit_id
  Integer agent_id
/////////agentkredit////////////////////
  Integer agentagr_id
  Integer kredit_id
/////////agent//////////////////////////
  String agent_name

  def csiSelectAgents(iAgentagrId){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsLong=[:]

    hsSql.select="*, agent.name as agent_name"
    hsSql.from='agentrate, agentkredit, agent'
    hsSql.where="agentrate.agentkredit_id=agentkredit.id and agentrate.agent_id=agent.id"+
                ((iAgentagrId>=0)?' and agentkredit.agentagr_id=:agentagr_id':'')
    hsSql.group="agent.id"
    hsSql.order="agentrate.id asc"

    if(iAgentagrId>=0)
      hsLong['agentagr_id']=iAgentagrId

		searchService.fetchData(hsSql,hsLong,null,null,null,AgentrateSearch.class)
  }

}