class Company {
  def searchService
  
  static mapping = {
    version false
  }
  static constraints = {
    opendate(nullable:true)
    namedate(nullable:true)
    adrdate(nullable:true)
    capitaldate(nullable:true)
    reregdate(nullable:true)
    reqdate(nullable:true)
    picdate(nullable:true)
    ldocdate(nullable:true)
  }
  private enum Historyfields {
    LEGALNAME, LEGALADR, TAXINSPECTION_ID, OKTMO, OKATO, TEL, TAXOPTION_ID, ACTIVITYSTATUS_ID, KPP, CAPITAL, NAMEDATE, ADRDATE, CAPITALDATE, CAPITALSECURE, CAPITALPAID, PFRFREG, FSSREG
  }

  Integer id
  String name
  String legalname
  Date namedate
  Integer is_holding
  Integer is_bank
  Integer cgroup_id = 0
  Integer visualgroup_id = 1
  String inn
  String kpp
  String okogu
  String okvedmain = ''
  String gd = ''
  String city
  String ogrn
  String oktmo
  String okato
  String okpo
  Date opendate
  Date inputdate = new Date()
  Date reregdate
  String regauthority
  String legaladr
  Date adrdate  
  String postadr
  String tel
  String smstel
  String smstel2 = ''
  String email
  String emailpassword
  String www = ''
  String taxinspection_id
  Integer taxoption_id  
  Integer form_id
  Integer is_subarenda
  Integer is_sublizing
  Long cost = 0l
  Long buycost = 0l
  Long salecost = 0l
  Long capital = 0l
  Date capitaldate
  Integer capitalsecure = 1
  Integer capitalpaid = 0
  String contactinfo = ''
  String comment
  Integer modstatus
  Integer activitystatus_id = 1
  Integer is_taxdebt = 0
  Integer is_dirchange = 0
  String color = 'transparent'
  Integer colorfill = 1
  String pfrfreg
  String fssreg
  Long responsible1 = 0
  Long responsible2 = 0
  Date moddate = new Date()
  Integer tagproject = 0
  Integer tagclient = 0
  Integer tagexpense = 0
  String tagcomment = ''
  Integer outsource_id
  Integer outsourceprice = 0
  Integer is_req = 0
  Date reqdate
  Integer is_pic = 0
  Date picdate
  Integer is_ldoc = 0
  Date ldocdate
  
  def transient admin_id

  def afterInsert(){
    new Companyhist(company_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    moddate = new Date()
    if(isHaveDirty()) new Companyhist(company_id:id,admin_id:admin_id).setData(properties).save()
  }

  Company setData(_request){
    name = _request.cname
    legalname = _request.legalname?:_request.cname
    namedate = _request.namedate
    is_holding = _request.is_holding?:0
    is_bank = is_holding?0:_request.is_bank?:0
    cgroup_id = _request.cgroup_id?:0
    visualgroup_id = _request.visualgroup_id?:1
    inn = _request.inn
    kpp = _request.kpp?:''
    okogu = _request.okogu?:''
    city = _request.city?:''
    ogrn = _request.ogrn?:''
    oktmo = _request.oktmo?:''
    okato = _request.okato?:''
    okpo = _request.okpo?:''    
    reregdate = _request.reregdate
    regauthority = _request.regauthority?:''
    opendate = _request.opendate?:null
    legaladr = _request.legaladr?:''
    adrdate = _request.adrdate
    postadr = _request.postadr?:_request.legaladr?:''
    tel = _request.tel?:''
    smstel = _request.smstel?:''
    email = _request.email?:''
    emailpassword = _request.emailpassword?:''
    www = _request.www?:''
    taxinspection_id = _request.taxinspection_id
    taxoption_id = _request.taxoption_id
    form_id = _request.form_id?:0
    is_subarenda = _request.is_subarenda?:0
    is_sublizing = _request.is_sublizing?:0
    capital = _request.capital?:0l
    capitaldate = _request.capitaldate
    capitalsecure = _request.capitalsecure?:1
    capitalpaid = _request.capitalsecure!=2?0:_request.capitalpaid?:1
    comment = _request.comment?:''
    activitystatus_id = _request.activitystatus_id?:1
    is_dirchange = _request.is_dirchange?:0
    pfrfreg = _request.pfrfreg?:''
    fssreg = _request.fssreg?:''
    responsible1 = !is_holding?0:_request.responsible1?:0
    responsible2 = !is_holding?0:_request.responsible2?:0
    is_req = is_holding?0:_request.is_req?:0
    reqdate = is_holding||!is_req?null:_request.reqdate?:null
    is_pic = is_holding?0:_request.is_pic?:0
    picdate = is_holding||!is_pic?null:_request.picdate?:null
    is_ldoc = is_holding?0:_request.is_ldoc?:0
    ldocdate = is_holding||!is_ldoc?null:_request.ldocdate?:null
    this
  }

  Company csiSetTagData(_request){
    tagproject = _request.tagproject?:tagproject
    tagclient = _request.tagclient?:tagclient
    //tagexpense = _request.tagexpense?:Tools.getIntVal(Dynconfig.findByName('company.tags.default.expense.value')?.value,0)
    tagcomment = _request.tagcomment?:tagcomment
    this
  }

  Company computeModstatus(){
    modstatus = Activitystatus.get(activitystatus_id)?.is_close==1?0:1
    this
  }

  Company csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Company csiSetGd(sGd){
    gd = sGd?:''
    this
  }
  
  Company csiSetColor(sColor){
    color = sColor?:'transparent'
    this
  }

  Company csiSetColorFill(iFill){
    colorfill = iFill?:0
    this
  }

  Company addtoproject(iProjectId){
    new Cproject(company_id:id,project_id:iProjectId).save(failOnError:true)
    this
  }

  Company removefromproject(iProjectId){
    Cproject.findByCompany_idAndProject_id(id,iProjectId)?.delete(flush:true)
    this
  }

  Company addtookved(sOkvedId,dModdate,sComment){
    new Compokved(company_id:id,okved_id:sOkvedId,is_main:okvedmain?0:1,moddate:dModdate,comments:sComment).save(failOnError:true)
    okvedmain = okvedmain?:sOkvedId
    this
  }

  Company removefromokved(sOkvedId){
    okvedmain = Compokved.findByCompany_idAndOkved_id(id,sOkvedId)?.delete(flush:true)?.is_main?'':okvedmain
    this
  }

  Company csiSetMainOkved(sOkvedId){
    okvedmain = Compokved.findByCompany_idAndOkved_id(id,sOkvedId)?.csiSetMain()?.save(failOnError:true)?.okved_id?:okvedmain
    this
  }

  Company csiSetIsTaxdebt(iStatus){
    is_taxdebt = iStatus?:0
    this
  }
  
  Company csiSetOutsource(_request){
    outsource_id = _request.outsource_id?:0
    outsourceprice = _request.outsourceprice?:0
    buycost = _request.buycost?:0l
    salecost = _request.salecost?:0l
    this
  }

  Boolean isHaveDirty (){ return Company.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }
  
  def csiFindBankAccounts(sBankId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, company.id as id"
    hsSql.from='company, bankaccount'
    hsSql.where="company.id=bankaccount.company_id AND company.is_holding=1"+
                " AND bankaccount.modstatus=1"+
                ((sBankId!='')?' AND bankaccount.bank_id=:sBankId':'')
    hsSql.order="company.name asc"      
   

    if(sBankId!='')
      hsString['sBankId']=sBankId

   return searchService.fetchDataByPages(hsSql,null,null,null,hsString,
      null,null,iMax,iOffset,'company.id',true,Company.class)
  }
  
  def csiFindCompanyTaskpay(sName,iMax){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, company.id AS id"
    hsSql.from='company, taskpay'              
    hsSql.where="taskpay.company_id = company.id"+                
                ((sName!='')?' AND name like concat("%",:name,"%")':'')
    hsSql.order="name asc"
    hsSql.group="company.id"
  
    if(sName!='')
      hsString['name']=sName             

    return searchService.fetchData(hsSql,null,null,hsString,null,Company.class,iMax)
  }
}
