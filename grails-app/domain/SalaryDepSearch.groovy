class SalaryDepSearch {
  def searchService

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
  String p_shortname
/////////Department////
  String d_name
  Integer parent
/////////Salarycomp////
  BigDecimal sc_netsalary

  def csiFindMonthSalary(dDate,iDepartmentId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, pers.shortname as p_shortname, department.name as d_name, (select sum(netsalary) from salarycomp where salarycomp.is_pers=1 and salarycomp.pers_id=pers.id and salarycomp.month=salary.month and salarycomp.year=salary.year) as sc_netsalary"
    hsSql.from='salary, pers, department'
    hsSql.where="pers.id=salary.pers_id and salary.department_id=department.id"+
                (dDate?' AND salary.year =:year AND salary.month =:month':'')+
                (iDepartmentId>-100?' and salary.department_id=:department_id':'')
    hsSql.order="department.name asc, pers.shortname asc"

    if(dDate){
      hsLong['month'] = dDate.getMonth()+1
      hsLong['year'] = dDate.getYear()+1900
    }
    if(iDepartmentId>-100)
      hsLong['department_id']=iDepartmentId

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'salary.id',true,SalaryDepSearch.class)
  }
}