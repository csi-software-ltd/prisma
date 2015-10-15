class Spacetype {
  static mapping = {
    version false
  }

  Integer id
  String name
  String fieldname = ''
  String sectionname = ''

  def csiSetSpacetype(hsInrequest){
    name=hsInrequest?.name?:''
    this
  }

}