class KreditSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////kredit///////////////////////
  Integer id
  Integer kredtype
  Integer is_real
  Integer is_tech
  Integer is_realtech
  Integer client
  String bank_id
  Integer creditor
  String anumber
  Date adate
  BigDecimal summa
  BigDecimal startsumma
  BigDecimal agentsum
  Date startsaldodate
  Double rate
  Integer valuta_id
  BigDecimal debt
  Date startdate
  Date enddate
  Integer kreditterm
  Integer modstatus
  Date inputdate
  Integer is_agr
  Integer is_cbcalc
  Integer zalogstatus
  Integer cessionstatus
  String comment
  Long responsible
  Integer is_check
//////////////Company/////////////////////
  String client_name
  String creditor_name
  String bank_name

  String toString(){
    "$client_name - $bank_name - $anumber от ${String.format('%td.%<tm.%<tY',adate)}"
  }

  def csiSelectKredits(iId,sInn,sCompanyName,sBankName,iValutaId,iReal,iTech,iRealtech,lResponsible,iCession,iZalog,iStatus,iNoCheck,bShowReal,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, company.name as client_name, bank.name as bank_name, c2.name as creditor_name"
    hsSql.from='kredit left join company c2 on (kredit.creditor=c2.id), company, bank'
    hsSql.where="kredit.client=company.id and kredit.bank_id=bank.id"+
                ((iId>0)?' and kredit.id=:kid':'')+
                ((sInn!='')?' and company.inn like concat("%",:inn,"%")':'')+
                ((sCompanyName!='')?' and company.name like concat("%",:company_name,"%")':'')+
                ((sBankName!='')?' and bank.name like concat("%",:bname,"%")':'')+
                ((iValutaId>0)?' and kredit.valuta_id=:valuta_id':'')+
                ((iReal>0)?' and kredit.is_real=1':'')+
                ((iTech>0)?' and kredit.is_tech=1':'')+
                ((iRealtech>0)?' and kredit.is_realtech=1':'')+
                ((lResponsible>0)?' and kredit.responsible=:responsible':'')+
                ((iCession>0)?' and kredit.cessionstatus>0':'')+
                ((iZalog>0)?' and kredit.zalogstatus=:zalogstatus':'')+
                ((iStatus>-100)?' and kredit.modstatus=:modstatus':'')+
                ((iNoCheck>0)?' and kredit.is_check=0':'')+
                (!bShowReal?' and kredit.is_tech=1':'')
    hsSql.order="kredit.adate desc"

    if(iId>0)
      hsLong['kid']=iId
    if(sInn!='')
      hsString['inn']=sInn
    if(sCompanyName!='')
      hsString['company_name']=sCompanyName
    if(sBankName!='')
      hsString['bname']=sBankName
    if(iValutaId>0)
      hsLong['valuta_id']=iValutaId
    if(iZalog>0)
      hsLong['zalogstatus']=iZalog
    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(lResponsible>0)
      hsLong['responsible']=lResponsible

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'kredit.id',true,KreditSearch.class)
  }

  def csiFindCompanyKredits(iCompanyId,iStatus=1,bShowReal=true){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, '' as client_name, bank.name as bank_name, '' as creditor_name"
    hsSql.from='kredit, bank'
    hsSql.where="kredit.bank_id=bank.id"+
                ((iStatus>0)?' and kredit.modstatus=1':' and kredit.modstatus=0')+
                ((iCompanyId>0)?' and if(kredit.creditor>0,kredit.creditor,kredit.client)=:client':'')+
                (!bShowReal?' and kredit.is_tech=1':'')
    hsSql.order="kredit.id desc"

    if(iCompanyId>0)
      hsLong['client']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,KreditSearch.class)
  }

  def csiSelectNewTechKredits(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, company.name as client_name, bank.name as bank_name, '' as creditor_name"
    hsSql.from='kredit, company, bank'
    hsSql.where="kredit.client=company.id and kredit.bank_id=bank.id and kredit.is_tech=1 and kredit.client_id=0 and kredit.modstatus=1"
    hsSql.order="kredit.id desc"

    searchService.fetchData(hsSql,null,null,null,null,KreditSearch.class)
  }

  def csiSelectNewCessionKredits(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, company.name as client_name, bank.shortname as bank_name, '' as creditor_name"
    hsSql.from='kredit, company, bank'
    hsSql.where="kredit.client=company.id and kredit.bank_id=bank.id and kredit.cessionstatus=0 and kredit.kredtype<3 and kredit.modstatus=1"
    hsSql.order="bank.shortname asc, company.name asc, kredit.anumber asc"

    searchService.fetchData(hsSql,null,null,null,null,KreditSearch.class)
  }

  def csiSelectKredit(iId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, company.name as client_name, bank.shortname as bank_name, '' as creditor_name"
    hsSql.from='kredit, company, bank'
    hsSql.where="kredit.client=company.id and kredit.bank_id=bank.id and kredit.id=:kid"
    hsSql.order="kredit.id desc"

    hsLong['kid'] = iId

    searchService.fetchData(hsSql,hsLong,null,null,null,KreditSearch.class)
  }

  def csiSelectKreditSummary(Date dateStart, Date dateEnd, iClientId, iSupplierId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, company.name as client_name, bank.name as bank_name, '' as creditor_name"
    hsSql.from='kredit, company, bank'
    hsSql.where="if(kredit.creditor>0,kredit.creditor,kredit.client)=company.id and kredit.bank_id=bank.id and kredit.modstatus>=0"+
                (iClientId>0?' and kredit.client=:client':'')+
                (iSupplierId>0?' and kredit.bank_id=""':'')+
                (dateStart?' and kredit.adate>=:datestart':'')+
                (dateEnd?' and kredit.adate<=:dateend and kredit.enddate>=:dateend':'')
    hsSql.order="kredit.id desc"

    if(iClientId>0)
      hsLong['client'] = iClientId
    if (dateStart)
      hsString['datestart'] = String.format('%tF',dateStart)
    if (dateEnd)
      hsString['dateend'] = String.format('%tF',dateEnd)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,KreditSearch.class)
  }
}