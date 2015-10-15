class AgentplanSearch {
  def searchService
  static mapping = { version false }
/////////actagent///////////////////////
  Integer id
  Integer agentagr_id
  Integer agent_id
  Integer month
  Integer year
  Date inputdate
  BigDecimal summa
  BigDecimal summaprev
  BigDecimal summafix
  BigDecimal paid
  BigDecimal agentfix
  Integer modstatus
/////////general////////////////////////
  String agent_name
  String bank_name
  String client_name
  BigDecimal actpaidsum

  def csiSelectAgents(iAgentagrId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="a.*, agent.name as agent_name, (select sum(actpaid+overpaid) from actagent where actagent.agent_id=a.agent_id and actagent.agentagr_id=a.agentagr_id) as actpaidsum, '' as bank_name, '' as client_name"
    hsSql.from='actagent a, agent'
    hsSql.where="a.agent_id=agent.id and a.id=(select max(id) from actagent where agentagr_id=a.agentagr_id and agent_id=a.agent_id and is_report=0)"+
                ((iAgentagrId>0)?' and a.agentagr_id=:agentagr_id':'')
    hsSql.order="agent.name asc"

    if(iAgentagrId>0)
      hsLong['agentagr_id']=iAgentagrId

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentplanSearch.class)
  }

  def csiSelectAgentagrs(iAgentId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="a.*, client.name as client_name, bank.name as bank_name,'' as agent_name, (select sum(actpaid+overpaid) from actagent where actagent.agent_id=a.agent_id and actagent.agentagr_id=a.agentagr_id) as actpaidsum"
    hsSql.from='actagent a join agentagr on (a.agentagr_id=agentagr.id) join bank on (agentagr.bank_id=bank.id) join client on (agentagr.client_id=client.id)'
    hsSql.where="a.id=(select max(id) from actagent where agentagr_id=a.agentagr_id and agent_id=a.agent_id and is_report=0)"+
                ((iAgentId>0)?' and a.agent_id=:agent_id':'')
    hsSql.order="a.id desc"

    if(iAgentId>0)
      hsLong['agent_id']=iAgentId

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentplanSearch.class)
  }

}