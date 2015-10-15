class EnquirySearch {
  def searchService
  static mapping = {
    version false
  }

//////////////enquiry/////////////////////
  Integer id
  Integer company_id
  String nomer
  Integer whereto
  String bank_id
  String taxinspection_id
  Date inputdate
  Date startdate
  Integer term
  Date termdate
  Date enddate
  Date ondate
  Integer modstatus
  Integer enqtype_id
  Integer accounttype
  Integer valuta_id
  String endetails
  String comment
  Integer admin_id
//////////////Company/////////////////////
  String company_name
  String bank_name
  String inspection_name
  String inspection_district

  def csiSelectEnqueries(sCompanyName,sBankId,sTaxInspectionId,iWereto,iStatus,dInputStart,dInputEnd,dTermdate,dOndate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="enquiry.*, company.name as company_name, ifnull(bank.name,'') as bank_name, ifnull(taxinspection.name,'') as inspection_name, ifnull(taxinspection.district,'') as inspection_district"
    hsSql.from='enquiry join company on (enquiry.company_id=company.id) left join bank on (enquiry.bank_id=bank.id) left join taxinspection on (enquiry.taxinspection_id=taxinspection.id)'
    hsSql.where="1=1"+
                ((sCompanyName!='')?' and company.name like concat("%",:company_name,"%")':'')+
                ((sBankId!='')?' and enquiry.bank_id=:bank_id':'')+
                ((sTaxInspectionId!='')?' and enquiry.taxinspection_id=:taxinspection_id':'')+
                ((iWereto>-100)?' and enquiry.whereto=:whereto':'')+
                ((iStatus>-100)?' and enquiry.modstatus=:modstatus':'')+
                (dInputStart?' and enquiry.inputdate>=:inputdatestart':'')+
                (dInputEnd?' and enquiry.inputdate<=:inputdateend':'')+
                (dTermdate?' and enquiry.termdate=:termdate':'')+
                (dOndate?' and enquiry.ondate=:ondate':'')
    hsSql.order="enquiry.id desc"

    if(sCompanyName!='')
      hsString['company_name']=sCompanyName
    if(sBankId!='')
      hsString['bank_id']=sBankId
    if(sTaxInspectionId!='')
      hsString['taxinspection_id']=sTaxInspectionId
    if(iWereto>-100)
      hsLong['whereto']=iWereto
    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(dInputStart)
      hsString['inputdatestart']=String.format('%tF',dInputStart)
    if(dInputEnd)
      hsString['inputdateend']=String.format('%tF',dInputEnd+1)
    if(dTermdate)
      hsString['termdate']=String.format('%tF',dTermdate)
    if(dOndate)
      hsString['ondate']=String.format('%tF',dOndate)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'enquiry.id',true,EnquirySearch.class)
  }

  def csiSelectTypes(iType,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='enqtype'
    hsSql.where="1=1"+
              ((iType>0)?' AND type=:type':'')
    hsSql.order="name asc"

    if(iType>0)
      hsLong['type'] = iType

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'id',true,Enqtype.class)
  }
}