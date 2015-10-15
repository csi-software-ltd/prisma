class Salarycomp {
def searchService
  static mapping = {
    version false
  }
  static constraints = {
    paydate(nullable:true)
  }

  Long id
  Integer is_pers
  Integer company_id
  String companyname
  String companyinn
  String fio
  Long pers_id
  Integer perstype
  String snils
  String position
  String region
  Double overpayment = 0d
  Integer is_noaccount
  Integer month
  Integer year
  Date inputdate = new Date()
  BigDecimal fullsalary
  BigDecimal netsalary
  BigDecimal debtsalary
  BigDecimal ndfl
  BigDecimal debtndfl
  BigDecimal fss_tempinvalid
  BigDecimal debtfss_tempinvalid
  BigDecimal fss_accident
  BigDecimal debtfss_accident
  BigDecimal ffoms
  BigDecimal debtffoms
  BigDecimal pf
  BigDecimal debtpf
  BigDecimal cardmain = 0.0g
  BigDecimal cardadd = 0.0g
  Integer cashsalary = 0
  Integer compstatus
  Integer perstatus
  Integer paidmainstatus = 0
  Integer paidaddstatus = 0
  Integer paidstatus = 0
  Date paydate
  Integer empldebt = 0

  def beforeUpdate(){
    if(is_noaccount) Company.get(company_id)?.csiSetIsTaxdebt(1)?.save()
    else if(paidmainstatus==-1||paidaddstatus==-1) Pers.get(pers_id)?.csiSetIsSalarydebt(1)?.save()
  }

  Salarycomp setData(_request,_repDate){
    companyname = _request.companyname
    companyinn = _request.companyinn
    company_id = Company.findByInn(companyinn)?.id?:0
    fio = is_pers?_request.fio:''
    csiUpdateSnils(_request.snils)
    position = is_pers?_request.position:''
    region = is_pers?_request.region:''
    is_noaccount = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(company_id,1,1).find{it.ibankstatus==1}?0:1
    month = _repDate.getMonth()+1
    year = _repDate.getYear()+1900
    fullsalary = _request.fullsalary
    netsalary = _request.netsalary
    debtsalary = _request.debtsalary?:0g
    ndfl = _request.ndfl?:0.0g
    debtndfl = _request.debtndfl?:0.0g
    fss_tempinvalid = _request.fss_tempinvalid?:0.0g
    debtfss_tempinvalid = _request.debtfss_tempinvalid?:0.0g
    fss_accident = _request.fss_accident?:0.0g
    debtfss_accident = _request.debtfss_accident?:0.0g
    ffoms = _request.ffoms?:0.0g
    debtffoms = _request.debtffoms?:0.0g
    pf = _request.pf?:0.0g
    debtpf = _request.debtpf?:0.0g
    compstatus = company_id?1:0
    //if (is_pers&&(_request.ndfl||_request.fss_tempinvalid||_request.fss_accident||_request.ffoms||_request.pf)) new Salarycomp(is_pers:0).setData(_request,_repDate).save(failOnError:true)
    this
  }

  Salarycomp updateCompBuhData(_request){
    if(!is_pers){
      fullsalary = _request.fullsalary?:0.0g
      ndfl = _request.ndfl?:0.0g
      debtndfl = _request.debtndfl?:0.0g
      fss_tempinvalid = _request.fss_tempinvalid?:0.0g
      debtfss_tempinvalid = _request.debtfss_tempinvalid?:0.0g
      fss_accident = _request.fss_accident?:0.0g
      debtfss_accident = _request.debtfss_accident?:0.0g
      ffoms = _request.ffoms?:0.0g
      debtffoms = _request.debtffoms?:0.0g
      pf = _request.pf?:0.0g
      debtpf = _request.debtpf?:0.0g
    }
    this
  }

  Salarycomp updatePersBuhData(_request){
    if(is_pers){
      fullsalary = _request.fullsalary?:0.0g
      netsalary = fullsalary * 0.87
      debtsalary = _request.debtsalary?:0.0g
    }
    this
  }

  Salarycomp csiUpdateSnils(_snils){
    snils = is_pers?_snils:''
    def lsPersId = new CompersSearch().csiFindCompers(company_id?:-1,snils,-100).collect{ it.pers_id }.unique()
    pers_id = !is_pers||lsPersId.size()!=1?0:lsPersId[0]
    perstype = !is_pers?0:Pers.get(pers_id)?.perstype?:0
    perstatus = !is_pers?-1:pers_id?1:0
    this
  }

  Salarycomp csiUpdateInn(_inn){
    company_id = Company.findByInn(_inn)?.id?:0
    compstatus = company_id?1:0
    is_noaccount = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(company_id,1,1).find{it.ibankstatus==1}?0:1
    if (is_pers) csiUpdateSnils(snils)
    else {
      Salarycomp.findAllByCompanyinnAndIs_persAndMonthAndYear(companyinn,1,month,year).each{ it.csiUpdateInn(_inn).save(flush:true) }
    }
    companyinn = _inn
    this
  }

  Salarycomp accrue(){
    compstatus = 2
    perstatus = perstatus>0?2:perstatus
    this
  }

  Salarycomp disaccrue(){
    compstatus = 1
    perstatus = perstatus==2?1:perstatus
    this
  }

  BigDecimal computesum(){
    fullsalary +
    netsalary +
    debtsalary +
    ndfl +
    debtndfl +
    fss_tempinvalid +
    debtfss_tempinvalid +
    fss_accident +
    debtfss_accident +
    ffoms +
    debtffoms +
    pf +
    debtpf
  }

  Salarycomp computeempldebt(){
    empldebt = cardadd - debtsalary
    cardadd = debtsalary
    this
  }

  Salarycomp csiUpdateDirectorsCardsSumma(_summa) {
    is_noaccount = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(company_id,1,1).find{it.ibankstatus==1}?0:1
    if(!is_noaccount){
      paidmainstatus = Persaccount.findByPers_idAndModstatusAndIs_mainAndBank_idNotEqual(pers_id,1,1,'000000000')?0:-1
      paidaddstatus = Persaccount.findByPers_idAndModstatusAndIs_mainAndBank_idNotEqual(pers_id,1,0,'000000000')?0:-1
      if (Persaccount.findByPers_idAndModstatusAndIs_main(pers_id,1,1)?.bank_id=='000000000') cashsalary = Math.ceil(_summa).toInteger()
      else cardmain = _summa
    } else {
      cashsalary = Math.ceil(_summa).toInteger()
      paidmainstatus = -1
      paidaddstatus = -1
    }
    if (Persaccount.findByPers_idAndModstatusAndIs_main(pers_id,1,0)?.bank_id=='000000000') cashsalary -= netsalary - cardmain + debtsalary
    else cardadd = netsalary - cardmain + debtsalary
    this
  }

  Salarycomp csiUpdateTechniciansCardsSumma() {
    is_noaccount = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(company_id,1,1).find{it.ibankstatus==1}?0:1
    cardadd = netsalary + debtsalary
    paidaddstatus = is_noaccount?-1:Persaccount.findByPers_idAndModstatusAndIs_main(pers_id,1,0)?0:-1
    this
  }

  Salarycomp csiUpdateEmployeeCardsSumma() {
    is_noaccount = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(company_id,1,1).find{it.ibankstatus==1}?0:1
    paidmainstatus = is_noaccount?-1:Persaccount.findByPers_idAndModstatusAndIs_main(pers_id,1,1)?0:-1
    cardmain = netsalary + debtsalary
    if(is_noaccount) {
      User.findByPers_idAndModstatus(pers_id,1,[sort:'precassadebt',order:'desc'])?.updatePredebt(Math.ceil(netsalary).toInteger()-cashsalary)?.save(flush:true,failOnError:true)
      cashsalary = Math.ceil(netsalary).toInteger()
    }
    this
  }

  Salarycomp csiUpdateCashsalary(_summa) {
    cashsalary += (Math.ceil(_summa).toInteger()>1?Math.ceil(_summa).toInteger():0)
    this
  }

  Salarycomp csiUpdateCardmain(_summa) {
    cardadd -= _summa - cardmain
    cashsalary -= Math.ceil(_summa - cardmain).toInteger()
    cardmain = _summa
    this
  }

  Salarycomp csiSetPaidstatus(iStatus) {
    paidstatus = iStatus?:0
    this
  }

  Salarycomp csiSetPaidmainstatus(iStatus) {
    paidmainstatus = iStatus?:0
    this
  }

  Salarycomp csiSetPaidaddstatus(iStatus) {
    paidaddstatus = iStatus?:0
    this
  }

  Salarycomp csiClearIs_noaccount() {
    is_noaccount = 0
    this
  }

  Salarycomp clearCashsalary() {
    cashsalary = 0
    this
  }

  Salarycomp updatePaidstatuses(_status,_isMain) {
    if(_isMain==1) csiSetPaidmainstatus(_status)
    else if(_isMain==0) csiSetPaidaddstatus(_status)
    this
  }

  Salarycomp csiSetPaydate(dDate) {
    paydate = dDate
    this
  }

  def csiSelectCompanies(dDate,sName,iStatus,lsInn,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]
    def hsList=[:]

    hsSql.select="*"
    hsSql.from='salarycomp'
    hsSql.where="is_pers=0"+
                (dDate?' AND year =:year AND month =:month':'')+
                ((sName!='')?' AND companyname like concat("%",:cname,"%")':'')+
                ((iStatus>-100)?' AND compstatus =:compstatus':'')+
                (lsInn?.size()>0?' AND companyinn in (:innlist)':'')
    hsSql.order="companyname asc"

    if(dDate){
      hsLong['month'] = dDate.getMonth()+1
      hsLong['year'] = dDate.getYear()+1900
    }
    if(sName!='')
      hsString['cname'] = sName
    if(iStatus>-100)
      hsLong['compstatus'] = iStatus
    if(lsInn?.size()>0)
      hsList['innlist'] = lsInn

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,hsList,null,iMax,iOffset,'id',true,Salarycomp.class)
  }

  def csiSelectPers(dDate,sName,iType,iStatus,lsInn,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]
    def hsList=[:]

    hsSql.select="*"
    hsSql.from='salarycomp'
    hsSql.where="is_pers=1"+
                (dDate?' AND year =:year AND month =:month':'')+
                ((sName!='')?' AND fio like concat("%",:pname,"%")':'')+
                ((iType>-100)?' AND perstype =:perstype':'')+
                ((iStatus>-100)?' AND perstatus =:perstatus':'')+
                (lsInn?.size()>0?' AND companyinn in (:innlist)':'')
    hsSql.order="fio asc"

    if(dDate){
      hsLong['month'] = dDate.getMonth()+1
      hsLong['year'] = dDate.getYear()+1900
    }
    if(sName!='')
      hsString['pname'] = sName
    if(iType>-100)
      hsLong['perstype'] = iType
    if(iStatus>-100)
      hsLong['perstatus'] = iStatus
    if(lsInn?.size()>0)
      hsList['innlist'] = lsInn

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,hsList,null,iMax,iOffset,'id',true,Salarycomp.class)
  }

  def csiSelectCompanynames(sName){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='salarycomp'
    hsSql.where="1=1"+
                ((sName!='')?' AND companyname like concat("%",:company_name,"%")':'')
    hsSql.order="companyname asc"
    hsSql.group="companyname"

    if(sName!='')
      hsString['company_name']=sName

    searchService.fetchData(hsSql,null,null,hsString,null,Salarycomp.class,10)
  }

  def csiSelectPersnames(sName){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='salarycomp'
    hsSql.where="1=1"+
                ((sName!='')?' AND fio like concat("%",:pname,"%")':'')
    hsSql.order="fio asc"
    hsSql.group="fio"

    if(sName!='')
      hsString['pname']=sName

    searchService.fetchData(hsSql,null,null,hsString,null,Salarycomp.class,10)
  }

}