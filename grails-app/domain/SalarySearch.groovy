class SalarySearch {
  def searchService
  static mapping = {
    version false
  }
  static constraints = {
  }

////////Salary//////
  Long id
  Integer month
  Integer year
  Long pers_id
  Integer department_id
  Date inputdate
  Date moddate
  BigDecimal actsalary
  BigDecimal offsalary
  Integer prepayment
  BigDecimal prevfix
  BigDecimal cardmain
  BigDecimal cardadd
  Integer cash
  Integer bonus
  Integer shtraf
  Float overloadhour
  Integer overloadsumma
  Integer holiday
  Integer reholiday
  Integer precashpayment
  Integer modstatus
  Integer prepaystatus
  Date prepaydate
  Integer offstatus
  Date offdate
  Integer cashstatus
  Date cashdate
/////////Pers//////////
  String shortname
  Integer is_haveagr

  def csiFindPrepayments(iMonth,iYear,iDepartmentId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, pers.shortname as shortname"
    hsSql.from='salary, pers'
    hsSql.where="pers.id=salary.pers_id"+
                ((iMonth>0)?' and salary.month=:month':'')+
                ((iYear>0)?' and salary.year=:year':'')+
                ((iDepartmentId>-100)?' and salary.department_id=:department_id':'')
    hsSql.order="pers.shortname asc"

    if(iMonth>0)
      hsLong['month']=iMonth
    if(iYear>0)
      hsLong['year']=iYear
    if(iDepartmentId>-100)
      hsLong['department_id']=iDepartmentId

    searchService.fetchData(hsSql,hsLong,null,null,null,SalarySearch.class)
  }

  def csiFindSalaries(dDate,lPersId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, pers.shortname as shortname"
    hsSql.from='salary, pers'
    hsSql.where="pers.id=salary.pers_id"+
                (dDate?' AND salary.year =:year AND salary.month =:month':'')+
                ((lPersId>0)?' and salary.pers_id=:pers_id':'')
    hsSql.order="pers.shortname asc"

    if(dDate){
      hsLong['month'] = dDate.getMonth()+1
      hsLong['year'] = dDate.getYear()+1900
    }
    if(lPersId>0)
      hsLong['pers_id']=lPersId

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'salary.id',true,SalarySearch.class)
  }
}