class PayrequestTaskSearch {
  def searchService
  static mapping = { version false }

//////////Payrequest///////
  Integer id
  Date paydate
  Date inputdate
  Date execdate
  Date indate
  BigDecimal summa
  BigDecimal summands
  Integer is_nds
  Integer modstatus
  Integer instatus
  String fromcompany
  String frominn
  Integer fromcompany_id
  Integer bankaccount_id
  String tocompany
  Integer tocompany_id
  Integer tobankaccount_id
  String toinn
  String tokpp
  String tobank
  String tobankbik
  String toaccount
  String tocorraccount
  Integer tax_id
  Integer kbkrazdel_id
  String platperiod
  Integer paytype
  Integer paycat
  Integer payway
  Integer paygroup
  Integer agreementtype_id
  Integer agreement_id
  Integer is_dop
  Integer is_fine
  Long pers_id
  Integer taskpay_id
  Integer payment_id
  String agreementnumber
  String destination
  String oktmo
  String comment
  Long initiator
  Long clientadmin
  Long tagadmin
  Integer expensetype_id
  Integer project_id
  Integer client_id
  Integer subclient_id
  Integer percenttype
  Double compercent
  Double subcompercent
  Double midpercent
  Double supcompercent
  BigDecimal comission
  BigDecimal subcomission
  BigDecimal midcomission
  BigDecimal supcomission
  BigDecimal clientcommission
  BigDecimal agentcommission
  BigDecimal clientdelta
  Integer confirmstatus
  Integer is_clientcommission
  Integer is_midcommission
  String sfactura
  Date sfacturadate
  Integer deal_id
  Integer agentagr_id
  Integer agent_id
  String tagcomment
  Integer is_bankmoney
  Double payoffperc
  BigDecimal payoffsumma
  BigDecimal depbody
  BigDecimal depprc
  Integer cashrequest_id
  Integer is_generate
  Long file_id
  Integer car_id
  Integer agrpayment_id
  Integer is_third
  Integer related_id
//////////Task/////////////
  Integer is_accept
  Date acceptdate
  Integer is_urgent
//////////Bankaccount//////
  Integer actsaldo
  Date actsaldodate
//////////General//////////
  String bank_name

  def csiSelectTaskpay(hsParams,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong = [:]
    def hsString = [:]

    hsSql.select="*, bank.name as bank_name"
    hsSql.from="payrequest left join taskpay on(payrequest.taskpay_id=taskpay.id) left join bankaccount on(payrequest.bankaccount_id=bankaccount.id) left join bank on(bankaccount.bank_id=bank.id)"
    hsSql.where="payrequest.paytype in (1,3) and payrequest.modstatus<2"+
      (hsParams?.bank_id?' AND bankaccount.bank_id=:bank_id':'')+
      (hsParams?.company_id>0?" AND payrequest.fromcompany_id=:company_id":'')+
      (hsParams?.reportstart?" AND payrequest.paydate>=:date_start":'')+
      (hsParams?.reportend?" AND payrequest.paydate<=:date_end":'')
    hsSql.order="payrequest.paydate desc, payrequest.id desc"

    if(hsParams?.reportstart)
      hsString['date_start'] = String.format('%tF',hsParams.reportstart)
    if(hsParams?.reportend)
      hsString['date_end'] = String.format('%tF',hsParams.reportend)
    if(hsParams?.company_id>0)
      hsLong['company_id'] = hsParams.company_id
    if(hsParams?.bank_id)
      hsString['bank_id'] = hsParams.bank_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'payrequest.id',true,PayrequestTaskSearch.class)
  }

  def csiSelectHandTaskpay(hsParams,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong = [:]
    def hsString = [:]

    hsSql.select="*, bank.name as bank_name"
    hsSql.from="payrequest join taskpay on(payrequest.taskpay_id=taskpay.id) left join bankaccount on(payrequest.bankaccount_id=bankaccount.id) left join bank on(bankaccount.bank_id=bank.id)"
    hsSql.where="payrequest.paytype in (1,3) and payrequest.payway=1 and taskpay.is_accept=1"+
      (hsParams?.bank_id?' AND bankaccount.bank_id=:bank_id':'')+
      (hsParams?.company_id>0?" AND payrequest.fromcompany_id=:company_id":'')+
      (!hsParams?.modstatus?" AND payrequest.modstatus<2":hsParams?.modstatus==1?' AND payrequest.modstatus>=2':'')+
      (hsParams?.reportstart?" AND payrequest.paydate>=:date_start":'')+
      (hsParams?.reportend?" AND payrequest.paydate<=:date_end":'')+
      (hsParams?.execdatestart?" AND payrequest.execdate>=:execdate_start":'')+
      (hsParams?.execdateend?" AND payrequest.execdate<=:execdate_end":'')
    hsSql.order="payrequest.paydate desc, payrequest.id desc"

    if(hsParams?.reportstart)
      hsString['date_start'] = String.format('%tF',hsParams.reportstart)
    if(hsParams?.reportend)
      hsString['date_end'] = String.format('%tF',hsParams.reportend)
    if(hsParams?.execdatestart)
      hsString['execdate_start'] = String.format('%tF',hsParams.execdatestart)
    if(hsParams?.execdateend)
      hsString['execdate_end'] = String.format('%tF',hsParams.execdateend)
    if(hsParams?.company_id>0)
      hsLong['company_id'] = hsParams.company_id
    if(hsParams?.bank_id)
      hsString['bank_id'] = hsParams.bank_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'payrequest.id',true,PayrequestTaskSearch.class)
  }

  BigDecimal computeCurSaldo(){
    return (actsaldo?:0) - (Taskpay.findAllByModdateGreaterThanEqualsAndBankaccount_idAndTaskpaystatusInList(actsaldodate?:new Date()-365,bankaccount_id?:-1,[2,4]).sum{it.summa}?:0) + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThan(actsaldodate?:new Date()-365,bankaccount_id?:-1,2,2).sum{it.summa}?:0) + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(actsaldodate?:new Date()-365,bankaccount_id?:-1,3,2,4,1).sum{it.summa}?:0) - (Payrequest.findAllByPaydateGreaterThanEqualsAndBankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(actsaldodate?:new Date()-365,bankaccount_id?:-1,3,2,4,1).sum{it.summa}?:0) - (Taskpay.findAllByBankaccount_idAndTaskpaystatusInListAndIs_accept(bankaccount_id?:-1,[0,1,3,5],1).sum{it.summa}?:0)
  }
}