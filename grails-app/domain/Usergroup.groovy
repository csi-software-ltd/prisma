class Usergroup {
  def searchService
  static constraints = {
    description(nullable:true)
  }
  static mapping = {
    version false
  }
  Integer id
  String name = ''
  String menu = ''
  String description = ''
  Integer department_id = 0
  Integer visualgroup_id = 0
  
  Integer is_superuser = 0
  Integer is_panel = 0
  Integer is_users = 0
  Integer is_pers = 0
  Integer is_persedit = 0
  Integer is_persinsert = 0
  Integer is_persaccount = 0
  Integer is_persaccountedit = 0 
  Integer is_catalog = 0
  Integer is_groupmanage = 0
  Integer is_agr = 0
  Integer is_license = 0
  Integer is_licenseedit = 0
  Integer is_company = 0
  Integer is_companyedit = 0
  Integer is_companyinsert = 0
  Integer is_companycard = 0
  Integer is_companyaccount = 0
  Integer is_companyaccountedit = 0
  Integer is_companystaff = 0
  Integer is_companystaffedit = 0
  Integer is_companyrequisit = 0
  Integer is_project = 0
  Integer is_projectedit = 0
  Integer is_arenda = 0
  Integer is_arendaedit = 0
  Integer is_prolongpermit = 0
  Integer is_prolongwork = 0
  Integer is_lizing = 0
  Integer is_lizingedit = 0
  Integer is_lizingpaymentedit = 0
  Integer is_trade = 0
  Integer is_tradeedit = 0
  Integer is_kredit = 0
  Integer is_kreditedit = 0
  Integer is_kreditpaymentedit = 0
  Integer is_kreditclient = 0
  Integer is_kreditinfo = 0
  Integer is_realkredit = 0
  Integer is_cession = 0
  Integer is_cessionedit = 0
  Integer is_cessionpaymentedit = 0
  Integer is_agent = 0
  Integer is_agentedit = 0
  Integer is_service = 0
  Integer is_serviceedit = 0
  Integer is_smr = 0
  Integer is_smredit = 0
  Integer is_loan = 0
  Integer is_loanedit = 0
  Integer is_deposit = 0
  Integer is_depositedit = 0
  Integer is_finlizing = 0
  Integer is_finlizingedit = 0
  Integer is_cassa = 0
  Integer is_cashdep = 0
  Integer is_salary = 0
  Integer is_salaryavans = 0
  Integer is_salaryedit = 0
  Integer is_salarybuh = 0
  Integer is_salarybuhedit = 0
  Integer is_salaryoff = 0
  Integer is_salaryoffedit = 0
  Integer is_salarynal = 0
  Integer is_salarynaledit = 0
  Integer is_salaryalldep = 0
  Integer is_salaryapprove = 0
  Integer is_rep_allsalary = 0
  Integer is_rep_dirsalary = 0
  Integer is_rep_agentagrprofit = 0
  Integer is_rep_clientpay = 0
  Integer is_rep_documents = 0
  Integer is_rep_service = 0
  Integer is_rep_cash = 0
  Integer is_rep_booh = 0
  Integer is_rep_payment = 0
  Integer is_task = 0
  Integer is_taskmy = 0
  Integer is_taskall = 0
  Integer is_taskpay = 0
  Integer is_taskpayall = 0
  Integer is_enquiry = 0
  Integer is_enquiryedit = 0
  Integer is_payment = 0  
  Integer is_bankedit = 0
  Integer is_bankinsert = 0  
  Integer is_taxedit = 0
  Integer is_taxinsert = 0
  Integer is_okvedinsert = 0
  Integer is_oktmoinsert = 0
  Integer is_kbkinsert = 0
  Integer is_expenseedit = 0
  Integer is_department = 0
  Integer is_departmentedit = 0
  Integer is_spacediredit = 0
  Integer is_holidayedit = 0
  Integer is_agrtypeedit = 0
  Integer is_positionedit = 0
  Integer is_visualgroup = 0
  Integer is_config = 0
  Integer is_cgroup = 0
  
  Integer is_useredit = 0
  Integer is_userinsert = 0
  Integer is_usergroupedit = 0
  Integer is_usergroupinsert = 0  
  Integer is_usergroupenter = 0
  
  Integer is_payplan = 0
  Integer is_payplanedit = 0
  Integer is_payplantask = 0
  Integer is_payplanexec = 0
  Integer is_payaccept = 0
  Integer is_payordering = 0
  Integer is_paysaldo = 0
  Integer is_viewbudgpayplantask = 0
  Integer is_viewkredpayplantask = 0
  Integer is_viewrentpayplantask = 0
  Integer is_viewgnrlpayplantask = 0
  Integer is_client = 0
  Integer is_clientedit = 0
  Integer is_clientpayment = 0
  Integer is_clientpaymentedit = 0
  Integer is_clientpaynew = 0
  Integer is_payrequestdelete = 0
  Integer is_payt = 0
  Integer is_paytedit = 0
  Integer is_paynalog = 0
  Integer is_paynalogedit = 0
  Integer is_payproject = 0
  Integer is_dopcardpayment = 0

  Integer is_payedit = 0
  Integer is_paytag = 0    

  def csiFindUsergroup(sGroupName,iDepartmentId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='usergroup'
    hsSql.where="is_superuser!=1"+
      ((sGroupName)?' AND name like concat("%",:gname,"%")':'')+
      ((iDepartmentId>-100)?' AND department_id=:department_id':'')
    hsSql.order="name asc"

    if(sGroupName)
      hsString['gname'] = sGroupName
    if(iDepartmentId>-100)
      hsLong['department_id'] = iDepartmentId

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'id',true,Usergroup.class)
  }

  def csiSetData(hsInrequest){
    name = hsInrequest?.name?:''
    description = hsInrequest?.description?:''
    department_id = hsInrequest?.department_id?:0
    visualgroup_id = hsInrequest?.visualgroup_id?:0
    
    this
  }

}
