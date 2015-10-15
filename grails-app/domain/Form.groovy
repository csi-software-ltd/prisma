class Form {
  static mapping = {
    version false
  }

  Integer id
  String name
  String fullname
  
  def csiSetForm(sName,sFullname){
    name=sName
    fullname=sFullname
    this
  }
}