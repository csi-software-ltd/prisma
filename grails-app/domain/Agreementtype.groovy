class Agreementtype {
  static mapping = {
    version false
    sort sortorder: "asc"
  }

  Integer id
  String name
  String name2
  String icon
  String checkfield
  String action
  String companyaction
  Integer sortorder
  
  def csiSetAgreementtype(hsInrequest){
    name = hsInrequest?.name?:''
    sortorder = hsInrequest?.sortorder?:0
  
    this
  }  

}