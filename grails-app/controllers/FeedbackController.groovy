import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class FeedbackController {
  def requestService
  def imageService

  final String FSUPER = 'is_superuser'

  def beforeInterceptor = [action:this.&checkUser]

  def checkUser() {
    if(session?.user?.id!=null){
      def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
      session.attention_message=oTemp_notification?oTemp_notification.text:null
    }else{
      response.sendError(401)
      return false;
    }
  }

  def checkAccess(iActionId){
    def bDenied = true
    if(session?.user)
      session.user.menu.each{
				if (iActionId==it.id) bDenied = false
	    }
    if (bDenied) {
      response.sendError(403)
      return
	  }
  }

  private Boolean checkSectionPermission(String sField) {
    checkSectionPermission([sField])
  }

  private Boolean checkSectionPermission(lsField) {
    if(!lsField.find{ session.user.group?."$it" }) {
      response.sendError(403)
      return false;
    }
    return true
  }

  private Boolean recieveSectionPermission(String sField) {
    recieveSectionPermission([sField])
  }

  private Boolean recieveSectionPermission(lsField) {
    lsField.find{ session.user.group?."$it" } as Boolean
  }

  private Boolean checkQuestionAccess(_question) {
    if (_question.user_id!=session.user.id) {
      response.sendError(403)
      return false
    }
    return true
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Main >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
	def index() {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.feedlastRequest){
      session.feedlastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.feedlastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.feedsection = requestService.getIntDef('feedsection',0)
    }
    hsRes.isSuper = recieveSectionPermission(FSUPER)

    return hsRes
	}

  def askfilter = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    if (session.feedlastRequest?.fromDetails){
      hsRes.inrequest = session.feedlastRequest
    }
    hsRes.feedtypes = Feedbacktype.list()

    return hsRes
  }

  def questions = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    if (session.feedlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.feedlastRequest
      session.feedlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['feedtype'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.feedlastRequest = hsRes.inrequest
    }
    session.feedlastRequest.feedsection = 0

    hsRes.searchresult = new FeedbackSearch().csiSelectQuestions(hsRes.user.id,0,'','',hsRes.inrequest.feedtype?:0,-100,20,hsRes.inrequest.offset)
    hsRes.ftypes = Feedbacktype.list().inject([:]){map, type -> map[type.id]=type.name;map}

    return hsRes
  }

  def newquestion = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    hsRes.ftypes = Feedbacktype.list()

    return hsRes
  }

  def incertquestion = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['feedbacktype_id'],null,['qtext'])

    if(!hsRes.inrequest.qtext)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
				new Feedback(user_id:hsRes.user.id,qtext:hsRes.inrequest.qtext.replace('\n','<br/>'),feedbacktype_id:hsRes.inrequest.feedbacktype_id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Feedback/incertquestion\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def answerfilter = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    if (session.feedlastRequest?.fromDetails){
      hsRes.inrequest = session.feedlastRequest
    }
    hsRes.feedtypes = Feedbacktype.list()

    return hsRes
  }

  def answers = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    if (session.feedlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.feedlastRequest
      session.feedlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['feedtype','fid'],null,['username','keyword'])
    	hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.feedlastRequest = hsRes.inrequest
    }
    session.feedlastRequest.feedsection = 0

    hsRes.searchresult = new FeedbackSearch().csiSelectQuestions(0,hsRes.inrequest.fid?:0,hsRes.inrequest.username?:'',hsRes.inrequest.keyword?:'',
                                                                 hsRes.inrequest.feedtype?:0,hsRes.inrequest.modstatus?:0,20,hsRes.inrequest.offset)
    hsRes.ftypes = Feedbacktype.list().inject([:]){map, type -> map[type.id]=type.name;map}

    return hsRes
  }

  def question = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    hsRes.question = Feedback.get(requestService.getIntDef('id',0))
    if (!hsRes.question) {
      response.sendError(404)
      return
    }
    if(!recieveSectionPermission(FSUPER)&&!checkQuestionAccess(hsRes.question)) return

    hsRes.ftypes = Feedbacktype.list()
    hsRes.author = Pers.get(User.get(hsRes.question.user_id)?.pers_id?:0)
    hsRes.isSuper = recieveSectionPermission(FSUPER)
    if(!hsRes.isSuper) hsRes.question.readAnswer().save(flush:true)

    return hsRes
  }

  def updatequestion = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20
    hsRes.result=[errorcode:[]]
 
    hsRes.question = Feedback.get(requestService.getIntDef('id',0))
    if (!hsRes.question) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['atext','qtext'])

    def hsData
    imageService.init(this)

    if(recieveSectionPermission(FSUPER)&&!hsRes.inrequest.atext)
      hsRes.result.errorcode<<2
    else if(!recieveSectionPermission(FSUPER)&&!hsRes.inrequest.qtext)
      hsRes.result.errorcode<<3
    if(recieveSectionPermission(FSUPER)&&!hsRes.result.errorcode){
      hsData = imageService.rawUpload('file',true)
      if(hsData.error in [1,3])
        hsRes.result.errorcode<<1
    }

    if(!hsRes.result.errorcode){
      try {
        hsRes.question.setData(hsRes.inrequest,recieveSectionPermission(FSUPER)).csiSetFileId(imageService.rawUpload('file').fileid).computeStatus().save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Feedback/updatequestion\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

  def delete = {
    checkAccess(20)
    if (!checkSectionPermission(FSUPER)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    try {
      Feedback.get(requestService.getIntDef('id',0))?.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Feedback/delete\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def movetofaq = {
    checkAccess(20)
    if (!checkSectionPermission(FSUPER)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20
    hsRes.result=[errorcode:[]]

    hsRes.question = Feedback.get(requestService.getIntDef('id',0))
    if (!hsRes.question) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.question.movetofaq(requestService.getIntDef('status',0)).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Feedback/movetofaq\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def faq = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    session.feedlastRequest = [feedsection:2]

    hsRes.faqlist = Feedback.findAllByModstatus(2,[sort:'qdate',order:'desc'])

    return hsRes
  }

  def faqanswer = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 20

    hsRes.question = Feedback.get(requestService.getIntDef('faq_id',0))
    if (!hsRes.question) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Main <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def showscan = {
    checkAccess(20)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)

    def photo = Picture.get(requestService.getIntDef('id',0))
    if (!photo||(requestService.getStr('code')!=Tools.generateModeParam(photo?.id))) {
      response.sendError(404)
      return
    }

    //render file: photo.filedata, contentType: 'image/jpeg' //Only from grails 2.3    
    response.contentType = photo.mimetype?:'image/jpeg'
    response.outputStream << photo.filedata
    response.flushBuffer()
  }
}