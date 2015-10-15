class LoanhistSearch {
  def searchService
  static mapping = { version false }

//////////////loanhist////////////////////
  Integer id
  Integer loan_id
  Integer loanclass
  Long summa
  Double rate
  Date enddate
  String comment
  Date inputdate
  Long admin_id
//////////////Admin///////////////////////
  String admin_name

  def csiFindLoanHistory(iLoanId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='loanhist left join user u1 on (u1.id=loanhist.admin_id)'
    hsSql.where="1=1"+
                ((iLoanId>0)?' and loanhist.loan_id=:loan_id':'')
    hsSql.order="loanhist.inputdate desc"

    if(iLoanId>0)
      hsLong['loan_id'] = iLoanId

    searchService.fetchData(hsSql,hsLong,null,null,null,LoanhistSearch.class)
  }

}