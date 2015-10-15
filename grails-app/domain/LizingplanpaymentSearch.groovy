class LizingplanpaymentSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////lizingplanpayment////////////
  Integer id
  Integer lizing_id
  Date paydate
  BigDecimal summa
  Integer modstatus
  Long admin_id
//////////////Company/////////////////////
  String arendator_name
  String arendodatel_name
//////////////Lizing//////////////////////
  String anumber
  Date adate

  def csiSelectLizingsPayments(sCompanyName,iStatus,dStartDate,dEndDate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name"
    hsSql.from='lizingplanpayment, lizing, company c1, company c2'
    hsSql.where="lizingplanpayment.lizing_id=lizing.id and lizing.arendator=c1.id and lizing.arendodatel=c2.id and lizingplanpayment.modstatus>=0"+
                ((sCompanyName!='')?' and c1.name like concat("%",:company_name,"%")':'')+
                ((iStatus>-100)?' and lizingplanpayment.modstatus=:modstatus':'')+
                ((dStartDate)?' and lizingplanpayment.paydate>=:startdate':'')+
                ((dEndDate)?' and lizingplanpayment.paydate<:enddate':'')
    hsSql.order="lizingplanpayment.paydate asc"

    if(sCompanyName!='')
      hsString['company_name']=sCompanyName
    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(dStartDate)
      hsString['startdate']=String.format('%tF',dStartDate)
    if(dEndDate)
      hsString['enddate']=String.format('%tF',dEndDate)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'lizingplanpayment.id',true,LizingplanpaymentSearch.class)
  }

 def csiFindDefaultDataForPayment(iLizingId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='lizingplanpayment'
    hsSql.where="lizingplanpayment.lizing_id=:lizing_id and lizingplanpayment.modstatus=0 and is_insurance=0 and (select id from payrequest where agrpayment_id=lizingplanpayment.id) is null"
    hsSql.order="lizingplanpayment.paydate asc"

    hsLong['lizing_id'] = iLizingId

    searchService.fetchData(hsSql,hsLong,null,null,null,Lizingplanpayment.class,1)[0]
  }

}