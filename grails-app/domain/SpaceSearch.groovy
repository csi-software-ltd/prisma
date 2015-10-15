class SpaceSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////space////////////////////////
  Integer id
  Integer mainagr_id
  Integer spacetype_id
  Integer arendator
  Integer arendodatel
  String city
  String fulladdress
  String shortaddress
  String anumber
  Date adate
  Date inputdate
  Date enddate
  Integer prolongcondition
  Integer prolongterm
  Integer monthnotification
  String description
  Integer asort
  Integer subsub_id
  Integer arendatype_id
  Integer paystatus
  Integer modstatus
  Integer is_nosubrenting
  Integer subspaceqty
  Integer is_nopayment
  Integer is_adrsame
  String comment
  Integer project_id
  Double area
  Integer payterm
  String bank_id
  Double ratemeter
  Integer is_addpayment
  BigDecimal rate
  BigDecimal actrate
  BigDecimal ratedop
  BigDecimal debt
  Date debtdate
  Long responsible
  Integer permitstatus
  Integer workstatus
  String prolongcomment
  Date permitdate
  Date workdate
  Long workuser
//////////////Company/////////////////////
  String arendator_name
  String arendodatel_name
  Integer payreqstatus

  def csiFindSpace(iCompanyId,iAsort,iMainAgrId=0){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, 0 as payreqstatus"
    hsSql.from='space,company as c1, company as c2'
    hsSql.where="space.arendator=c1.id and space.arendodatel=c2.id"+
                ((iCompanyId>0)?' and (space.arendator=:company_id or space.arendodatel=:company_id)':'')+
                ((iAsort>-100)?' and space.asort=:asort':'')+
                ((iMainAgrId>0)?' and space.mainagr_id=:mainagr_id and space.permitstatus>=0':'')
    hsSql.order="space.id desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId
    if(iAsort>-100)
      hsLong['asort']=iAsort
    if(iMainAgrId>0)
      hsLong['mainagr_id']=iMainAgrId

    searchService.fetchData(hsSql,hsLong,null,null,null,SpaceSearch.class)
  }

  def csiSelectSpaces(iId,sAddress,sCompanyName,iArendatype,iSpacetype,iProjectId,lResponsible,iDebt,iIsnds,iSame,iStatus,dDate,sAnumber,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, 0 as payreqstatus"
    hsSql.from='space, company as c1, company as c2'
    hsSql.where="space.arendator=c1.id and space.arendodatel=c2.id"+
                (iId>0?' and space.id=:sid':'')+
                ((sAddress!='')?' and space.fulladdress like concat("%",:address,"%")':'')+
                ((sCompanyName!='')?' and (c1.name like concat("%",:company_name,"%") or c2.name like concat("%",:company_name,"%"))':'')+
                ((iArendatype>0)?' and space.arendatype_id=:arendatype_id':'')+
                ((iSpacetype>0)?' and space.spacetype_id=:spacetype_id':'')+
                ((iProjectId>0)?' and space.project_id=:project_id':'')+
                ((lResponsible>0)?' and space.responsible=:responsible':'')+
                (iDebt>0?' and ifnull((select sum(summa) from spacecalculation where spacecalculation.space_id=space.id and is_dop=0),0)>ifnull((select sum(summa) from payrequest where payrequest.agreementtype_id=2 and payrequest.agreement_id=space.id and payrequest.is_dop=0 and payrequest.modstatus>=2 and payrequest.paytype=1),0)':'')+
                ((iSame>0)?' and space.is_adrsame>0':'')+
                (iIsnds==1?' and c2.taxoption_id=1':iIsnds==0?' and c2.taxoption_id>1':'')+
                ((iStatus>-100)?' and space.modstatus=:modstatus':'')+
                ((dDate)?' and space.enddate<:enddate':'')+
                ((sAnumber!='')?' and space.anumber like concat("%",:anumber,"%")':'')
    hsSql.order="if(space.arendatype_id=1,space.id,if(space.subsub_id=0,space.mainagr_id,space.subsub_id)) desc, space.arendatype_id asc, if(space.subsub_id=0,space.id,space.mainagr_id) desc, space.subsub_id asc"

    if(iId>0)
      hsLong['sid']=iId
    if(sAddress!='')
      hsString['address']=sAddress
    if(sCompanyName!='')
      hsString['company_name']=sCompanyName
    if(iArendatype>0)
      hsLong['arendatype_id']=iArendatype
    if(iSpacetype>0)
      hsLong['spacetype_id']=iSpacetype
    if(iProjectId>0)
      hsLong['project_id']=iProjectId
    if(lResponsible>0)
      hsLong['responsible']=lResponsible
    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(dDate)
      hsString['enddate']=String.format('%tF',dDate)
    if(sAnumber!='')
      hsString['anumber']=sAnumber

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'space.id',true,SpaceSearch.class)
  }

  def csiSelectSpacesForPayrequest(sCompanyName,iStatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, ifnull((select id from payrequest where payrequest.agreement_id=space.id and payrequest.agreementtype_id=2 and payrequest.is_dop=0 and payrequest.modstatus>=0 and paydate > curdate() - INTERVAL 35 DAY and ((MOD(MONTH(paydate),12)=MOD(MONTH(curdate())+if(space.payterm>=DAYOFMONTH(curdate()),0,1),12) and DAYOFMONTH(paydate)<=space.payterm) or (MOD(MONTH(paydate),12)=MOD(MONTH(curdate())-1+if(space.payterm>=DAYOFMONTH(curdate()),0,1),12) and DAYOFMONTH(paydate)>space.payterm))),0) as payreqstatus"
    hsSql.from='space, company as c1, company as c2'
    hsSql.where="space.arendator=c1.id and space.arendodatel=c2.id and space.modstatus=1 and space.is_nopayment=0"+
                ((sCompanyName!='')?' and c1.name like concat("%",:company_name,"%")':'')+
                ((iStatus<-100)?'':' and ifnull((select id from payrequest where payrequest.agreement_id=space.id and payrequest.agreementtype_id=2 and payrequest.is_dop=0 and payrequest.modstatus>=0 and paydate > curdate() - INTERVAL 35 DAY and ((MOD(MONTH(paydate),12)=MOD(MONTH(curdate())+if(space.payterm>=DAYOFMONTH(curdate()),0,1),12) and DAYOFMONTH(paydate)<=space.payterm) or (MOD(MONTH(paydate),12)=MOD(MONTH(curdate())-1+if(space.payterm>=DAYOFMONTH(curdate()),0,1),12) and DAYOFMONTH(paydate)>space.payterm))),0)'+(iStatus==1?'>':'=')+'0')
    hsSql.order="if(space.payterm>=DAYOFMONTH(curdate()),1,0) desc, space.payterm asc"

    if(sCompanyName!='')
      hsString['company_name']=sCompanyName

    searchService.fetchDataByPages(hsSql,null,null,null,hsString,null,null,iMax,iOffset,'space.id',true,SpaceSearch.class)
  }

  def csiSelectSpaceProlongs(hsRequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, 0 as payreqstatus"
    hsSql.from='space, company as c1, company as c2'
    hsSql.where="space.arendator=c1.id and space.arendodatel=c2.id and space.modstatus >= 0 and space.prolongcondition in (1,3) and space.enddate <= (curdate() + INTERVAL IF(space.monthnotification!=0,space.monthnotification,1)*30 DAY)"+
                ((hsRequest?.permitstatus>-100)?' and space.permitstatus=:permitstatus':' and space.permitstatus in (0,1)')
    hsSql.order="space.enddate asc"

    if(hsRequest?.permitstatus>-100)
      hsLong['permitstatus']=hsRequest.permitstatus

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'space.id',true,SpaceSearch.class)
  }

  def csiFindProlonged(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="space.id"
    hsSql.from='space'
    hsSql.where="space.modstatus >= 0 and space.prolongcondition in (1,3) and space.enddate > (curdate() + INTERVAL IF(space.monthnotification!=0,space.monthnotification,1)*30 DAY) and space.permitstatus!=0"
    hsSql.order="space.enddate asc"

    searchService.fetchData(hsSql,null,null,null,null)
  }

  def csiSelectSpaceSummary(Date dateStart, Date dateEnd, iArendatorId, iArendodatelId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, 0 as payreqstatus"
    hsSql.from='space, company c1, company c2'
    hsSql.where="space.arendator=c1.id and space.arendodatel=c2.id and space.modstatus>=0"+
                (iArendodatelId>0?' and space.arendodatel=:arendodatel':'')+
                (iArendatorId>0?' and space.arendator=:arendator':'')+
                (dateStart?' and space.adate>=:datestart':'')+
                (dateEnd?' and space.adate<=:dateend and space.enddate>=:dateend':'')
    hsSql.order="space.id desc"

    if(iArendodatelId>0)
      hsLong['arendodatel'] = iArendodatelId
    if(iArendatorId>0)
      hsLong['arendator'] = iArendatorId
    if (dateStart)
      hsString['datestart'] = String.format('%tF',dateStart)
    if (dateEnd)
      hsString['dateend'] = String.format('%tF',dateEnd)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,SpaceSearch.class)
  }
}