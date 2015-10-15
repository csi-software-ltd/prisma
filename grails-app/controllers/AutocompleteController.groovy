import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AutocompleteController {
  def requestService

  def beforeInterceptor = [action:this.&checkUser]

  def checkUser() {
    if(session?.user?.id!=null){
    }else{
      response.sendError(401)
      return false;
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Main >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def companyname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id
      }
    }
    render hsRes as JSON
  }
  
  def companyinn_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByInnIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.inn
        hsRes.data << it.id+';'+it.name
      }
    }
    render hsRes as JSON
  }
  
  def companyname_full_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id + ';' + it.inn + ';' + it.oktmo
      }
      Company.findAllByInnIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id + ';' + it.inn + ';' + it.oktmo
      }
    }
    render hsRes as JSON
  }

  def companyname_ext_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlikeAndIs_holding('%'+hsRes.query+'%',0,[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id+';'+it.taxoption_id 
      }
      Company.findAllByInnIlikeAndIs_holding(hsRes.query+'%',0,[max:10]).each{
        hsRes.suggestions << it.inn
        hsRes.data << it.id+';'+it.taxoption_id 
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def companyname_bank_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlikeAndIs_holdingAndIs_bank('%'+hsRes.query+'%',0,1,[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id+';'+it.taxoption_id 
      }
      Company.findAllByInnIlikeAndIs_holdingAndIs_bank(hsRes.query+'%',0,1,[max:10]).each{
        hsRes.suggestions << it.inn
        hsRes.data << it.id+';'+it.taxoption_id 
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def companyname_int_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlikeAndIs_holding('%'+hsRes.query+'%',1,[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id+';'+it.taxoption_id 
      }
      Company.findAllByInnIlikeAndIs_holding(hsRes.query+'%',1,[max:10]).each{
        hsRes.suggestions << it.inn
        hsRes.data << it.id+';'+it.taxoption_id 
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def space_arendator_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlikeAndIs_holding('%'+hsRes.query+'%',1,[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.legaladr
      }
      Company.findAllByInnIlikeAndIs_holding(hsRes.query+'%',1,[max:10]).each{
        hsRes.suggestions << it.inn
        hsRes.data << it.legaladr
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def space_owner_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlikeAndIs_holding(hsRes.query+'%',0,[max:10]).each{
        hsRes.suggestions << it.name
      }
      Company.findAllByInnIlikeAndIs_holding(hsRes.query+'%',0,[max:10]).each{
        hsRes.suggestions << it.inn
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def space_arendodatel_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
      Company.findAllByInnIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.inn
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def trade_supplier_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
      Company.findAllByInnIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.inn
      }
    }
    render hsRes as JSON
  }

  def bankname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Bank.findAllByNameIlike('%'+hsRes.query+'%',[max:10,sort:'is_local',order:'desc']).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id + ';' + it.coraccount + ';' + it.ibankterm
      }
    }
    render hsRes as JSON
  }

  def banknamebik_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Bank.findAllByNameIlike('%'+hsRes.query+'%',[max:10,sort:'is_local',order:'desc']).each{
        hsRes.suggestions << it.name
      }
      Bank.findAllByIdIlike(hsRes.query+'%',[max:10,sort:'is_local',order:'desc']).each{
        hsRes.suggestions << it.id
      }
    }
    render hsRes as JSON
  }

  def banknameholding_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      new BankaccountSearch().csiFindBanknames(hsRes.query).each{
        hsRes.suggestions << it.bankname
      }
    }
    render hsRes as JSON
  }

  def okved_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Okved.findAllByIdIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.id
        hsRes.data << it.id
      }
    }
    render hsRes as JSON
  }

  def okvedname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Okved.findAllByIdIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.toString()
      }
    }
    render hsRes as JSON
  }

  def bik_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Bank.findAllByIdIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.id
        hsRes.data << it.name + ';' + it.coraccount + ';' + it.ibankterm
      }
    }
    render hsRes as JSON
  }
  
  def kpp_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Taxinspection.findAllByIdIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.id + '01001'
        hsRes.data << it.id
      }
    }
    render hsRes as JSON
  }  

  def kreditnumber_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Kredit.findAllByAnumberIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.anumber
      }
    }
    render hsRes as JSON
  }

  def agentagr_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Agentagr.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
    }
    render hsRes as JSON
  }

  def gd_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByGdIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.gd
      }
      hsRes.suggestions.unique()
    }
    render hsRes as JSON
  }

  def district_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      Taxinspection.findAllByDistrictIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.district
      }
      hsRes.suggestions.unique()
    }
    render hsRes as JSON
  }

  def persname_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Pers.findAllByShortnameIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.shortname
        hsRes.data << it.id
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def compholdername_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      new CompholderSearch().compholderAutocomplete(hsRes.query).each{
        hsRes.suggestions << it.shortname
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def persname_nouser_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      new Pers().csiGetPersNoUser(hsRes.query).each{      
        hsRes.suggestions << it.shortname
        hsRes.data << it.id+';'+it.perstype
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def executor_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      User.findAllByCashaccessGreaterThanAndModstatusAndNameIlike(0,1,hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }
  
  def kbk_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Kbk.findAllByKbkIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.kbk        
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }
  
  def persname_full_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Pers.findAllByShortnameIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.shortname
        hsRes.data << it.id+';'+it.inn
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def buhcompanyname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      new Salarycomp().csiSelectCompanynames(hsRes.query).each{
        hsRes.suggestions << it.companyname
      }
    }
    render hsRes as JSON
  }

  def buhpersname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      new Salarycomp().csiSelectPersnames(hsRes.query).each{
        hsRes.suggestions << it.fio
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }
  
  def clientname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Client.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id
      }
    }
    render hsRes as JSON
  }
  
  def fromcompanyname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Payment.findAll('FROM Payment WHERE fromcompany LIKE :fromcompany GROUP BY fromcompany',[fromcompany:'%'+hsRes.query+'%'],[max:10]).each{
        hsRes.suggestions << it.fromcompany        
      }
    }
    render hsRes as JSON
  }

  def tocompanyname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Payment.findAll('FROM Payment WHERE tocompany LIKE :tocompany GROUP BY tocompany',[tocompany:'%'+hsRes.query+'%'],[max:10]).each{
        hsRes.suggestions << it.tocompany        
      }
    }
    render hsRes as JSON
  }

  def agentname_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      Agent.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
    }
    render hsRes as JSON
  }

  def usergroup_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      Usergroup.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
    }
    render hsRes as JSON
  }

  def companytaskpay_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      new Company().csiFindCompanyTaskpay('%'+hsRes.query+'%',10).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id
      }
    }
    render hsRes as JSON
  }  
  def executortaskpay_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      new User().csiFindExecutorTaskpay('%'+hsRes.query+'%',10).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id
      }
    }
    render hsRes as JSON
  }

  def loanlender_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      Company.findAllByNameIlike('%'+hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
      new CompholderSearch().compholderAutocomplete(hsRes.query).each{
        hsRes.suggestions << it.shortname
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def loanclient_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      Company.findAllByNameIlike('%'+hsRes.query+'%',[max:5]).each{
        hsRes.suggestions << it.name
      }
      Pers.findAllByShortnameIlike(hsRes.query+'%',[max:5]).each{
        hsRes.suggestions << it.shortname
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def expensetype_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      new ExpensetypeSearch().csiGetList(hsRes.query,requestService.getIntDef('user_id',0),10).each{
        hsRes.suggestions << it.toString()
        hsRes.data << it.id
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }
  
  def login_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      User.findAllByLoginIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.login
        hsRes.data << it.id
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }
  
  def expensetype1_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Expensetype1.findAllByNameIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }
  
  def department_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Department.findAllByNameIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }

  def composition_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Composition.findAllByNameIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON
  }
  
  def outsource_autocomplete = {
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Outsource.findAllByNameIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
        hsRes.data << it.id
      }
    }
    if(!hsRes.suggestions){
      response.sendError(404)
      return
    }
    render hsRes as JSON  
  }

  def bankdirpers_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      new Pers().csiFindBankdirnames(hsRes.query).each{
        hsRes.suggestions << it.fullname
      }
    }
    render hsRes as JSON
  }
}