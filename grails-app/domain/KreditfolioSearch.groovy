class KreditfolioSearch {
  def searchService
  def agentKreditService
  static mapping = { version false }

//////////////kredit//////////////////////
  Integer id
  Integer kredtype
  Integer is_real
  Integer is_tech
  Integer is_realtech
  Integer client
  String bank_id
  Integer creditor
  String cbank_id
  Integer reasonagroffice
  Integer reasonagrwh
  Integer reasonagrbank
  Integer reasonagrspace
  String anumber
  Date adate
  Integer summa
  BigDecimal startsumma
  Date startsaldodate
  Double rate
  Integer valuta_id
  BigDecimal debt
  Date startdate
  Date enddate
  Date stopdate
  Integer kreditterm
  Integer payterm
  Integer paytermcondition
  Integer repaymenttype_id
  Integer monthnumber
  Integer modstatus
  Integer kredittransh
  Date inputdate
  Integer is_agr
  Integer is_cbcalc
  Integer client_id
  Integer zalogstatus
  Integer cessionstatus
  String comment
  Long responsible
//////////////Dopagr/////////////////////
  String nomer
  Date dsdate
  Date ds_startdate
  Date ds_enddate
  Integer ds_summa
  BigDecimal ds_rate
  String ds_comment
//////////////Others/////////////////////
  String company_name
  Integer activitystatus_id
  String bank_name
  Integer is_license

  def csiSelectKreditfolio(dKreditfolioDate,dDateStart,dDateEnd,sBankId,iCompanyId,lResponsible,iAgr,iZalogId,iNoLicense,iDebt,iActive,iActStatus){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, company.name as company_name, bank.name as bank_name, kreditdopagr.startdate as ds_startdate, kreditdopagr.enddate as ds_enddate, kreditdopagr.summa as ds_summa, kreditdopagr.rate as ds_rate, kreditdopagr.comment as ds_comment"
    hsSql.from='kredit join bank on (kredit.bank_id=bank.id) join company on (if(kredit.creditor>0,kredit.creditor,kredit.client)=company.id) left join kreditdopagr on(kreditdopagr.kredit_id=kredit.id and kreditdopagr.startdate<=:maindate and kreditdopagr.enddate>=:maindate and kreditdopagr.id=(select max(kreditdopagr.id) from kreditdopagr where kreditdopagr.kredit_id=kredit.id and kreditdopagr.startdate<=:maindate and kreditdopagr.enddate>=:maindate))'
    hsSql.where="kredit.modstatus>=0"+
                ((sBankId!='')?' and kredit.bank_id=:bankId':'')+
                ((iCompanyId>0)?' and company.id=:cId':'')+
                ((lResponsible>0)?' and kredit.responsible=:responsible':'')+
                ((iAgr>0)?' and kredit.is_agr=1':'')+
                ((iNoLicense>0)?' and bank.is_license=0':'')+
                ((iDebt>0)?' and kredit.debt>0':'')+
                ((iActive>0)?' and kredit.modstatus=1':'')+
                (iActStatus in [1,2,5,8]?' and company.activitystatus_id=:actstatus':iActStatus==3?' and company.activitystatus_id in (3,4)':iActStatus==6?' and company.activitystatus_id in (6,7)':iActStatus==9?' and company.activitystatus_id in (9,10)':'')+
                ((iZalogId>-100)?' and kredit.zalogstatus=2':'')+
                ((iZalogId>0)?' and (select count(*) from kreditzalog where kreditzalog.kredit_id=kredit.id and kreditzalog.zalogtype_id=:zalogtype) > 0':'')+
                (dKreditfolioDate?' and ifnull(kreditdopagr.startdate,kredit.startdate)<=:maindate and ifnull(kreditdopagr.enddate,kredit.enddate)>=:maindate':'')+
                (dDateStart?' and ifnull(kreditdopagr.enddate,kredit.enddate)>=:startdate':'')+
                (dDateEnd?' and ifnull(kreditdopagr.enddate,kredit.enddate)<=:enddate':'')
    hsSql.order="company.name asc"
    hsSql.group="kredit.id"

    if(sBankId!='')
      hsString['bankId'] = sBankId
    if(iCompanyId>0)
      hsLong['cId'] = iCompanyId
    if(iZalogId>0)
      hsLong['zalogtype'] = iZalogId
    if(lResponsible>0)
      hsLong['responsible'] = lResponsible
    if(iActStatus in [1,2,5,8])
      hsLong['actstatus'] = iActStatus

    hsString['maindate'] = String.format('%tF',dKreditfolioDate)
    if(dDateStart)
      hsString['startdate'] = String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['enddate'] = String.format('%tF',dDateEnd)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,-1,0,'*',true,KreditfolioSearch.class)
  }

  def csiSelectKreditCompany(iCompanyId,dKreditDate,dStopdate){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, company.name as company_name, bank.name as bank_name, kreditdopagr.startdate as ds_startdate, kreditdopagr.enddate as ds_enddate, kreditdopagr.summa as ds_summa, kreditdopagr.rate as ds_rate, kreditdopagr.comment as ds_comment"
    hsSql.from='kredit join bank on (kredit.bank_id=bank.id) join company on (if(kredit.creditor>0,kredit.creditor,kredit.client)=company.id) left join kreditdopagr on(kreditdopagr.kredit_id=kredit.id and kreditdopagr.startdate<=:maindate and kreditdopagr.enddate>=:maindate and kreditdopagr.id=(select max(kreditdopagr.id) from kreditdopagr where kreditdopagr.kredit_id=kredit.id and kreditdopagr.startdate<=:maindate and kreditdopagr.enddate>=:maindate))'
    hsSql.where="kredit.modstatus>=0"+
                ((iCompanyId>0)?' and company.id=:cId':'')+
                (dKreditDate?' and ifnull(kreditdopagr.startdate,kredit.startdate)<=:maindate and ifnull(kredit.stopdate,:maindate)>=:maindate':'')+
                (dStopdate?' and ifnull(kredit.stopdate,:maindate)>=:stopdate':'')
    hsSql.order="kredit.enddate asc"
    hsSql.group="kredit.id"

    hsString['maindate'] = String.format('%tF',dKreditDate)
    if(iCompanyId>0)
      hsLong['cId'] = iCompanyId
    if(dStopdate)
      hsString['stopdate'] = String.format('%tF',dStopdate)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,-1,0,'*',true,KreditfolioSearch.class)
  }

  BigDecimal computesaldo(Date _date){
    BigDecimal kredsumma = 0.0g
    BigDecimal paid = 0.0g
    if (kredtype==1) kredsumma = (ds_summa?:startsaldodate?startsumma:summa).toBigDecimal()
    else if (kredtype==3) kredsumma = Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeAndPaydateLessThanEqualsAndModstatusGreaterThanAndPaydateGreaterThanEquals(3,id,0,1,_date,-1,startsaldodate?:new Date(1,0,1)).sum{it.summa}?:0
    else kredsumma = (startsaldodate?startsumma:0)+(Kreditline.findAllByKredit_idAndModstatusGreaterThanEqualsAndPaydateLessThanEqualsAndPaydateGreaterThanEquals(id,0,_date,startsaldodate?:new Date(1,0,1)).sum{it.summa}?:0)

    if (kredtype==3) paid = Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeAndPaydateLessThanEqualsAndModstatusGreaterThanAndPaydateGreaterThanEquals(3,id,0,2,_date,-1,startsaldodate?:new Date(1,0,1)).sum{it.summa}?:0
    else paid = Kreditpayment.findAllByKredit_idAndModstatusGreaterThanEqualsAndPaydateLessThanEqualsAndPaydateGreaterThanEquals(id,0,_date,startsaldodate?:new Date(1,0,1)).sum{it.summa}?:0
    kredsumma - paid
  }
}