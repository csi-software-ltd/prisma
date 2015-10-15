class SalarycompSearch {
  def searchService
  static mapping = {
    version false
  }

//////////salarycomp/////////
  Long id
  Integer is_pers
  Integer company_id
  String companyname
  String companyinn
  String fio
  Long pers_id
  Integer perstype
  String snils
  String position
  String region
  Double overpayment
  Integer is_noaccount
  Integer month
  Integer year
  Date inputdate
  BigDecimal fullsalary
  BigDecimal netsalary
  BigDecimal debtsalary
  BigDecimal ndfl
  BigDecimal debtndfl
  BigDecimal fss_tempinvalid
  BigDecimal debtfss_tempinvalid
  BigDecimal fss_accident
  BigDecimal debtfss_accident
  BigDecimal ffoms
  BigDecimal debtffoms
  BigDecimal pf
  BigDecimal debtpf
  BigDecimal cardmain
  BigDecimal cardadd
  Integer cashsalary
  Integer compstatus
  Integer perstatus
  Integer paidmainstatus
  Integer paidaddstatus
  Date paydate
//////////pers//////////////
  Long actsalary
  Integer is_haveibank
  Integer is_havemaincard
  Integer is_haveaddcard
  String bankname
  String nomer
  String paccount
  String pin

  def csiSelectOffreports(dDate,iType,sCompanyname,sPersname,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',group:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, '' as bankname, '' as nomer, '' as paccount, '' as pin, IF(sum(IF((persaccount.is_main=0 and persaccount.modstatus=1),1,0)),1,0) as is_haveaddcard, IF(sum(IF(persaccount.is_main=1 and persaccount.modstatus=1,1,0)),1,0) as is_havemaincard,IF((select count(*) from bankaccount where bankaccount.company_id=salarycomp.company_id and modstatus=1 and ibankstatus=1 and typeaccount_id=1)>0,1,0) as is_haveibank"
    hsSql.from='salarycomp left join persaccount on(salarycomp.pers_id=persaccount.pers_id), pers'
    hsSql.where="salarycomp.pers_id=pers.id and salarycomp.is_pers=1"+
                (dDate?' AND salarycomp.year =:year AND salarycomp.month =:month':'')+
                ((iType>-100)?' AND salarycomp.perstype =:perstype':'')+
                ((sCompanyname!='')?' AND salarycomp.companyname like concat("%",:cname,"%")':'')+
                ((sPersname!='')?' AND salarycomp.fio like concat("%",:pname,"%")':'')
    hsSql.group="salarycomp.id"
    hsSql.order="salarycomp.fio asc, salarycomp.companyname asc"

    if(dDate){
      hsLong['month'] = dDate.getMonth()+1
      hsLong['year'] = dDate.getYear()+1900
    }
    if(iType>-100)
      hsLong['perstype'] = iType
    if(sCompanyname!='')
      hsString['cname'] = sCompanyname
    if(sPersname!='')
      hsString['pname'] = sPersname

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'*',true,SalarycompSearch.class)
  }

  def csiSelectDCPayments(dDate,sBankname,sCompanyname,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, bank.name as bankname, 0 as actsalary, 0 as is_haveibank, 0 as is_havemaincard, 0 as is_haveaddcard"
    hsSql.from='salarycomp join persaccount on(salarycomp.pers_id=persaccount.pers_id) join bank on (persaccount.bank_id=bank.id)'
    hsSql.where="salarycomp.is_pers=1 and salarycomp.cardadd>0 and persaccount.is_main=0 and salarycomp.paidaddstatus>=0"+
                (dDate?' AND salarycomp.year =:year AND salarycomp.month =:month':'')+
                ((sCompanyname!='')?' AND salarycomp.companyname like concat("%",:cname,"%")':'')+
                ((sBankname!='')?' AND bank.name like concat("%",:bname,"%")':'')
    hsSql.order="bank.name asc, salarycomp.companyname asc, salarycomp.fio asc"

    if(dDate){
      hsLong['month'] = dDate.getMonth()+1
      hsLong['year'] = dDate.getYear()+1900
    }
    if(sCompanyname!='')
      hsString['cname'] = sCompanyname
    if(sBankname!='')
      hsString['bname'] = sBankname

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'salarycomp.id',true,SalarycompSearch.class)
  }

}