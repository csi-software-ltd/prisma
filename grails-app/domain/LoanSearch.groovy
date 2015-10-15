class LoanSearch {
  def searchService
  static mapping = { version false }

//////////////loan////////////////////////
  Integer id
  Integer loantype
  Integer client
  Long client_pers
  Integer lender
  Long lender_pers
  String anumber
  Date inputdate
  Date adate
  Date startdate
  Date enddate
  Long summa
  Double rate
  Integer valuta_id
  Long debt
  BigDecimal bodydebt
  Integer loanterm
  Integer payterm
  Integer paytermcondition
  Integer repaymenttype_id
  Integer monthnumber
  Integer modstatus
  Integer is_cbcalc
  String comment
//////////////Company/////////////////////
  String client_name
  String clientpers_name
  String lender_name
  String lenderpers_name

  def csiSelectLoans(sClientName,sLenderName,iLoanType,iStatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as client_name, c2.name as lender_name, p1.shortname as clientpers_name, p2.shortname as lenderpers_name"
    hsSql.from='loan left join company c1 on (loan.client=c1.id) left join company c2 on (loan.lender=c2.id) left join pers p1 on (loan.client_pers=p1.id) left join pers p2 on (loan.lender_pers=p2.id)'
    hsSql.where="1=1"+
                ((sClientName!='')?' and (c1.name like concat("%",:client_name,"%") or p1.shortname like concat("%",:client_name,"%"))':'')+
                ((sLenderName!='')?' and (c2.name like concat("%",:lender_name,"%") or p2.shortname like concat("%",:lender_name,"%"))':'')+
                ((iLoanType>-100)?' and loan.loantype=:loantype':'')+
                ((iStatus>-100)?' and loan.modstatus=:modstatus':'')
    hsSql.order="loan.id desc"

    if(sClientName!='')
      hsString['client_name'] = sClientName
    if(sLenderName!='')
      hsString['lender_name'] = sLenderName
    if(iLoanType>-100)
      hsLong['loantype'] = iLoanType
    if(iStatus>-100)
      hsLong['modstatus'] = iStatus

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'loan.id',true,LoanSearch.class)
  }

  def csiFindCompanyLoans(iCompanyId,iStatus=1){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as client_name, c2.name as lender_name, p1.shortname as clientpers_name, p2.shortname as lenderpers_name"
    hsSql.from='loan left join company c1 on (loan.client=c1.id) left join company c2 on (loan.lender=c2.id) left join pers p1 on (loan.client_pers=p1.id) left join pers p2 on (loan.lender_pers=p2.id)'
    hsSql.where=((iStatus>0)?'loan.modstatus=1':'loan.modstatus=0')+
                ((iCompanyId>0)?' and (loan.client=:company or loan.lender=:company)':'')
    hsSql.order="loan.id desc"

    if(iCompanyId>0)
      hsLong['company']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,LoanSearch.class)
  }

}
