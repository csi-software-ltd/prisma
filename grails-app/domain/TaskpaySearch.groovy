class TaskpaySearch {
  def searchService
  static mapping = { version false }

//////////taskpay//////////
  Integer id
  Date inputdate
  Integer taskpaystatus
  Integer paygroup
  Date term
  Date moddate
  Long initiator
  Long executor
  Integer bankaccount_id
  Integer company_id
  BigDecimal summa
  String description
  String plan
  String comment
  Integer is_accept
  Date acceptdate
  Long acceptoperator
  Integer is_client
  Integer is_internal
  Integer is_urgent
  Integer payway
  Integer is_manual
//////////general//////////
  String company_name
  String bank_name
  String executor_name

  def csiSelectTaskpay(lUserId,lsPaygroup,iId,sExecutor,iTaskpaystatus,sCompanyName,dTermdate,iIsAccept,iPaygroup,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong = [:]
    def hsString = [:]
    def hsList = [:]

    hsSql.select="taskpay.*, bank.name as bank_name, company.name as company_name, user.name as executor_name"
    hsSql.from="taskpay left join company on(taskpay.company_id=company.id) left join bankaccount on(taskpay.bankaccount_id=bankaccount.id) left join bank on(bankaccount.bank_id=bank.id) left join user on(taskpay.executor=user.id)"
    hsSql.where="(taskpay.paygroup in (:groups) OR taskpay.executor=:user_id)"+
      ((sExecutor!='')?' AND user.name LIKE concat("%",:executor,"%")':'')+
      ((iTaskpaystatus>-2)?" AND taskpay.taskpaystatus=:taskpaystatus":'')+
      ((iIsAccept>-100)?" AND taskpay.is_accept=:is_accept":'')+
      ((iId>0)?" AND taskpay.id=:tid":'')+
      ((iPaygroup>0)?" AND taskpay.paygroup=:paygroup":'')+
      ((sCompanyName)?" AND company.name LIKE concat('%',:company_name,'%')":"")+
      (dTermdate?" AND taskpay.term=:termdate":'')
    hsSql.order="term desc, company.name asc"

    hsLong['user_id'] = lUserId
    hsList['groups'] = lsPaygroup
    if(sExecutor!='')
      hsString['executor'] = sExecutor
    if(iTaskpaystatus>-2)
      hsLong['taskpaystatus'] = iTaskpaystatus
    if(iIsAccept>-100)
      hsLong['is_accept'] = iIsAccept
    if(iPaygroup>0)
      hsLong['paygroup'] = iPaygroup
    if(iId>0)
      hsLong['tid'] = iId
    if(sCompanyName)
      hsString['company_name'] = sCompanyName
    if(dTermdate)
      hsString['termdate'] = String.format('%tF',dTermdate)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,hsList,null,iMax,iOffset,'taskpay.id',true,TaskpaySearch.class)
  }  
}