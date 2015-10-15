class KreditpaymentSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////kreditpayment//////////////////
  Integer id
  Integer kredit_id
  Date paydate
  BigDecimal summa
  BigDecimal summaperc
  BigDecimal summarub
  BigDecimal summapercrub
  BigDecimal rate
  BigDecimal paid
  Date paiddate
  BigDecimal percpaid
  Integer paidstatus
  Date percpaiddate
  Integer percpaidstatus
  Integer modstatus
  Integer paidmonth
  Long admin_id
//////////////Admin///////////////////////
  String admin_name
//////////////Company/////////////////////
  String client_name
//////////////Bank////////////////////////
  String bank_name
//////////////Kredit//////////////////////
  String anumber
  Date adate
  Integer is_real
  Integer is_tech
  Integer is_realtech

  def csiFindKreditPayment(iKreditId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name, '' as client_name, '' as bank_name, '' as anumber, null as adate, 0 as is_real, 0 as is_tech, 0 as is_realtech"
    hsSql.from='kreditpayment left join user u1 on (u1.id=kreditpayment.admin_id)'
    hsSql.where="1=1"+
                ((iKreditId>0)?' and kreditpayment.kredit_id=:kredit_id':'')
    hsSql.order="kreditpayment.paydate asc"

    if(iKreditId>0)
      hsLong['kredit_id']=iKreditId

    searchService.fetchData(hsSql,hsLong,null,null,null,KreditpaymentSearch.class)
  }

  def csiSelectKreditsPayments(sCompanyName,iStatus,dStartDate,dEndDate,iKredsort,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, company.name as client_name, bank.name as bank_name, '' as admin_name"
    hsSql.from='kreditpayment, kredit, company, bank'
    hsSql.where="kreditpayment.kredit_id=kredit.id and kredit.client=company.id and kredit.bank_id=bank.id"+
                ((sCompanyName!='')?' and company.name like concat("%",:company_name,"%")':'')+
                ((iStatus>-100)?' and ((kreditpayment.paidstatus=:modstatus and kreditpayment.summarub>0) or (kreditpayment.percpaidstatus=:modstatus and kreditpayment.summapercrub>0))':'')+
                ((dStartDate)?' and kreditpayment.paydate>=:startdate':'')+
                ((dEndDate)?' and kreditpayment.paydate<:enddate':'')+
                (iKredsort==1?' and kredit.is_real=1':iKredsort==2?' and kredit.is_tech=1':iKredsort==3?' and kredit.is_realtech=1':'')
    hsSql.order="kreditpayment.paydate asc"

    if(sCompanyName!='')
      hsString['company_name']=sCompanyName
    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(dStartDate)
      hsString['startdate']=String.format('%tF',dStartDate)
    if(dEndDate)
      hsString['enddate']=String.format('%tF',dEndDate)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'kreditpayment.id',true,KreditpaymentSearch.class)
  }

 def csiFindDefaultDataForPayment(iKreditId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='kreditpayment'
    hsSql.where="kreditpayment.kredit_id=:kredit_id and kreditpayment.modstatus in (0,1) and (select id from payrequest where agrpayment_id=kreditpayment.id) is null"
    hsSql.order="kreditpayment.paydate asc"

    hsLong['kredit_id'] = iKreditId

    searchService.fetchData(hsSql,hsLong,null,null,null,Kreditpayment.class,1)[0]
  }

}