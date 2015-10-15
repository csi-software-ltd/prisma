class LoanpaymentSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////loanpayment//////////////////
  Integer id
  Integer loan_id
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

  def csiFindKreditPayment(iLoanId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='loanpayment left join user u1 on (u1.id=loanpayment.admin_id)'
    hsSql.where="1=1"+
                ((iLoanId>0)?' and loanpayment.loan_id=:loan_id':'')
    hsSql.order="loanpayment.paydate asc"

    if(iLoanId>0)
      hsLong['loan_id']=iLoanId

    searchService.fetchData(hsSql,hsLong,null,null,null,LoanpaymentSearch.class)
  }
}