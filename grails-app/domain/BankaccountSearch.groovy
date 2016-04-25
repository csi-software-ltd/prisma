class BankaccountSearch {
  def searchService
  static mapping = {
    version false
  }

/////Account/////////
  Integer id
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
  Integer is_duplicate
  Integer is_smsinfo
  Integer typeaccount_id
  Integer is_nosms
  String smstel
  String dopoffice
  BigDecimal saldo
  BigDecimal actsaldo
  Date saldodate
  Date actsaldodate
  Date actmoddate
  BigDecimal banksaldo
  Date banksaldodate
/////Bank////////////
  String bankname
  String shortname
  String prevnameinfo
  Integer is_license
  Date stopdate
/////Company/////////
  String cname
  String inn
  String gd

/////////////////////////////////////////////////////////////////////////////////////////////////////
  String toString(){
    "$schet в $bankname"
  }

  def csiFindAccounts(iCompanyId,iStatus=-100,iTypeId=-100,hsParams=null){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, bank.name as bankname, '' as cname, '' as inn, '' as gd"
    hsSql.from='bankaccount,bank'
    hsSql.where="bank.id=bankaccount.bank_id"+
                ((iCompanyId>0)?' and bankaccount.company_id=:company_id':'')+
                ((iStatus>-100)?' and bankaccount.modstatus=:modstatus':'')+
                ((iTypeId>-100)?' and bankaccount.typeaccount_id=:type_id':'')+
                (hsParams?.is_request?' and bank.is_request=1':'')+
                (hsParams?.is_anketa?' and bank.is_anketa=1':'')
    hsSql.order="bankaccount.modstatus desc, bank.name asc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId
    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(iTypeId>-100)
      hsLong['type_id']=iTypeId

    searchService.fetchData(hsSql,hsLong,null,null,null,BankaccountSearch.class)
  }

  def csiFindAccountsByCompany(iCompanyId,iStatus){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, bank.name as bankname, '' as cname, '' as inn, '' as gd"
    hsSql.from='bankaccount,bank'
    hsSql.where="bank.id=bankaccount.bank_id"+
                ((iCompanyId>0)?' and bankaccount.company_id=:company_id':'')+
                (iStatus==0?' and bankaccount.modstatus=0':iStatus==1?' and bankaccount.modstatus=1 and bankaccount.typeaccount_id<6':iStatus==2?' and bankaccount.modstatus=1 and bankaccount.typeaccount_id>=6':'')
    hsSql.order="bankaccount.modstatus desc, bank.name asc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,BankaccountSearch.class)
  }

  def csiFindBankaccount(sBankname,iCompanyId,iValutaId,iTypeId,iIbankstatus,iSort,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]

    hsSql.select="*, bank.name as bankname, bankaccount.id as id, company.name as cname"
    hsSql.from='bankaccount,bank,company'
    hsSql.where="bank.id=bankaccount.bank_id and bankaccount.modstatus=1 and company.id=bankaccount.company_id and company.is_holding=1 and bank.is_license=1 and bankaccount.typeaccount_id<6"+
                ((iCompanyId>0)?' and bankaccount.company_id=:company_id':'')+
                ((iValutaId>0)?' and bankaccount.valuta_id=:valuta_id':'')+
                ((iTypeId>0)?' and bankaccount.typeaccount_id=:typeaccount_id':'')+
                (iIbankstatus>-100?' and bankaccount.ibankstatus=:ibankstatus':iIbankstatus==-100?' and bankaccount.ibankstatus in (-1,1)':'')+
                ((sBankname)?' and bank.name like concat("%",:bname,"%")':'')
    hsSql.order=(iSort==1)?"bankaccount.actsaldodate desc":(iSort==2)?'bankaccount.banksaldodate desc':(iSort==3)?"company.name asc, bank.name asc":"bank.name asc, company.name asc"

    if(iCompanyId>0)
      hsInt['company_id']=iCompanyId
    if(iValutaId>0)
      hsInt['valuta_id']=iValutaId
    if(iTypeId>0)
      hsInt['typeaccount_id']=iTypeId
    if(iIbankstatus>-100)
      hsInt['ibankstatus']=iIbankstatus
    if(sBankname)
      hsString['bname']=sBankname
      
    searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,null,null,iMax,iOffset,'bankaccount.id',true, BankaccountSearch.class)
  }

  def csiFindAccountForBanksaldo(sBankname,sCompanyName,iValutaId,iSort,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, bank.shortname as bankname, bankaccount.id as id, company.name as cname"
    hsSql.from='bankaccount,bank,company'
    hsSql.where="bank.id=bankaccount.bank_id and bankaccount.modstatus=1 and company.id=bankaccount.company_id and company.is_holding=1 and bank.is_license=1 and bankaccount.typeaccount_id<6"+
                ((iValutaId>0)?' and bankaccount.valuta_id=:valuta_id':'')+
                ((sBankname!='')?' and bank.name like concat("%",:bname,"%")':'')+
                ((sCompanyName!='')?' and company.name like concat("%",:cname,"%")':'')
    hsSql.order=(iSort==1)?"bank.name asc, company.name asc":"company.name asc, bank.name asc"

    if(sBankname)
      hsString['bname']=sBankname
    if(sCompanyName)
      hsString['cname']=sCompanyName
    if(iValutaId>0)
      hsLong['valuta_id']=iValutaId

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'bankaccount.id',true, BankaccountSearch.class)
  }

  def csiFindBanknames(sBankname){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsString=[:]

    hsSql.select="*, bank.name as bankname, '' as cname, '' as inn, '' as gd"
    hsSql.from='bankaccount,bank,company'
    hsSql.where="bank.id=bankaccount.bank_id and bankaccount.company_id=company.id and company.is_holding=1"+
                ((sBankname!='')?' and bank.name like concat("%",:bname,"%")':'')
    hsSql.group="bank.id"
    hsSql.order="bank.name asc"

    if(sBankname!='')
      hsString['bname']=sBankname

    searchService.fetchData(hsSql,null,null,hsString,null,BankaccountSearch.class,10)
  }

  def csiFindBanknames(){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']

    hsSql.select="*, bank.name as bankname, company.name as cname"
    hsSql.from='bankaccount,bank,company'
    hsSql.where="bank.id=bankaccount.bank_id and bankaccount.company_id=company.id and bankaccount.typeaccount_id=1 and company.modstatus=1 and bankaccount.modstatus=1 and company.is_holding=1"
    hsSql.group="bank.id"
    hsSql.order="bank.name asc"

    searchService.fetchData(hsSql,null,null,null,null,BankaccountSearch.class)
  }

  def csiFindAccountsForBankrequest(){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']

    hsSql.select="*, bank.name as bankname, company.name as cname"
    hsSql.from='bankaccount,bank,company'
    hsSql.where="bank.id=bankaccount.bank_id and bankaccount.company_id=company.id and bankaccount.typeaccount_id=1 and bank.is_request=1 and company.modstatus=1 and bankaccount.modstatus=1"
    hsSql.group="bankaccount.company_id"
    hsSql.order="company.name asc"

    searchService.fetchData(hsSql,null,null,null,null,BankaccountSearch.class)
  }

  def csiFindAccountsForBankanketa(){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']

    hsSql.select="*, bank.name as bankname, company.name as cname"
    hsSql.from='bankaccount,bank,company'
    hsSql.where="bank.id=bankaccount.bank_id and bankaccount.company_id=company.id and bankaccount.typeaccount_id=1 and bank.is_anketa=1 and company.modstatus=1 and bankaccount.modstatus=1"
    hsSql.group="bankaccount.company_id"
    hsSql.order="company.name asc"

    searchService.fetchData(hsSql,null,null,null,null,BankaccountSearch.class)
  }

  def csiFindExpiresAccounts(sBankname){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, bank.name as bankname, company.name as cname"
    hsSql.from='bankaccount,bank,company'
    hsSql.where="bank.id=bankaccount.bank_id and bankaccount.company_id=company.id and company.is_holding=1 and bankaccount.modstatus=1 and bankaccount.ibank_open is not null and bankaccount.ibank_close < curdate() + INTERVAL 31 DAY"+
                ((sBankname!='')?' and bank.name like concat("%",:bname,"%")':'')
    hsSql.order="bankaccount.ibank_close asc"

    if(sBankname!='')
      hsString['bname'] = sBankname

    searchService.fetchData(hsSql,null,null,hsString,null,BankaccountSearch.class)
  }
}