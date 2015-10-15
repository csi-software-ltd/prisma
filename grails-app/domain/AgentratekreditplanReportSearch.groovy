class AgentratekreditplanReportSearch {
  def searchService
  static mapping = { version false }
/////////agentratekreditplan//////////////////////
  Integer id
  Integer agentkredit_id
  Integer month
  Integer year
  BigDecimal debt
  BigDecimal clientdebt
  Date datestart
  Date dateend
  Integer modstatus
  BigDecimal summa
  Float calcrate
  Float calccost
/////////general/////////////////////////////////
  Integer agentagr_id
  Integer kredit_id
  Float akr_rate
  Float akr_cost
  Integer client
  String anumber
  Date adate
  String clientname

  def csiSelectReportPeriods(dDateStart,dDateEnd){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, agentkredit.rate as akr_rate, agentkredit.cost as akr_cost, company.name as clientname"
    hsSql.from='agentratekreditplan join agentkredit on (agentratekreditplan.agentkredit_id=agentkredit.id) join kredit on (agentkredit.kredit_id=kredit.id) join company on (kredit.client=company.id)'
    hsSql.where="1=1"+
                ((dDateStart)?' and ((agentratekreditplan.month>=:month and agentratekreditplan.year=:year) or agentratekreditplan.year>:year)':'')+
                ((dDateEnd)?' and ((agentratekreditplan.month<=:monthend and agentratekreditplan.year=:yearend) or agentratekreditplan.year<:yearend)':'')
    hsSql.order="agentratekreditplan.id asc"

    if(dDateStart){
      hsLong['month'] = dDateStart.getMonth()+1
      hsLong['year'] = dDateStart.getYear()+1900
    }
    if(dDateEnd){
      hsLong['monthend'] = dDateEnd.getMonth()+1
      hsLong['yearend'] = dDateEnd.getYear()+1900
    }

    searchService.fetchData(hsSql,hsLong,null,null,null,AgentratekreditplanReportSearch.class)
  }

  BigDecimal recieveSS(){
    if (!calcrate) return 0.0g
    Agentrateforperiods ar = Agentrateforperiods.findByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(agentkredit_id,id,1)
    if (!ar) return (summa + clientdebt) * (calcrate - (Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(agentkredit_id,id,0).sum{ it.rate }?:0)) / calcrate
    else if (ar.subtype) return (summa + clientdebt) * (100 * (calcrate - (Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(agentkredit_id,id,0).sum{ it.rate }?:0)) + calccost * ar.rate) / (calcrate * (100 + ar.rate))
    else return 100 * (summa + clientdebt) * (calcrate - (Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(agentkredit_id,id,0).sum{ it.rate }?:0)) / (calcrate * (100 + ar.rate))
  }

  BigDecimal recieveCost(){
    if (!calcrate) return 0.0g
    (summa + clientdebt) * calccost / calcrate
  }

  BigDecimal recieveProfit(){
    recieveSS() - recieveCost()
  }
}