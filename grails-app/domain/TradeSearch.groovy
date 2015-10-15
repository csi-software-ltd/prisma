class TradeSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////lizing////////////////////////
  Integer id
  Integer tradetype
  Integer tradesort
  Integer tradecat_id
  Integer client
  Integer supplier
  String anumber
  Date adate
  Date inputdate
  Date enddate
  String description
  Integer modstatus
  String comment
  Integer summa
  Integer paytype
  Integer debt
  Date debtdate
  Long responsible
//////////////Company/////////////////////
  String client_name
  String supplier_name

  def csiSelectTrades(sInn,sCompanyName,lResponsible,iDebt,iTradesort,iTradetype,iStatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as client_name, c2.name as supplier_name"
    hsSql.from='trade, company as c1, company as c2'
    hsSql.where="trade.client=c1.id and trade.supplier=c2.id"+
                ((sInn!='')?' and (c1.inn like concat("%",:inn,"%") or c2.inn like concat("%",:inn,"%"))':'')+
                ((sCompanyName!='')?' and (c1.name like concat("%",:company_name,"%") or c2.name like concat("%",:company_name,"%"))':'')+
                ((lResponsible>0)?' and trade.responsible=:responsible':'')+
                ((iDebt>0)?' and trade.debt>0':'')+
                ((iTradesort>-100)?' and trade.tradesort=:tradesort':'')+
                ((iTradetype>-100)?' and trade.tradetype=:tradetype':'')+
                ((iStatus>-100)?' and trade.modstatus=:modstatus':'')
    hsSql.order="trade.id desc"

    if(sInn!='')
      hsString['inn']=sInn
    if(sCompanyName!='')
      hsString['company_name']=sCompanyName
    if(lResponsible>0)
      hsLong['responsible']=lResponsible
    if(iTradesort>-100)
      hsLong['tradesort']=iTradesort
    if(iTradetype>-100)
      hsLong['tradetype']=iTradetype
    if(iStatus>-100)
      hsLong['modstatus']=iStatus

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'trade.id',true,TradeSearch.class)
  }

  def csiSelectCompanyTrades(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as client_name, c2.name as supplier_name"
    hsSql.from='trade, company as c1, company as c2'
    hsSql.where="trade.client=c1.id and trade.supplier=c2.id"+
                ((iCompanyId>0)?' and (client=:company_id or supplier=:company_id)':'')
    hsSql.order="trade.id desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,TradeSearch.class)
  }

  def csiSelectTradeSummary(Date dateStart, Date dateEnd, iClientId, iSupplierId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as client_name, c2.name as supplier_name"
    hsSql.from='trade, company c1, company c2'
    hsSql.where="trade.client=c1.id and trade.supplier=c2.id and trade.modstatus>=0"+
                (iSupplierId>0?' and trade.supplier=:supplier':'')+
                (iClientId>0?' and trade.client=:client':'')+
                (dateStart?' and trade.adate>=:datestart':'')+
                (dateEnd?' and trade.adate<=:dateend and ifnull(trade.enddate,:dateend)>=:dateend':'')
    hsSql.order="trade.id desc"

    if(iSupplierId>0)
      hsLong['supplier'] = iSupplierId
    if(iClientId>0)
      hsLong['client'] = iClientId
    if (dateStart)
      hsString['datestart'] = String.format('%tF',dateStart)
    if (dateEnd)
      hsString['dateend'] = String.format('%tF',dateEnd)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,TradeSearch.class)
  }
}