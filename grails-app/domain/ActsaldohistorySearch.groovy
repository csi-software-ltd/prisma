class ActsaldohistorySearch {
  def searchService
  static mapping = { version false }

/////Saldohistory1/////////
  Integer id
  Integer bankaccount_id
  Date inputdate
  Integer actsaldo
  Date actsaldodate
  BigDecimal saldo
  Date saldodate
  Long banksaldo
  Date banksaldodate
/////Saldohistory2/////////
  BigDecimal s2_saldo
  Date s2_saldodate
/////General///////////////
  String bankname
  String company_name
  Integer typeaccount_id
  Integer valuta_id
  String schet

/////////////////////////////////////////////////////////////////////////////////////////////////////
  def csiGetSaldoreport(dStartdate,dEnddate,sCompanyName,sBankname,iValutaId,iSort,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsString=[:]
    def hsLong=[:]

    hsSql.select="*, ash2.saldo as s2_saldo, ash2.saldodate as s2_saldodate, company.name as company_name, bank.shortname as bankname"
    hsSql.from='actsaldohistory ash1 left join actsaldohistory ash2 on (ash2.saldodate<=ash1.actsaldodate and ash1.bankaccount_id=ash2.bankaccount_id), bankaccount, company, bank'
    hsSql.where="ash1.bankaccount_id=bankaccount.id and bankaccount.company_id=company.id and bankaccount.bank_id=bank.id and ash1.actsaldodate is not null and ash1.inputdate=(select max(inputdate) from actsaldohistory where actsaldohistory.actsaldodate=ash1.actsaldodate and actsaldohistory.bankaccount_id=ash1.bankaccount_id) and (ash2.saldodate is NULL OR ash2.saldodate=(select max(saldodate) from actsaldohistory where actsaldohistory.saldodate<=ash1.actsaldodate and actsaldohistory.bankaccount_id=ash2.bankaccount_id))"+
                (dStartdate?' and ash1.actsaldodate>=:startdate':'')+
                (dEnddate?' and ash1.actsaldodate<=:enddate':'')+
                (iValutaId>0?' and bankaccount.valuta_id=:valuta_id':'')+
                ((sCompanyName!='')?' and company.name like concat("%",:cname,"%")':'')+
                ((sBankname!='')?' and bank.name like concat("%",:bname,"%")':'')
    hsSql.group="ash1.bankaccount_id, ash1.actsaldodate"
    hsSql.order=(iSort==1)?"bank.name asc, company.name asc, ash1.actsaldodate desc":"company.name asc, bank.name asc, ash1.actsaldodate desc"

    if(dStartdate)
      hsString['startdate'] = String.format('%tF',dStartdate)
    if(dEnddate)
      hsString['enddate'] = String.format('%tF',dEnddate)
    if(iValutaId>0)
      hsLong['valuta_id'] = iValutaId
    if(sCompanyName!='')
      hsString['cname'] = sCompanyName
    if(sBankname!='')
      hsString['bname'] = sBankname

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'*',true,ActsaldohistorySearch.class)
  }

}