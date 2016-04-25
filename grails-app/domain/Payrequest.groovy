class Payrequest {
  def searchService
  def paymentService
  def agentKreditService

  static mapping = {
    version false
  }
  static constraints = {
    execdate(nullable:true)
    indate(nullable:true)
    sfacturadate(nullable:true)
  }
  
  public static final PAY_CAT_AGR=1
  public static final PAY_CAT_BUDG=2
  public static final PAY_CAT_PERS=3
  public static final PAY_CAT_OTHER=4
  public static final PAY_CAT_BANK=5
  public static final PAY_CAT_ORDER=6

  Integer id
  Date paydate
  Date inputdate = new Date()
  Date execdate
  Date indate
  BigDecimal summa
  BigDecimal summands
  Integer is_nds = 1
  Integer modstatus = 0
  Integer instatus = 0
  String fromcompany = ''
  String frominn = ''
  Integer fromcompany_id = 0
  Integer bankaccount_id = 0
  String tocompany = ''
  Integer tocompany_id = 0
  Integer tobankaccount_id = 0
  String toinn = ''
  String tokpp = ''
  String tobank = ''
  String tobankbik = ''
  String toaccount = ''
  String tocorraccount = ''
  Integer tax_id = 0
  Integer kbkrazdel_id = 0
  String platperiod = ''
  Integer paytype = 0
  Integer paycat = 0
  Integer payway = 0
  Integer paygroup = 0
  Integer agreementtype_id = 0
  Integer agreement_id = 0
  Integer is_dop = 0
  Integer is_fine = 0
  Long pers_id = 0
  Integer taskpay_id = 0
  Integer payment_id = 0
  String agreementnumber = ''
  String destination = ''
  String oktmo  = ''
  String comment = ''
  Long initiator = 0
  Long clientadmin = 0
  Long tagadmin = 0
  Integer expensetype_id = 0
  Integer project_id = 0
  Integer client_id = 0
  Integer subclient_id = 0
  Integer percenttype = 0
  Double compercent = 0d
  Double subcompercent = 0d
  Double midpercent = 0d
  Double supcompercent = 0d
  BigDecimal comission = 0.0g
  BigDecimal subcomission = 0.0g
  BigDecimal midcomission = 0.0g
  BigDecimal supcomission = 0.0g
  BigDecimal clientcommission = 0.0g
  BigDecimal agentcommission = 0.0g
  BigDecimal clientdelta = 0.0g
  Integer confirmstatus = 0
  Integer is_clientcommission = 0
  Integer is_midcommission = 0
  String sfactura = ''
  Date sfacturadate
  Integer deal_id = 0
  Integer agentagr_id = 0
  Integer agent_id = 0
  String tagcomment = ''
  Integer is_bankmoney = 0
  Double payoffperc = 0d
  BigDecimal payoffsumma = 0.0g
  BigDecimal depbody = 0.0g
  BigDecimal depprc = 0.0g
  Integer cashrequest_id = 0
  Integer is_generate = 0
  Long file_id = 0
  Integer car_id = 0
  Integer agrpayment_id = 0
  Integer is_third = 0
  Integer is_payconfirm = 0
  Integer related_id = 0

////////////////////////////////////////////////////////////
  def beforeInsert(){
    if (!project_id) project_id = Project.findByIs_main(1)?.id?:0
    clientdelta = is_bankmoney ? 0.0g : summa - (paytype in [3,4,6]?summa:(clientcommission+agentcommission)) - (paytype in [1,3,11]?-comission:comission) - (paytype in [1,3,11]?-midcomission:midcomission)
    platperiod = platperiod?:String.format('%tm.%<tY',paydate)
    instatus = instatus?:paytype in [2,11]?1:0
    paygroup = (paycat==Payrequest.PAY_CAT_PERS||paycat==Payrequest.PAY_CAT_BUDG)?1:(client_id>0||agreementtype_id==3)?2:(paycat==Payrequest.PAY_CAT_AGR&&agreementtype_id==2)?3:4
    depbody = paytype==2?summa:depbody
    if (paytype==9) Holding.findByName('dopcardsaldo').changeSaldo(-summa).save(failOnError:true)
    if (modstatus==3 && agreementtype_id==4 && agreement_id>0 && paytype in [1,3]){
      Lizing.withNewSession {
        def oLizing = Lizing.get(agreement_id)
        if (oLizing && oLizing.startsaldodate<paydate)
          oLizing.updateDebt(agentKreditService.computeLizingDebt(oLizing)-summa).save(flush:true)
      }
    }
  }

  def afterInsert(){
    if (paytype==2&&agreementtype_id==13&&agreement_id>0&&depbody>0) Indepositproject.withNewSession { new Indepositproject(indeposit_id:agreement_id,operationdate:paydate,payrequest_id:id).csiSetProject(Project.findByIs_main(1)?.id).csiSetSumma(depbody).save(flush:true) }
    if (paytype==1&&agreementtype_id==13&&agreement_id>0)
      Indepositproject.withNewSession {
        depbody = summa
        new Project().csiSearchIndepositProjects(agreement_id).each{ prj ->
          def prcsum = prj.computeIndepositProjectPercent(agreement_id,paydate)
          if (prcsum>0&&depbody>=prcsum) {
            new Indepositproject(indeposit_id:agreement_id,operationdate:paydate,payrequest_id:id).csiSetProject(prj.id).csiSetSumma(prcsum).csiSetIsPercent().save(flush:true)
            depbody -= prcsum
          }
        }
        depprc = summa - depbody
        depbody += (summa * (Indeposit.get(agreement_id)?.comrate?:0)) / (100 - (Indeposit.get(agreement_id)?.comrate?:0))
        new Indepositproject(indeposit_id:agreement_id,operationdate:paydate,payrequest_id:id).csiSetProject(Project.findByIs_main(1)?.id).csiSetSumma(-depbody).save(flush:true)
        save(flush:true)
      }
  }

  def beforeDelete(){
    if(modstatus>1) Payrequest.withNewSession{ paymentService.undoRequest(this) }
    if(paytype==10) Payrequest.withNewSession{ Payrequest.findAllByRelated_id(id).each{ it.csiSetRelated(0).save(flush:true) } }
    if (paytype==2&&agreementtype_id==3&&Kredit.get(agreement_id)?.kredtype in [2,4]){
      Kreditline.withNewSession { Kreditline.findByKredit_idAndPaydateAndSummarub(agreement_id,paydate,summa)?.delete(flush:true) }
    }
    if (agreementtype_id==13&&agreement_id>0&&paytype in [1,2]){
      Indepositproject.withNewSession { Indepositproject.findAllByPayrequest_id(id).each{ it.delete(flush:true) } }
    }
    if (modstatus==3 && agreementtype_id==4 && agreement_id>0 && paytype in [1,3]){
      Lizing.withNewSession {
        def oLizing = Lizing.get(agreement_id)
        if (oLizing && oLizing.startsaldodate<paydate)
          oLizing.updateDebt(agentKreditService.computeLizingDebt(oLizing)+summa).save(flush:true)
      }
    }
  }

  def beforeUpdate(){
    if(isDirty('modstatus')&&getPersistentValue('modstatus')!=3&&modstatus==2) Payrequest.withNewSession{ paymentService.doRequest(this) }
    if(isDirty('modstatus')&&getPersistentValue('modstatus')==2&&(modstatus==1||modstatus==0)) Payrequest.withNewSession{ paymentService.undoRequest(this) }
    if(isDirty('summa')&&modstatus>=2&&is_generate==1) Payrequest.withNewSession{ paymentService.undoRequest(this); paymentService.doRequest(this); }
    clientdelta = is_bankmoney ? 0.0g : summa - (paytype in [3,4,6]?summa:(clientcommission+agentcommission)) - (paytype in [1,3,11]?-comission:comission) - (paytype in [1,3,11]?-midcomission:midcomission)
    paygroup = (paycat==Payrequest.PAY_CAT_PERS||paycat==Payrequest.PAY_CAT_BUDG)?1:(client_id>0||agreementtype_id==3)?2:(paycat==Payrequest.PAY_CAT_AGR&&agreementtype_id==2)?3:4
    changeDCSaldo()
    //////////lizing>>>
    if (isDirty('modstatus') && modstatus==3 && agreementtype_id==4 && agreement_id>0 && paytype in [1,3]){
      Lizing.withNewSession {
        def oLizing = Lizing.get(agreement_id)
        if (oLizing && oLizing.startsaldodate<paydate)
          oLizing.updateDebt(agentKreditService.computeLizingDebt(oLizing)-summa).save(flush:true)
      }
    }
    //////////lizing<<<
    ///////indeposit>>>
    if (paytype==2&&agreementtype_id==13&&agreement_id>0&&depbody>0){
      Indepositproject.withNewSession {
        Indepositproject.findAllByPayrequest_id(id).each{ it.delete(flush:true) }
        new Indepositproject(indeposit_id:agreement_id,operationdate:paydate,payrequest_id:id).csiSetProject(Project.findByIs_main(1)?.id).csiSetSumma(depbody).save(flush:true)
      }
    } else if (paytype==1&&agreementtype_id==13&&agreement_id>0){
      if (isDirty('agreementtype_id')||isDirty('agreement_id'))
        Indepositproject.withNewSession {
          Indepositproject.findAllByPayrequest_id(id).each{ it.delete(flush:true) }
          depbody = summa
          new Project().csiSearchIndepositProjects(agreement_id).each{ prj ->
            def prcsum = prj.computeIndepositProjectPercent(agreement_id,paydate)
            if (prcsum>0&&depbody>=prcsum) {
              new Indepositproject(indeposit_id:agreement_id,operationdate:paydate,payrequest_id:id).csiSetProject(prj.id).csiSetSumma(prcsum).csiSetIsPercent().save(flush:true)
              depbody -= prcsum
            }
          }
          depprc = summa - depbody
          depbody += (summa * (Indeposit.get(agreement_id)?.comrate?:0)) / (100 - (Indeposit.get(agreement_id)?.comrate?:0))
          new Indepositproject(indeposit_id:agreement_id,operationdate:paydate,payrequest_id:id).csiSetProject(Project.findByIs_main(1)?.id).csiSetSumma(-depbody).save(flush:true)
          save(flush:true)
        }
    } else Indepositproject.withNewSession { Indepositproject.findAllByPayrequest_id(id).each{ it.delete(flush:true) } }
    ///////indeposit<<<
  }
 ////////////////////////////////////////////////////////////
  def csiSelectPayrequest(hsInrequest,iMax,iOffset,iVision=0){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]
    def oCompany = Company.findByName(hsInrequest?.companyname?:'')

    hsSql.select="*"
    hsSql.from="payrequest"
    hsSql.where="1=1"+
      (hsInrequest?.pid>0?' AND id=:pid':'')+
      ((hsInrequest?.companyname)?' AND (fromcompany like concat("%",:companyname,"%") or tocompany like concat("%",:companyname,"%")'+(oCompany?' or fromcompany_id=:company_id or tocompany_id=:company_id':'')+')':'')+
      ((hsInrequest?.modstatus>-100)?' AND modstatus=:modstatus':'')+
      ((hsInrequest?.instatus>-100)?' AND instatus=:instatus':'')+
      ((hsInrequest?.paytype>-100)?' AND paytype=:paytype':' and paytype!=10')+
      ((hsInrequest?.paycat>-100)?' AND paycat=:paycat':'')+
      ((hsInrequest?.paydate)?' AND paydate=:paydate':'')+
      ((hsInrequest?.is_noclient)?' AND client_id=0':'')+
      ((hsInrequest?.is_noinner)?' AND paytype!=3':'')+
      ((hsInrequest?.is_notag)?' AND expensetype_id=0':'')+
      ((hsInrequest?.is_payconfirm)?' AND is_payconfirm=1':'')+
      (hsInrequest?.project_id>0?' AND project_id=:project_id':'')+
      ((hsInrequest?.platperiod_year)?((hsInrequest?.platperiod_month)?' AND platperiod=:platperiod':' AND platperiod like concat("%.",:platperiod,"%")'):'')+
      (iVision>0?' and ((fromcompany_id=0 and tocompany_id=0) or IFNULL((select visualgroup_id from company where company.id=payrequest.fromcompany_id and is_holding=1),0)=:visualgroup_id or IFNULL((select visualgroup_id from company where company.id=payrequest.tocompany_id and is_holding=1),0)=:visualgroup_id)':'')
    hsSql.order="paydate desc, id desc"

    if(hsInrequest?.pid>0)
      hsInt['pid'] = hsInrequest?.pid
    if(hsInrequest?.companyname)
      hsString['companyname'] = hsInrequest?.companyname
    if(oCompany)
      hsInt['company_id'] = oCompany.id
    if(hsInrequest?.modstatus>-100)
      hsInt['modstatus'] = hsInrequest?.modstatus
    if(hsInrequest?.instatus>-100)
      hsInt['instatus'] = hsInrequest?.instatus
    if(hsInrequest?.paytype>-100)
      hsInt['paytype'] = hsInrequest?.paytype
    if(hsInrequest?.paycat>-100)
      hsInt['paycat'] = hsInrequest?.paycat
    if(hsInrequest?.project_id>0)
      hsInt['project_id'] = hsInrequest.project_id
    if(hsInrequest?.paydate)
      hsString['paydate'] = String.format('%tF',hsInrequest?.paydate)
    if(hsInrequest?.platperiod_year)
      if(hsInrequest?.platperiod_month)
        hsString['platperiod'] = String.format('%tm.%<tY',new Date(hsInrequest?.platperiod_year-1900,hsInrequest?.platperiod_month-1,1))
      else
        hsString['platperiod'] = String.format('%tY',new Date(hsInrequest?.platperiod_year-1900,1,1))
    if(iVision>0)
      hsInt['visualgroup_id'] = iVision

    searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,null,null,iMax,iOffset,'id',true,Payrequest.class)
  }

  def csiSelectKreditOutPayrequests(iKreditId){
    def hsSql = [select:'',from:'',where:'',order:'']
    def hsLong = [:]

    hsSql.select="*"
    hsSql.from="payrequest"
    hsSql.where="agreementtype_id=3 and agreement_id=:kredit_id and modstatus>=0 and (paytype in (1,5) or (paytype=2 and is_dop=1))"
    hsSql.order="paydate desc"

    hsLong['kredit_id'] = iKreditId

    searchService.fetchData(hsSql,hsLong,null,null,null,Payrequest.class)
  }

  def csiSelectRepayments(iClientId,iSubClientId,iAgentagrId){
    def hsSql = [select:'',from:'',where:'',order:'']
    def hsLong = [:]

    hsSql.select="*"
    hsSql.from="payrequest"
    hsSql.where="agent_id=0 and client_id=:client_id and subclient_id=:subclient_id and paytype=4 and modstatus>=0"+
      ((iAgentagrId>0)?' AND agentagr_id=:agentagr_id':'')
    hsSql.order="modstatus asc, inputdate desc"

    hsLong['client_id'] = iClientId
    hsLong['subclient_id'] = iSubClientId
    if(iAgentagrId>0)
      hsLong['agentagr_id'] = iAgentagrId

    searchService.fetchData(hsSql,hsLong,null,null,null,Payrequest.class)
  }

  def csiSelectAgentpayments(iClientId){
    def hsSql = [select:'',from:'',where:'',order:'']
    def hsLong = [:]

    hsSql.select="*"
    hsSql.from="payrequest"
    hsSql.where="agent_id>0 and client_id=:client_id and paytype=6 and modstatus>=0"
    hsSql.order="modstatus asc, inputdate desc"

    hsLong['client_id'] = iClientId

    searchService.fetchData(hsSql,hsLong,null,null,null,Payrequest.class,10)
  }

  def csiSelectAgentpaymentsByDate(iClientId,dDate){
    def hsSql = [select:'',from:'',where:'',order:'']
    def hsLong = [client_id:iClientId]
    def hsString = [:]

    hsSql.select="*"
    hsSql.from="payrequest"
    hsSql.where="agent_id>0 and client_id=:client_id and paytype=6 and modstatus>=0"+
      (dDate?' AND (inputdate>=:date or execdate>=:date or execdate is null)':'')
    hsSql.order="inputdate asc"

    if (dDate)
      hsString['date'] = String.format('%tF',dDate)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,Payrequest.class)
  }
 ////////////////////////////////////////////////////////////
  Payrequest csiSetModstatus(iStatus){
    modstatus = iStatus
    if (modstatus==2) {
      if(is_payconfirm) modstatus = 3
      csiSetExecdate(new Date())
      instatus = 1
    } else if (modstatus==1||modstatus==0){
      csiSetExecdate(null)
      instatus = 0
    }

    this
  }

  Payrequest csiSetTaskpay_id(iTaskpay_id){
    taskpay_id = iTaskpay_id
    this
  }

  Payrequest csiSetPayway(iPayway){
    payway = iPayway?:0
    this
  }

  Payrequest csiSetExecdate(dExecdate){
    execdate = dExecdate
    this
  }

  Payrequest csiSetIndate(dIndate){
    indate = dIndate
    this
  }

  Payrequest csiSetFileId(iFileId){
    file_id = iFileId?:file_id
    this
  }

  Payrequest csiSetRelated(iId){
    related_id = iId?:0
    this
  }

  Payrequest receiveincome(Date _indate){
    if (instatus==1) {
      instatus = 2
      csiSetIndate(_indate)
    }
    this
  }

  Payrequest cancellreceiveincome(){
    if (instatus==2) {
      instatus = 1
      csiSetIndate(null)
    }
    this
  }

  Payrequest confirmincome(iStatus){
    if (paytype in 1..2) {
      instatus = 3
      indate = paydate
    } else if (paytype==3&&iStatus>instatus) {
      instatus = iStatus
      if (iStatus==3) indate = paydate
    }
    this
  }

  Payrequest csisetrefill(){
    if (paytype==1) {
      paytype = 5
    }
    this
  }

  Payrequest cancellrefill(){
    if (paytype==5&&!client_id) {
      paytype = 1
    }
    this
  }

  Payrequest closerefill(){
    if (paytype==5&&modstatus==2) {
      csiSetModstatus(3)
      instatus = 3
      csiSetIndate(new Date())
    }
    this
  }

  Payrequest csidecline(){
    csiSetModstatus(-1)
    this
  }

  Payrequest csirestore(){
    csiSetModstatus(0)
    this
  }

  Payrequest filldata(Salarycomp _salarycomp, Boolean _isAdd = false){
    fromcompany = _salarycomp.companyname
    frominn = _salarycomp.companyinn
    fromcompany_id = _salarycomp.company_id
    platperiod = String.format('%tm.%<tY',new Date(_salarycomp.year-1900,_salarycomp.month-1,1))
    if (_salarycomp.is_pers) {
      pers_id = _salarycomp.pers_id
      def oAccount = Persaccount.findByPers_idAndModstatusAndIs_main(pers_id,1,_isAdd?0:1)
      def oBank = Bank.get(oAccount.bank_id)
      tobank = oBank.name
      tobankbik = oAccount.bank_id
      tocorraccount = oBank.coraccount
      toaccount = oAccount.paccount
      destination = "Перевод зарплаты на ${_isAdd?'дополнительную':'основную'} карту работнику "+_salarycomp.fio+" за ${String.format('%tm.%<tY',new Date(_salarycomp.year-1900,_salarycomp.month-1,1))}"
      if (_isAdd) csiSetDop()
      is_nds = 0
    }
    this
  }

  Payrequest filldata(Taxpayment _taxpayment){
    paytype = 1
    paycat = Payrequest.PAY_CAT_BUDG
    tax_id = _taxpayment.tax_id
    kbkrazdel_id = Tax.get(tax_id)?.kbkrazdel_id?:0
    paydate = _taxpayment.taxdate
    fromcompany = _taxpayment.companyname
    frominn = _taxpayment.inn
    fromcompany_id = _taxpayment.company_id
    platperiod = String.format('%tm.%<tY',new Date(_taxpayment.year-1900,_taxpayment.month-1,1))
    summa = _taxpayment.summa
    summands = 0.0g
    is_nds = 0
    this
  }

  Payrequest computePaydate(){
    def computeDate = new Date()
    computeDate.setDate(Tools.getIntVal(Dynconfig.findByName('payrequest.offsalary.paydate')?.value,15))
    while((computeDate.getDay() in [0,6] && !Holiday.findByHdate(computeDate))||(computeDate.getDay() in 1..5 && Holiday.findByHdate(computeDate))){
      computeDate--
    }
    paydate = computeDate
    this
  }
//////////////////////////////////////////////////////////// 
  Payrequest computePaydate(Date computeDate){
    while((computeDate.getDay() in [0,6] && !Holiday.findByHdate(computeDate))||(computeDate.getDay() in 1..5 && Holiday.findByHdate(computeDate))){
      computeDate++
    }
    paydate = computeDate
    this
  }
//////////////////////////////////////////////////////////// 
  Payrequest csiSetSpaceAgrData(Space _agr){
    def oFromCompany = Company.get(_agr.arendator)
    fromcompany = oFromCompany.name
    frominn = oFromCompany.inn
    fromcompany_id = oFromCompany.id
    def oToCompany = Company.get(_agr.arendodatel)
    tocompany = oToCompany.name
    toinn = oToCompany.inn
    tocompany_id = oToCompany.id
    if (_agr.bank_id){
      def oBank = Bank.get(_agr.bank_id)
      tobank = oBank?.name?:''
      tobankbik = oBank?.id?:''
      tocorraccount = oBank?.coraccount?:''
      def oAccount = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(tocompany_id,1,oBank?.id?:'',1)
      toaccount = oAccount?.schet?:''
      tobankaccount_id = oAccount?.id?:0
    }
    agreementnumber = _agr.anumber
    this
  }

  Payrequest csiSetServiceAgrData(Service _agr){
    def oFromCompany = Company.get(_agr.zcompany_id)
    fromcompany = oFromCompany.name
    frominn = oFromCompany.inn
    fromcompany_id = oFromCompany.id
    def oToCompany = Company.get(_agr.ecompany_id)
    tocompany = oToCompany.name
    toinn = oToCompany.inn
    tocompany_id = oToCompany.id
    if (_agr.ebank_id){
      def oBank = Bank.get(_agr.ebank_id)
      tobank = oBank?.name?:''
      tobankbik = oBank?.id?:''
      tocorraccount = oBank?.coraccount?:''
      def oAccount = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(tocompany_id,1,oBank?.id?:'',1)
      toaccount = oAccount?.schet?:''
      tobankaccount_id = oAccount?.id?:0
    }
    agreementnumber = _agr.anumber
    this
  }

  Payrequest csiSetServiceAccData(String _tobank){
    def oBank = Bank.get(_tobank)
    tobank = oBank?.name?:''
    tobankbik = oBank?.id?:''
    tocorraccount = oBank?.coraccount?:''
    def oAccount = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(tocompany_id,1,oBank?.id?:'',1)
    toaccount = oAccount?.schet?:''
    tobankaccount_id = oAccount?.id?:0
    this
  }

  Payrequest csiSetSmrAgrData(Smr _agr){
    def oFromCompany = Company.get(_agr.client)
    fromcompany = oFromCompany.name
    frominn = oFromCompany.inn
    fromcompany_id = oFromCompany.id
    def oToCompany = Company.get(_agr.supplier)
    tocompany = oToCompany.name
    toinn = oToCompany.inn
    tocompany_id = oToCompany.id
    if (_agr.sbank_id){
      def oBank = Bank.get(_agr.sbank_id)
      tobank = oBank?.name?:''
      tobankbik = oBank?.id?:''
      tocorraccount = oBank?.coraccount?:''
      def oAccount = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(tocompany_id,1,oBank?.id?:'',1)
      toaccount = oAccount?.schet?:''
      tobankaccount_id = oAccount?.id?:0
    }
    agreementnumber = _agr.anumber
    this
  }

  Payrequest csiSetKreditAgrData(Kredit _agr){
    def oFromCompany = Company.get(_agr.creditor?:_agr.client)
    fromcompany = oFromCompany.name
    frominn = oFromCompany.inn
    fromcompany_id = oFromCompany.id
    if (_agr.bank_id){
      def oBank = Bank.get(_agr.bank_id)
      tocompany = oBank?.name?:''
      tobank = oBank?.name?:''
      tobankbik = oBank?.id?:''
      tocorraccount = oBank?.coraccount?:''
      toinn = ''
      tocompany_id = 0
    }
    agreementnumber = _agr.anumber
    this
  }

  Payrequest csiSetLizingAgrData(Lizing _agr){
    def oFromCompany = Company.get(_agr.creditor?:_agr.arendator)
    fromcompany = oFromCompany.name
    frominn = oFromCompany.inn
    fromcompany_id = oFromCompany.id
    def oToCompany = Company.get(_agr.arendodatel)
    tocompany = oToCompany.name
    toinn = oToCompany.inn
    tocompany_id = oToCompany.id
    agreementnumber = _agr.anumber
    this
  }

  Payrequest csiSetAccData(String _tobank){
    tobankbik = _tobank?:''
    tobank = Bank.get(tobankbik?:'')?.name?:''
    tocorraccount = Bank.get(tobankbik?:'')?.coraccount?:''
    def oAccount = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(tocompany_id,1,tobankbik,1)
    toaccount = oAccount?.schet?:''
    tobankaccount_id = oAccount?.id?:0
    this
  }

  Payrequest fillAgrDataFrom(Trade _agr){
    def oFromCompany = Company.get(_agr.client)
    fromcompany = oFromCompany.name
    frominn = oFromCompany.inn
    fromcompany_id = oFromCompany.id
    def oToCompany = Company.get(_agr.supplier)
    tocompany = oToCompany.name
    toinn = oToCompany.inn
    tocompany_id = oToCompany.id
    agreementnumber = _agr.anumber
    this
  }

  Payrequest csiSetLicenseAgrData(License _agr){
    def oFromCompany = Company.get(_agr.company_id)
    fromcompany = oFromCompany.name
    frominn = oFromCompany.inn
    fromcompany_id = oFromCompany.id
    def oToCompany = Company.get(_agr.sro_id)
    tocompany = oToCompany.name
    toinn = oToCompany.inn
    tocompany_id = oToCompany.id
    agreementnumber = _agr.anumber
    this
  }

  Payrequest fillRelatedFrom(Payrequest _prequest){
    paydate = _prequest.paydate
    csiSetExecdate(_prequest.paydate)
    summa = _prequest.summa
    summands = _prequest.summands
    is_nds = _prequest.is_nds
    csiSetModstatus(3)

    fromcompany = _prequest.tocompany
    frominn = _prequest.toinn
    fromcompany_id = _prequest.tocompany_id
    bankaccount_id = _prequest.tobankaccount_id

    tocompany = _prequest.fromcompany
    tocompany_id = _prequest.fromcompany_id
    tobankaccount_id = _prequest.bankaccount_id
    toinn = _prequest.frominn

    paytype = 10
    paycat = 4

    destination = "основной платеж: $_prequest.id"
    expensetype_id = _prequest.expensetype_id
    project_id = _prequest.project_id

    this
  }

  Payrequest createFromPayment(Payment _payment){
    paydate = _payment.paydate
    csiSetExecdate(_payment.paydate)
    summa = _payment.summa
    summands = _payment.summands
    csiSetModstatus(3)
    fromcompany = _payment.fromcompany
    frominn = _payment.frominn
    fromcompany_id = _payment.fromcompany_id
    bankaccount_id = Bankaccount.findByCompany_idAndBank_idAndSchet(fromcompany_id,_payment.frombankbik,_payment.fromaccount)?.id?:0
    tocompany = _payment.tocompany
    tocompany_id = _payment.tocompany_id
    tobankaccount_id = Bankaccount.findByCompany_idAndBank_idAndSchet(tocompany_id,_payment.tobankbik,_payment.toaccount)?.id?:0
    toinn = _payment.toinn
    tokpp = _payment.tokpp
    tobank = _payment.tobank
    tobankbik = _payment.tobankbik
    toaccount = _payment.toaccount
    tocorraccount = _payment.tocorraccount
    paytype = _payment.is_internal?3:_payment.paytype
    paycat = _payment.paycat
    agreement_id = _payment.agreement_id
    agreementtype_id = _payment.agreementtype_id
    agreementnumber = _payment.agreementnumber
    is_dop = _payment.is_dop
    is_fine = _payment.is_fine
    pers_id = _payment.pers_id
    destination = _payment.destination?:receiveDestination()
    comment = 'сгенерирован из платежа по выписке'
    is_generate = 1
    is_third = _payment.is_third
    project_id = _payment.project_id
    expensetype_id = _payment.expensetype_id
    car_id = _payment.car_id
    tagcomment = _payment.tagcomment
    client_id = _payment.client_id
    subclient_id = _payment.subclient_id
    is_bankmoney = _payment.is_bankmoney
    if(paytype==2) confirmincome(3)
    if((paytype==1&&!bankaccount_id)||(paytype==2&&!tobankaccount_id)) return null
    def oKredit = Kredit.get(agreement_id)
    if (agreementtype_id==3&&!is_fine){
      if (paytype==1&&!is_dop) agrpayment_id = Kreditpayment.findOrCreateByKredit_idAndPaydate(oKredit.id,paydate).fillfromPayrequest(this,oKredit.getvRate(),summa).csiSetAdmin(initiator).updatepaiddata(summa,2,is_dop,paydate).computeModstatus().save()?.id?:0
      else if (paytype==1&&is_dop) agrpayment_id = Kreditpayment.findOrCreateByKredit_idAndPaydate(oKredit.id,paydate).fillfromPayrequestDop(this,oKredit.getvRate(),summa).csiSetAdmin(initiator).updatepaiddata(summa,2,is_dop,paydate).computeModstatus().save()?.id?:0
      else if (paytype==2&&oKredit.kredtype in [2,4]&&!is_dop) Kreditline.findOrCreateByKredit_idAndPaydateAndSummarubAndModstatus(oKredit.id,paydate,summa,0).fillfromPayrequest(this,oKredit.getvRate()).csiSetAdmin(initiator).csiSetModstatus(1).save()
      else if (paytype==2&&oKredit.kredtype==1&&!is_dop) oKredit.csiSetStartdate(paydate).save()
    }
    this
  }

  Payrequest csiSetPaytransfer(_request, Taskpay _taskpay, sComment){
    paycat = _request.paycat
    paytype = _request.paytype

    fromcompany_id = _request.fromcompany_id?:0
    fromcompany = Company.get(fromcompany_id)?.name?:''    
    frominn = Company.get(fromcompany_id)?.inn?:''    
    oktmo = Company.get(fromcompany_id)?.oktmo?:''
    bankaccount_id = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(fromcompany_id,1,_request.frombank,1)?.id?:0

    tocompany_id = _request.tocompany_id?:0
    tocompany = Company.get(tocompany_id)?.name?:''
    toinn = Company.get(tocompany_id)?.inn?:''

    tobankaccount_id = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(tocompany_id,1,_request.tobank,1)?.id?:0
    def oAccount = Bankaccount.get(tobankaccount_id)
    def oBank = Bank.get(oAccount?.bank_id?:'')
    toaccount = oAccount?.schet?:''
    tobankbik = oBank?.id?:''
    tobank = oBank?.name?:''
    tocorraccount = oBank?.coraccount?:''

    paydate = Tools.getDate(_request.paydate)
    summa = _request.summa?:0.0g
    summands = 0.0g
    is_nds = _request.is_nds?:0
    destination = _request.destination?:''

    comment = sComment?:''
    csiSetTaskpay_id(_taskpay.id)
    csiSetModstatus(1)

    this
  }

  String receiveDestination(){
    def agr
    switch(agreementtype_id){
      case 1: agr=License.get(agreement_id); break
      case 2: agr=Space.get(agreement_id); break
      case 3: agr=Kredit.get(agreement_id); break
      case 4: agr=Lizing.get(agreement_id); break
      case 6: agr=Cession.get(agreement_id); break
      case 7: agr=Trade.get(agreement_id); break
      case 8: agr=Service.get(agreement_id); break
      case 9: agr=Smr.get(agreement_id); break
      case 10: agr=Loan.get(agreement_id); break
      case 11: agr=Bankdeposit.get(agreement_id); break
      case 12: agr=Finlizing.get(agreement_id); break
      case 13: agr=Indeposit.get(agreement_id); break
    }
    (agr?.toString()?:'')+(agreementtype_id==3?"Погашение ${is_dop?'процентов':'тела'}":'')
  }

  Payrequest updatesumma(BigDecimal _summa){
    if (is_generate) summa = _summa
    this
  }

  Payrequest updateisDop(Integer _is_dop){
    if (is_generate) is_dop = _is_dop
    this
  }

  Payrequest csiSetPersDop(){
    if (paycat==Payment.PAY_CAT_PERS) is_dop = 1
    this
  }

  Payrequest csiSetDop(){
    is_dop = 1
    this
  }

  Payrequest csiSetPayconfirm(iConfirm){
    is_payconfirm = iConfirm?:0
    this
  }

  Payrequest csiSetFine(){
    is_fine = 1
    this
  }
//////////////////////////////////////////////////////////// 
  Payrequest setGeneralData(_request){
    paydate = _request.payrequest_paydate
    summa = _request.summa
    summands = _request.summands?:0
    is_nds = _request.is_nds?:0
    is_dop = _request.is_dop?:0
    is_fine = _request.is_fine?:0
    destination = _request.destination?:''
    this
  }
/////////////////////////////////////////////////////////// 
  Payrequest updateTocompany(_request){
    if(paytype in [1,3] && modstatus==1 && (paycat==Payment.PAY_CAT_AGR || paycat==Payment.PAY_CAT_OTHER || paycat==Payment.PAY_CAT_ORDER)){
      tocompany_id = _request.tocompany_id?:0
      tocompany = Company.get(tocompany_id)?.name?:''
      toinn = Company.get(tocompany_id)?.inn?:''
      tobankbik = _request.tobank?:''
      tobank = Bank.get(tobankbik?:'')?.name?:''
      tocorraccount = Bank.get(tobankbik?:'')?.coraccount?:''
      def oAccount = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(tocompany_id,1,tobankbik,1)
      toaccount = oAccount?.schet?:''
      tobankaccount_id = oAccount?.id?:0
    }
    this
  }

  Payrequest csiSetDestination(String _dest){
    if(paytype in [1,3] && modstatus==1){
      destination = _dest?:''
    }
    this
  }

  Payrequest csiSetSumma(BigDecimal _summa){
    if(paytype in [1,3] && modstatus<=1){
      summa = _summa
      comission = computeComission()
      subcomission = computeSubComission()
      midcomission = computeMidComission()
      supcomission = computeSupComission()
    }
    this
  }

  Payrequest csiSetPayrequest(hsInrequest){
    fromcompany_id = hsInrequest.fromcompany_id?:0
    fromcompany = Company.get(fromcompany_id)?.name?:''    
    frominn = Company.get(fromcompany_id)?.inn?:''    
    oktmo = Company.get(fromcompany_id)?.oktmo?:''

    tocompany_id = hsInrequest.tocompany_id?:0
    tocompany = Company.get(tocompany_id)?.name?:''
    toinn = Company.get(tocompany_id)?.inn?:''

    paycat = hsInrequest.paycat
    paytype = hsInrequest.paytype
    is_dop = 0

    if(paycat==Payment.PAY_CAT_AGR || paycat==Payment.PAY_CAT_OTHER || paycat==Payment.PAY_CAT_ORDER){
      tobankbik = hsInrequest.tobank?:''
      tobank = Bank.get(tobankbik?:'')?.name?:''
      tocorraccount = Bank.get(tobankbik?:'')?.coraccount?:''
      def oAccount = Bankaccount.findByCompany_idAndModstatusAndBank_idAndTypeaccount_id(tocompany_id,1,tobankbik,1)
      toaccount = oAccount?.schet?:''
      tobankaccount_id = oAccount?.id?:0
    }

    if(paycat==Payment.PAY_CAT_AGR){
      csiSetAgreementData(hsInrequest)
    } else if(paycat==Payment.PAY_CAT_PERS){
      pers_id = hsInrequest?.pers_id?:0

      if(pers_id){
        def oPersaccount = Persaccount.findByPers_idAndIs_mainAndModstatus(pers_id,hsInrequest.card,1)
        if(oPersaccount){
          tobankbik = oPersaccount?.bank_id?:0
          tobank = Bank.get(tobankbik?:'')?.name?:''
          tocorraccount = Bank.get(tobankbik?:'')?.coraccount?:''
          toaccount = oPersaccount?.paccount
        }
      }
    } else if(paycat==Payment.PAY_CAT_BUDG){
      if(paytype==1)
        tax_id = hsInrequest.totax_id
      else if(paytype==2)
        tax_id = hsInrequest.fromtax_id
    }

    paydate = Tools.getDate(hsInrequest.paydate)
    summa = hsInrequest.summa?:0.0g
    summands = hsInrequest.summands?:0.0g
    payway = hsInrequest.payway?1:0
    destination = hsInrequest.destination?:''
    platperiod = hsInrequest.platperiod?:platperiod

    comment = hsInrequest.comment?:''
    client_id = hsInrequest.client_id?:0
    subclient_id = !client_id?0:hsInrequest.subclient_id?:0
    tagcomment = hsInrequest.tagcomment?:''
    agent_id = hsInrequest.agent_id?:0
    agentagr_id = hsInrequest.agentagr_id?:0
    is_clientcommission = hsInrequest.is_clientcommission?:0
    is_midcommission = is_clientcommission?0:hsInrequest.is_midcommission?:0
    is_nds = hsInrequest.is_nds?:0

    if(paytype in [2,8,9]) {
      csiSetModstatus(2)
    } else if (paytype==11){
      csiSetModstatus(3)
    }

    this
  }

  private void csiSetAgreementData(hsInrequest){
    agreement_id = hsInrequest.agreement_id?:0
    agreementtype_id = hsInrequest.agreementtype_id?:0

    def oAgreement
    switch(agreementtype_id){
      case 1: oAgreement=License.get(agreement_id); break
      case 2: oAgreement=Space.get(agreement_id); is_dop = hsInrequest?.is_dopmain?:0; break
      case 3: oAgreement=Kredit.get(agreement_id); is_dop = hsInrequest.is_dop?:0; is_fine = hsInrequest.is_fine?:0; break
      case 4: oAgreement=Lizing.get(agreement_id); break
      case 5: oAgreement=Agentagr.get(agreement_id); break
      case 6: oAgreement=Cession.get(agreement_id); break
      case 7: oAgreement=Trade.get(agreement_id); break
      case 8: oAgreement=Service.get(agreement_id); break
      case 9: oAgreement=Smr.get(agreement_id); break
      case 10: oAgreement=Loan.get(agreement_id); break
      case 11: oAgreement=Bankdeposit.get(agreement_id); is_dop = hsInrequest.is_dop?:0; break
      case 12: oAgreement=Finlizing.get(agreement_id); is_dop = hsInrequest.is_com?:0; break
      case 13: oAgreement=Indeposit.get(agreement_id); break
    }
    if(oAgreement)
      agreementnumber = agreementtype_id==5?oAgreement.name:(oAgreement.anumber + ' от ' + String.format('%td.%<tm.%<tY',oAgreement.adate))
  }

  Payrequest csiSetInitiator(iInitiator){
    initiator = iInitiator?:0
    this
  }

  Payrequest csiSetClientadmin(iAdmin){
    clientadmin = iAdmin?:0
    this
  }

  Payrequest csiSetTagadmin(iAdmin){
    tagadmin = iAdmin?:0
    this
  }

  Payrequest csiSetBankaccount_id(iBankaccount_id){
    bankaccount_id=iBankaccount_id?:0
    this
  }

  Payrequest csiSetPayrequestTag(hsInrequest,bIsCanTag,iAdmin){
    if(bIsCanTag&&paytype<=3){
      if(expensetype_id!=hsInrequest?.expensetype_id) csiSetTagadmin(iAdmin)
      project_id = hsInrequest?.project_id?:0
      expensetype_id = hsInrequest?.expensetype_id?:0
      car_id = !Expensetype.get(expensetype_id)?.is_car?0:hsInrequest.car_id?:0
      tagcomment = hsInrequest?.tagcomment?:''
      client_id = hsInrequest?.client_id?:0
      subclient_id = !client_id?0:hsInrequest?.subclient_id?:0
    } else if (is_bankmoney){
      project_id = 0
      expensetype_id = 0
      car_id = 0
      tagcomment = ''
    }
    this
  }

  Payrequest setDetailData(_request){
    if (paytype==2||modstatus==0){
      csiSetAgreementData(_request)
      tax_id = _request.tax_id?:0
      comment = _request.comment?:''
    }
    destination = _request.destination?:''
    is_bankmoney = _request.is_bankmoney?:0
    this
  }

  Payrequest csiSetPayrequestTag(hsInrequest){
    csiSetPayrequestTag(hsInrequest,true,0)
  }

  Payrequest setClientData(_request){
    is_bankmoney = _request.is_bankmoney?:0
    if (!confirmstatus&&paytype in [1,2,3,5,10,11]){
      client_id = deal_id||clientcommission>0?client_id:_request.client_id?:0
      subclient_id = _request.subclient_id?:0
    }
    if (!confirmstatus&&paytype in [1,2,3,5,10,11]&&!is_bankmoney){
      percenttype = is_clientcommission||is_midcommission?0:_request.percenttype?:0
      compercent = is_clientcommission||is_midcommission?0d:_request.compercent?_request.compercent.toDouble():0d
      subcompercent = !subclient_id||paytype==1?0d:_request.subcompercent?_request.subcompercent.toDouble():0d
      midpercent = is_clientcommission||is_midcommission||!Client.get(subclient_id)?.is_middleman?0d:_request.midpercent?_request.midpercent.toDouble():0d
      supcompercent = is_clientcommission||is_midcommission||!Client.get(subclient_id)?.is_clientcomm?0d:_request.supcompercent?_request.supcompercent.toDouble():0d
      comission = computeComission()
      subcomission = computeSubComission()
      midcomission = computeMidComission()
      supcomission = computeSupComission()
    } else if (is_bankmoney){
      percenttype = 0
      compercent = 0
      subcompercent = 0
      midpercent = 0
      supcompercent = 0
      comission = computeComission()
      subcomission = computeSubComission()
      midcomission = computeMidComission()
      supcomission = computeSupComission()
    }
    if (paytype==5&&client_id&&modstatus==0) csiSetModstatus(2)
    else if (paytype==5&&!client_id&&modstatus==2) csiSetModstatus(0)
    this
  }

  Payrequest csiSetSfactura(_request){
    sfactura = _request.sfactura?:''
    sfacturadate = _request.sfacturadate
    this
  }

  Payrequest setTClientData(_request){
    client_id = deal_id||clientcommission>0?client_id:_request.client_id?:0
    this
  }

  Payrequest cloneRequest(){
    Payrequest newrequest = new Payrequest()
    newrequest.properties = this.properties
    newrequest.inputdate = new Date()
    newrequest
  }

  Payrequest correctSummas(bdSumma){
    def correctedpercent = bdSumma/summa
    summa = bdSumma
    summands = summands * correctedpercent
    comission = comission * correctedpercent
    subcomission = subcomission * correctedpercent
    clientcommission = clientcommission * correctedpercent
    agentcommission = agentcommission * correctedpercent
    clientdelta = clientdelta * correctedpercent
    payoffsumma = payoffsumma * correctedpercent
    this
  }

  Payrequest csiSetDeal_id(iDealId){
    deal_id = iDealId?:0
    this
  }

  Payrequest csiSetConfirmstatus(iStatus){
    confirmstatus = iStatus?:0
    this
  }

  Payrequest csiSetPayoff(bdPercent){
    if (modstatus==2) {
      payoffperc = bdPercent?bdPercent.toDouble():0d
      payoffsumma = Math.rint(summa * payoffperc) / 100
    }
    this
  }

  Payrequest payRepayment(bdSumma){
    clientcommission += bdSumma
    csiSetModstatus(clientcommission == summa ? 2 : clientcommission>0 ? 1 : 0)
    csiSetExecdate(clientcommission > 0 ? new Date() : null)
    this
  }

  Payrequest updateClientcomission(bdSumma,iAgentagrId){
    clientcommission += bdSumma
    agentagr_id = clientcommission == 0 ? 0 : agentagr_id ?: iAgentagrId
    comission = computeComission()
    subcomission = computeSubComission()
    this
  }

  Payrequest payAgentpayment(bdSumma){
    agentcommission += bdSumma
    csiSetModstatus(agentcommission == summa ? 2 : agentcommission>0 ? 1 : 0)
    csiSetExecdate(agentcommission > 0 ? new Date() : null)
    this
  }

  Payrequest updateAgentcomission(bdSumma){
    agentcommission += bdSumma
    comission = computeComission()
    subcomission = computeSubComission()
    this
  }

  Payrequest updatecompany(iCompanyId){
    def oFromCompany = Company.get(iCompanyId)
    fromcompany = oFromCompany.name
    frominn = oFromCompany.inn
    fromcompany_id = oFromCompany.id
    this
  }

  BigDecimal computeComission(){
    return (summa - clientcommission - agentcommission) * (!percenttype ? (compercent / 100) : (compercent / (100 - compercent)))
  }

  BigDecimal computeSubComission(){
    return (summa - clientcommission - agentcommission) * (!percenttype ? (subcompercent / 100) : (subcompercent / (100 - compercent)))
  }

  BigDecimal computeMidComission(){
    return (summa - clientcommission - agentcommission) * (!percenttype ? (midpercent / 100) : (midpercent / (100 - midpercent)))
  }

  BigDecimal computeSupComission(){
    return (summa - clientcommission - agentcommission) * (!percenttype ? (supcompercent / 100) : (supcompercent / (100 - supcompercent)))
  }

  BigDecimal computeClientdelta(){
    return (confirmstatus?0.0g:paytype in [1,3,4,7,11]?-clientdelta:clientdelta)
  }

  BigDecimal computeIncome(){
    return (paytype in [2,5,8,9,10]&&!is_clientcommission&&!is_midcommission?clientdelta:0.0g)
  }

  BigDecimal computeOutlay(){
    return (paytype in [1,7,11]&&!is_clientcommission&&!is_midcommission?clientdelta:0.0g)
  }

  static Integer getLastComissionId(){
    return Payrequest.findByPaytypeAndModstatusGreaterThan(9,-1,[sort:'id',order:'desc'])?.id?:0
  }

  void changeDCSaldo(){
    if (paytype==1&&paycat==3&&is_dop==1&&isDirty('modstatus')&&getPersistentValue('modstatus')<2&&modstatus==2) Holding.findByName('dopcardsaldo').changeSaldo(summa).save(failOnError:true)
    else if (paytype==1&&paycat==3&&is_dop==1&&isDirty('modstatus')&&getPersistentValue('modstatus')==2&&modstatus<=2) Holding.findByName('dopcardsaldo').changeSaldo(-summa).save(failOnError:true)
    else if (!isDirty('modstatus')&&modstatus==2&&paytype==9) Holding.findByName('dopcardsaldo').changeSaldo(getPersistentValue('summa')?:0).changeSaldo(-summa).save(failOnError:true)
  }
}