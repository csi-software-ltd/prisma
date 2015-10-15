class TaxpaymentSearch {
  def searchService
  static mapping = { version false }

  Integer id
  Integer company_id
  String companyname
  String inn
  BigDecimal summa
  Integer tax_id
  Date taxdate
  Integer month
  Integer kvartal
  Integer year
  Integer taxyear
  Date inputdate
  Integer paystatus
  Date paydate

  def csiSelectTaxes(sCompanyName,iStatus,iTaxId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='taxpayment'
    hsSql.where="1=1"+
                ((iStatus>-100)?' and taxpayment.paystatus=:paystatus':'')+
                ((iTaxId>-100)?' and taxpayment.tax_id=:tax_id':'')+
                (sCompanyName?' and taxpayment.companyname like concat("%",:companyname,"%")':'')
    hsSql.order="taxpayment.inputdate desc, taxpayment.company_id asc, taxpayment.companyname asc"

    if(iStatus>-100)
      hsLong['paystatus'] = iStatus
    if(iTaxId>-100)
      hsLong['tax_id'] = iTaxId
    if(sCompanyName)
      hsString['companyname'] = sCompanyName

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'taxpayment.id',true,TaxpaymentSearch.class)
  }
}