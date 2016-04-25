class ServiceSearch {
  def searchService
  static mapping = { version false }

//////////////service/////////////////////
  Integer id
  Integer zcompany_id
  Integer ecompany_id
  Integer atype
  Integer asort
  Date inputdate
  Date adate
  Date enddate
  String anumber
  Long summa
  Integer payterm
  Integer paycondition
  Integer paytermcondition
  String zbank_id
  String ebank_id
  Integer prolongcondition
  Integer prolongterm
  String comment
  Integer modstatus
//////////////Company/////////////////////
  String zcompany_name
  String ecompany_name

  def csiSelectServices(iId,sZCompanyName,sECompanyName,iServicetype,iAsort,iStatus,dDate,iVision,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as zcompany_name, c2.name as ecompany_name"
    hsSql.from='service, company as c1, company as c2'
    hsSql.where="service.zcompany_id=c1.id and service.ecompany_id=c2.id"+
                (iId>0?' and service.id=:sid':'')+
                ((sZCompanyName!='')?' and c1.name like concat("%",:zcompany_name,"%")':'')+
                ((sECompanyName!='')?' and c2.name like concat("%",:ecompany_name,"%")':'')+
                ((iServicetype>0)?' and service.atype=:atype':'')+
                ((iAsort>0)?' and service.asort=:asort':'')+
                ((iStatus>-100)?' and service.modstatus=:modstatus':'')+
                ((dDate)?' and service.enddate<:enddate':'')+
                (iVision>0?' and (if(c1.is_holding=0,0,c1.visualgroup_id)=:visualgroup_id or if(c2.is_holding=0,0,c2.visualgroup_id)=:visualgroup_id)':'')
    hsSql.order="service.id desc"

    if(iId>0)
      hsLong['sid'] = iId
    if(sZCompanyName!='')
      hsString['zcompany_name'] = sZCompanyName
    if(sECompanyName!='')
      hsString['ecompany_name'] = sECompanyName
    if(iServicetype>0)
      hsLong['atype'] = iServicetype
    if(iAsort>0)
      hsLong['asort'] = iAsort
    if(iStatus>-100)
      hsLong['modstatus'] = iStatus
    if(dDate)
      hsString['enddate'] = String.format('%tF',dDate)
    if(iVision>0)
      hsLong['visualgroup_id']=iVision

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'service.id',true,ServiceSearch.class)
  }

  def csiFindByZcompanyIdAndModstatusAndType(iZcompanyId,iModstatus,iServicetype){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, '' as zcompany_name, c2.name as ecompany_name"
    hsSql.from='service, company as c2'
    hsSql.where="service.ecompany_id=c2.id and atype=1"+
                ((iZcompanyId>0)?' and service.zcompany_id=:cId':'')+
                ((iServicetype>0)?' and service.atype=:atype':'')+
                ((iModstatus>-100)?' and service.modstatus=:modstatus':'')
    hsSql.order="service.id desc"

    if(iZcompanyId>0)
      hsLong['cId'] = iZcompanyId
    if(iServicetype>0)
      hsLong['atype'] = iServicetype
    if(iModstatus>-100)
      hsLong['modstatus'] = iModstatus

    searchService.fetchData(hsSql,hsLong,null,null,null,ServiceSearch.class)
  }

  def csiSelectServiceSummary(Date dateStart, Date dateEnd, izCompanyId, ieCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as zcompany_name, c2.name as ecompany_name"
    hsSql.from='service, company c1, company c2'
    hsSql.where="service.zcompany_id=c1.id and service.ecompany_id=c2.id and service.modstatus>=0"+
                (ieCompanyId>0?' and service.ecompany_id=:ecompany_id':'')+
                (izCompanyId>0?' and service.zcompany_id=:zcompany_id':'')+
                (dateStart?' and service.adate>=:datestart':'')+
                (dateEnd?' and service.adate<=:dateend and service.enddate>=:dateend':'')
    hsSql.order="service.id desc"

    if(ieCompanyId>0)
      hsLong['ecompany_id'] = ieCompanyId
    if(izCompanyId>0)
      hsLong['zcompany_id'] = izCompanyId
    if (dateStart)
      hsString['datestart'] = String.format('%tF',dateStart)
    if (dateEnd)
      hsString['dateend'] = String.format('%tF',dateEnd)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,ServiceSearch.class)
  }
}