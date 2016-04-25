class LizingSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////lizing////////////////////////
  Integer id
  Integer arendator
  Integer arendodatel
  Integer owner
  Integer creditor
  String cbank_id
  Integer lizsort
  String anumber
  Date adate
  Date inputdate
  Date enddate
  String description
  Integer modstatus
  Integer cessionstatus
  String comment
  BigDecimal summa
  BigDecimal initialfee
  BigDecimal startsaldo
  Date startsaldodate
  BigDecimal restfee
  Integer rate
  Integer debt
  Date debtdate
  Integer project_id
  Integer car_id
  Long responsible
//////////////Company/////////////////////
  String arendator_name
  String arendodatel_name
  String creditor_name

  String toString(){
    "$arendator_name - $arendodatel_name - $anumber от ${String.format('%td.%<tm.%<tY',adate)}"
  }

  def csiSelectLizings(iId,sCompanyName,lResponsible,iDebt,iLizsort,iStatus,iProjectId,iCarId,iCession,iVision,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, c3.name as creditor_name"
    hsSql.from='lizing left join company c3 on (lizing.creditor=c3.id), company as c1, company as c2'
    hsSql.where="lizing.arendator=c1.id and lizing.arendodatel=c2.id"+
                (iId>0?' and lizing.id=:lid':'')+
                (sCompanyName!=''?' and (c1.name like concat("%",:company_name,"%") or c2.name like concat("%",:company_name,"%"))':'')+
                (lResponsible>0?' and lizing.responsible=:responsible':'')+
                (iDebt>0?' and lizing.debt>0':'')+
                (iLizsort>-100?' and lizing.lizsort=:lizsort':'')+
                (iStatus>-100?' and lizing.modstatus=:modstatus':'')+
                (iProjectId>0?' and lizing.project_id=:project_id':'')+
                (iCarId>0?' and lizing.car_id=:car_id':'')+
                (iCession>0?' and lizing.cessionstatus>0':'')+
                (iVision>0?' and (IFNULL(c3.visualgroup_id,c1.visualgroup_id)=:visualgroup_id or if(c2.is_holding=0,0,c2.visualgroup_id)=:visualgroup_id)':'')
    hsSql.order="lizing.adate desc"

    if(iId>0)
      hsLong['lid'] = iId
    if(sCompanyName!='')
      hsString['company_name'] = sCompanyName
    if(lResponsible>0)
      hsLong['responsible'] = lResponsible
    if(iLizsort>-100)
      hsLong['lizsort'] = iLizsort
    if(iStatus>-100)
      hsLong['modstatus'] = iStatus
    if(iProjectId>0)
      hsLong['project_id'] = iProjectId
    if(iCarId>0)
      hsLong['car_id'] = iCarId
    if(iVision>0)
      hsLong['visualgroup_id']=iVision

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'lizing.id',true,LizingSearch.class)
  }

  def csiSelectCompanyLizings(iCompanyId,iStatus=1){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, '' as creditor_name"
    hsSql.from='lizing, company as c1, company as c2'
    hsSql.where="if(lizing.creditor>0,lizing.creditor,lizing.arendator)=c1.id and lizing.arendodatel=c2.id"+
                ((iCompanyId>0)?' and (if(lizing.creditor>0,lizing.creditor,lizing.arendator)=:company_id or arendodatel=:company_id)':'')+
                ((iStatus>0)?' and lizing.modstatus=1':' and lizing.modstatus=0')
    hsSql.order="lizing.id desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,LizingSearch.class)
  }

  def csiSelectLizingSummary(Date dateStart, Date dateEnd, iArendatorId, iArendodatelId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, '' as creditor_name"
    hsSql.from='lizing, company c1, company c2'
    hsSql.where="if(lizing.creditor>0,lizing.creditor,lizing.arendator)=c1.id and lizing.arendodatel=c2.id and lizing.modstatus>=0"+
                (iArendodatelId>0?' and lizing.arendodatel=:arendodatel':'')+
                (iArendatorId>0?' and lizing.arendator=:arendator':'')+
                (dateStart?' and lizing.adate>=:datestart':'')+
                (dateEnd?' and lizing.adate<=:dateend and lizing.enddate>=:dateend':'')
    hsSql.order="lizing.id desc"

    if(iArendodatelId>0)
      hsLong['arendodatel'] = iArendodatelId
    if(iArendatorId>0)
      hsLong['arendator'] = iArendatorId
    if (dateStart)
      hsString['datestart'] = String.format('%tF',dateStart)
    if (dateEnd)
      hsString['dateend'] = String.format('%tF',dateEnd)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,LizingSearch.class)
  }

  def csiSelectNewCessionLizings(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, '' as creditor_name"
    hsSql.from='lizing, company c1, company c2'
    hsSql.where="lizing.arendator=c1.id and lizing.arendodatel=c2.id and lizing.cessionstatus=0 and lizing.modstatus=1"
    hsSql.order="c1.name asc, c2.name asc, lizing.anumber asc"

    searchService.fetchData(hsSql,null,null,null,null,LizingSearch.class)
  }

  def csiSelectLizing(iId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, '' as creditor_name"
    hsSql.from='lizing, company c1, company c2'
    hsSql.where="lizing.arendator=c1.id and lizing.arendodatel=c2.id and lizing.id=:lid"
    hsSql.order="lizing.id desc"

    hsLong['lid'] = iId

    searchService.fetchData(hsSql,hsLong,null,null,null,LizingSearch.class)
  }
}