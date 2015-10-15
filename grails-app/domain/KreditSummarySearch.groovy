class KreditSummarySearch {
  def searchService
  static mapping = { version false }
//////////////kredit///////////////////////
  Integer id
  Integer kredtype
  Integer is_real
  Integer is_tech
  Integer is_realtech
  Integer client
  String bank_id
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
//////////////General//////////////////////
  String bank_name
  BigDecimal debtrub
  BigDecimal debteur
  BigDecimal debtusd
  BigDecimal debtamd

  def csiSelectBankSummary(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, bank.name as bank_name, sum(if(kredit.valuta_id=857,kredit.debt,0)) as debtrub, sum(if(kredit.valuta_id=978,kredit.debt,0)) as debteur, sum(if(kredit.valuta_id=840,kredit.debt,0)) as debtusd, sum(if(kredit.valuta_id=51,kredit.debt,0)) as debtamd"
    hsSql.from='kredit join bank on (kredit.bank_id=bank.id)'
    hsSql.where="kredit.modstatus=1"
    hsSql.group="bank.id"
    hsSql.order="bank.name"

    searchService.fetchData(hsSql,null,null,null,null,KreditSummarySearch.class)
  }
}