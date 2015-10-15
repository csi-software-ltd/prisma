class BankdirSearch {
  def searchService
  static mapping = { version false }
/////Account/////////
  String id
  String bank_id
  Integer company_id
  Long pers_id
  Date directordate
  String schet
  String anomer
  Date adate
  Integer valuta_id
  Integer modstatus
  Date opendate
  Date closedate
  Date ibank_open
  Date ibank_close
  Integer ibankstatus
  Integer ibankblock
  String ibank_comment
  Integer is_bkactproc
  Integer is_duplicate
  Integer is_smsinfo
  Integer typeaccount_id
  String smstel
  String dopoffice
  Integer saldo
  Integer actsaldo
  Date saldodate
  Date actsaldodate
  Date actmoddate
  Long banksaldo
  Date banksaldodate
/////Bank////////////
  String bankname
  Integer ibankterm
  Integer is_license
/////Company/////////
  String cname
  String inn
  Integer cgroup_id
  String gd
  Integer outsource_id
  Integer activitystatus_id
  String okvedmain
/////Pers////////////
  String p_shortname

/////////////////////////////////////////////////////////////////////////////////////////////////////
  def csiFindBankdirs(iCgroupId,sBankName,sPersName,iTypeaccountId,iActStatus,iNoClosed,iActive){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="concat(company.id,'-',ifnull(bankaccount.id,'')) as id, bank.shortname as bankname, company.name as cname, pers.fullname as p_shortname, bankaccount.*, bank.*, company.*"
    hsSql.from='company left join bankaccount on (company.id=bankaccount.company_id) left join pers on (bankaccount.pers_id=pers.id) left join bank on (bank.id=bankaccount.bank_id)'
    hsSql.where="company.is_holding=1 and company.inn!='000000000000'"+
                ((iCgroupId>0)?' and company.cgroup_id=:cgroup_id':'')+
                ((sBankName!='')?' and bank.name like concat("%",:bname,"%")':'')+
                ((sPersName!='')?' and (pers.fullname like concat("%",:pname,"%") or company.gd like concat("%",:pname,"%"))':'')+
                ((iTypeaccountId>0)?' and bankaccount.typeaccount_id=:type_id':'')+
                ((iActStatus>0)?' and company.activitystatus_id=:actstatus_id':'')+
                ((iNoClosed>0)?' and bankaccount.modstatus=1':'')+
                ((iActive>0)?' and bank.is_license=1':'')
    hsSql.order="company.cgroup_id asc, company.name asc, bank.name asc"

    if(iCgroupId>0)
      hsLong['cgroup_id']=iCgroupId
    if(sBankName)
      hsString['bname']=sBankName
    if(sPersName)
      hsString['pname']=sPersName
    if(iTypeaccountId>0)
      hsLong['type_id']=iTypeaccountId
    if(iActStatus>0)
      hsLong['actstatus_id']=iActStatus

    searchService.fetchData(hsSql,hsLong,null,hsString,null,BankdirSearch.class)
  }
}