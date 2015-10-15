class SalaryreportSearch {
  def searchService
  static mapping = {
    version false
  }

  Integer id
  Integer month
  Integer year
  Date repdate
  BigDecimal summa
  Integer modstatus
  Integer salarytype_id
  Integer department_id
  Date inputdate
  Long file
  Integer is_confirm
  Long admin_id

  String admin_name

  def csiSelectAvanses(iStatus,iDepartmentId,lsDepIds,dRepdate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsList=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='salaryreport, user u1'
    hsSql.where="salaryreport.admin_id=u1.id and salaryreport.salarytype_id=1"+
                (dRepdate?' AND salaryreport.year =:year AND salaryreport.month =:month':'')+
                ((iStatus>-100)?' and salaryreport.modstatus=:modstatus':'')+
                ((iDepartmentId>-100)?' and salaryreport.department_id=:department_id':'')+
                (lsDepIds?' and salaryreport.department_id in (:department_ids)':'')
    hsSql.order="salaryreport.year desc, salaryreport.month desc"

    if(dRepdate){
      hsLong['month'] = dRepdate.getMonth()+1
      hsLong['year'] = dRepdate.getYear()+1900
    }
    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(iDepartmentId>-100)
      hsLong['department_id']=iDepartmentId
    if(lsDepIds)
      hsList['department_ids']=lsDepIds

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      hsList,null,iMax,iOffset,'salaryreport.id',true,SalaryreportSearch.class)
  }

  def csiSelectCashreports(iStatus,iDepartmentId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='salaryreport, user u1'
    hsSql.where="salaryreport.admin_id=u1.id and salaryreport.salarytype_id=5"+
                ((iStatus>-100)?' and salaryreport.modstatus=:modstatus':'')+
                ((iDepartmentId>-100)?' and salaryreport.department_id=:department_id':'')
    hsSql.order="salaryreport.year desc, salaryreport.month desc"

    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(iDepartmentId>-100)
      hsLong['department_id']=iDepartmentId

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'salaryreport.id',true,SalaryreportSearch.class)
  }

}