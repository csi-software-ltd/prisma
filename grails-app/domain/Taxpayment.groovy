class Taxpayment {
  static mapping = { version false }
  static constraints = {
    paydate(nullable:true)
  }

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
  Date inputdate = new Date()
  Integer paystatus = 0
  Date paydate

  Taxpayment setData(_request){
    def oCompany = Company.findByInn(_request.companyinn)
    company_id = oCompany?.id?:0
    companyname = oCompany?.name?:_request.companyname
    inn = _request.companyinn
    summa = _request.taxsumma
    taxdate = new Date(year-1900,month-1,Tax.get(tax_id)?.paydate?:15)
    this
  }

  Taxpayment csiLinkReports(){
    def oCompany = Company.findByInn(inn)
    company_id = oCompany?.id?:0
    companyname = oCompany?.name?:companyname
  }

  Taxpayment computeStatus(){
    paystatus = paystatus>0?paystatus:company_id?0:-1
    this
  }

  Taxpayment csiSetPaystatus(iStatus){
    paystatus = iStatus?:0
    this
  }

  Taxpayment csiSetPaydate(dDate){
    paydate = dDate
    this
  }
}