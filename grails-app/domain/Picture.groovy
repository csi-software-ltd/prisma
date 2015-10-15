class Picture {

  static constraints = {
  }
  static mapping = {
    version false
  }

  byte[] filedata
  String mimetype = 'image/jpeg'

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	Picture updateData (_file){
		filedata = _file?.getBytes()
    mimetype = _file?.getContentType()?:'image/jpeg'
		this
	}

}