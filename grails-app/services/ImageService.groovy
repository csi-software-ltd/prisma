class ImageService {

  boolean transactional = false
  static scope = "request"

  def transient m_oController=null

  /////////////////////////////////////////////////////////////////////////////////////////  
  private checkInit(){
    if(m_oController==null)
      log.debug("Does not set controller object in ImageService. Call imageService.init(this,....")
    return (m_oController==null)
  }
    
  /////////////////////////////////////////////////////////////////////////////////////////
  def init(oController){
    m_oController=oController
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  def rawUpload(sName, bValidateOnly=false) { //!
    def hsRes=[fileid:null,error:1] // 1 - UNSPECIFIC ERROR
    if(checkInit())
      return hsRes

    def fileImage
    try {
      fileImage= m_oController.request.getFile(sName)
    } catch (Exception e) {}

    if(!fileImage)
      return hsRes

    //RESERVED
    if(!fileImage.originalFilename){
      hsRes.error = 2
      return hsRes
    }
    //CHECK CONTENT TYPE  //,"image/bmp","image/gif" - prohibited

    if(!(fileImage.getContentType() in ["image/pjpeg","image/jpeg","image/png","image/x-png","application/pdf","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword"])){
      hsRes.error = 3
      return hsRes
    }

    if(bValidateOnly){
      hsRes.error = (hsRes.error==1?0:hsRes.error)
      return hsRes
    }

    try{
      hsRes.fileid = new Picture().updateData(fileImage)?.save(flush:true,failOnError:true)?.id
      hsRes.error = 0
    } catch (Exception e) {
      log.debug("Cannot save picture\n"+e.toString())
      throw e
    }

    return hsRes
  }

}